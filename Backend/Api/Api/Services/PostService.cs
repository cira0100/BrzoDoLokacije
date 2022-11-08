﻿using System.Security.Claims;
using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace Api.Services
{
    public class PostService : IPostService
    {
        private readonly MongoClient _client;
        private readonly IMongoCollection<Post> _posts;
        private readonly IHttpContextAccessor _httpContext;
        private readonly IFileService _fileService;
        private readonly ILocationService _locationService;
        private readonly IMongoCollection<User> _users;
        public PostService(IDatabaseConnection settings, IMongoClient mongoClient, IHttpContextAccessor httpContext, IFileService fileService,ILocationService locationService)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _posts = database.GetCollection<Post>(settings.PostCollectionName);
            _users = database.GetCollection<User>(settings.UserCollectionName);
            _httpContext = httpContext;
            _fileService = fileService;
            _locationService = locationService;
        }

        public async Task<PostSend> addPost(PostReceive post)
        {
            Post p = new Post();
            p._id = "";
            p.ownerId = _httpContext.HttpContext.User.FindFirstValue("id");

            p.locationId = post.locationId;
            p.description = post.description;
            p.views = new List<string>();
            p.reports = new List<string>();
            p.ratings = new List<Rating>();
            p.comments = new List<Comment>();
            p.images = new List<Models.File>();

            var folderPath = Path.Combine(Directory.GetCurrentDirectory(), "Files", p.ownerId);
            if (!Directory.Exists(folderPath))
            {
                Directory.CreateDirectory(folderPath);
            }

                foreach (var image in post.images)
            {
                var filename = image.FileName;
                var ext=Path.GetExtension(filename).ToLowerInvariant();
                var name = Path.GetFileNameWithoutExtension(filename).ToLowerInvariant();
                var fullPath=Path.Combine(folderPath, name);
                int i = 0;
                while (System.IO.File.Exists(fullPath))
                {
                    i++;
                    fullPath=Path.Combine(folderPath, name+i.ToString()+ext);
                }
                using(var stream=new FileStream(fullPath, FileMode.Create))
                {
                    await image.CopyToAsync(stream);
                }
                var f = new Models.File();
                f.path = fullPath;
                f._id = "";
                f=await _fileService.add(f);
                p.images.Add(f);
                
            }
            await _posts.InsertOneAsync(p);
            return await postToPostSend(p);

        }
        public async Task<PostSend> postToPostSend(Post post)
        {
            PostSend p = new PostSend();
            //Convert post to post send (TODO)
            p._id = post._id;
            p.ownerId = post.ownerId;
            p.description = post.description;
            p.location = await _locationService.getById(post.locationId);
            p.images = post.images;
            p.views = post.views.Count();
            if (post.ratings.Count() > 0)
            {
                List<int> ratings = new List<int>();
                foreach (var r in post.ratings)
                    ratings.Add(r.rating);
                p.ratings = ratings.Average();
            }
            else
                p.ratings = 0;
            p.comments = null;


            return p;
        }

        public async Task<List<PostSend>> getAllPosts()
        {
            List<Post> posts = await _posts.Find(_ => true).ToListAsync();
            List<PostSend> temp = new List<PostSend>();
            foreach (var post in posts)
            {
                temp.Add(await postToPostSend(post));
            }
            return temp;
        }

        public async Task<PostSend> getPostById(string id,string userid)
        {
            Post p = await _posts.Find(post => post._id == id).FirstOrDefaultAsync();
            if (p != null)
            {
                if (!p.views.Any(x => x == userid)) 
                {
                    p.views.Add(userid);
                    await _posts.ReplaceOneAsync(x => x._id == id, p);
                }
            }
            return await postToPostSend(p);
        }
        
        public async Task<Boolean> AddOrReplaceRating(RatingReceive  rating,string userid)
        {
            Post p = await _posts.Find(post => post._id == rating.postId).FirstOrDefaultAsync();
            if (p != null)
            {
                if (p.ownerId == userid)
                    return false;
                if(!p.ratings.Any(x => x.userId == userid))
                {
                    Rating r = new Rating();
                    r.rating = rating.rating;
                    r.userId = userid;
                    p.ratings.Add(r);
                    await _posts.ReplaceOneAsync(x => x._id == p._id, p);
                }
                else
                {
                    var r = p.ratings.Find(x => x.userId == userid);
                    p.ratings.Remove(r);
                    r.rating = rating.rating;
                    p.ratings.Add(r);
                    await _posts.ReplaceOneAsync(x => x._id == p._id, p);
                }
                return true;
            }
            return false;
        }
        public async Task<Boolean> RemoveRating(string postid, string userid)
        {
            Post p = await _posts.Find(post => post._id == postid).FirstOrDefaultAsync();
            if (p != null)
            {
                if (p.ratings.Any(x => x.userId == userid))
                {
                    var r = p.ratings.Find(x => x.userId == userid);
                    p.ratings.Remove(r);
                    await _posts.ReplaceOneAsync(x => x._id == postid, p);
                    return true;
                }
            }
            return false;
        }
        public async Task<Boolean> AddComment(CommentReceive cmnt,string userid,string postid)
        {
            Post p = await _posts.Find(post => post._id == postid).FirstOrDefaultAsync();
            if (p != null)
            {
                Comment c= new Comment();
                c.parentId = cmnt.parentId;
                c.userId = userid;
                c.comment = cmnt.comment;
                c.timestamp = DateTime.Now.ToUniversalTime();
                c._id = ObjectId.GenerateNewId().ToString();
                p.comments.Add(c);
                await _posts.ReplaceOneAsync(x => x._id == postid, p);
                return true;
            }
            return false;
        }

        public async Task<List<CommentSend>> ListComments(string postid)
        {
            Post p = await _posts.Find(post => post._id == postid).FirstOrDefaultAsync();
            if (p != null)
            {
                List<Comment> lista = new List<Comment>();
                lista = p.comments.FindAll(x => x.parentId == null || x.parentId == "");
                if (lista.Count() > 0) 
                {
                    List<CommentSend> tosend = new List<CommentSend>();
                    foreach(var comment in lista)
                    {
                        CommentSend c = new CommentSend();
                        c.userId = comment.userId;
                        c._id = comment._id;
                        c.parentId = comment.parentId;
                        c.comment = comment.comment;
                        c.timestamp = comment.timestamp;

                        var user = await _users.Find(x => x._id == comment.userId).FirstOrDefaultAsync();
                        if (user != null)
                            c.username = user.username;
                        else c.username = "Deleted user";

                        c.replies = await CascadeComments(comment._id, p);

                        tosend.Add(c);
                    }
                    return tosend;
                }
            }
            return null;
        }
        public async Task<List<CommentSend>> CascadeComments(string parentid,Post p)
        {
            List<Comment> lista = new List<Comment>();
            lista = p.comments.FindAll(x => x.parentId == parentid);
            if (lista.Count()>0)
            {
                List<CommentSend> replies = new List<CommentSend>();
                foreach (var comment in lista)
                {
                    CommentSend c = new CommentSend();
                    c.userId = comment.userId;
                    c._id = comment._id;
                    c.parentId = comment.parentId;
                    c.comment = comment.comment;
                    c.timestamp = comment.timestamp;

                    var user= await _users.Find(x => x._id == comment.userId).FirstOrDefaultAsync();
                    if (user != null)
                        c.username = user.username;
                    else c.username = "Deleted user";

                    c.replies = await CascadeComments(comment._id, p);

                    replies.Add(c);
                }
                return replies;
            }
            return null;
        }
        public async Task<Boolean> DeleteComments(string postid,string cmntid,string userid)
        {
            Post p = await _posts.Find(post => post._id == postid).FirstOrDefaultAsync();
            if (p != null)
            {
                var com = p.comments.Find(x => x._id == cmntid);
                if (com != null && com.userId == userid)
                {
                    var comment = p.comments.Find(x => x._id == cmntid);
                    p.comments.Remove(comment);
                    await _posts.ReplaceOneAsync(x => x._id == p._id, p);
                    await CascadeDeleteComments(cmntid, p);
                    return true;
                }
            }
            return false;              
        }
        public async Task CascadeDeleteComments(string cmntid,Post p)
        {
            List<Comment> lista = new List<Comment>();
            lista = p.comments.FindAll(x => x.parentId == cmntid);
            if (lista.Count() > 0)
            {
                foreach (var comment in lista) 
                {
                    p.comments.Remove(comment);
                    await _posts.ReplaceOneAsync(x => x._id == p._id, p);
                    await CascadeDeleteComments(comment._id, p);
                }             
            }
        }
    }
}