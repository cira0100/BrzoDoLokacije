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
            p.createdAt = DateTime.Now.ToUniversalTime();
            List<String> tags;
            if (post.tags != "none")
                tags = post.tags.Remove(post.tags.Length-1,1).Split("|").ToList();
            else tags = null;
            p.tags = tags;
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
            p._id = post._id;
            p.ownerId = post.ownerId;
            p.description = post.description;
            p.location = await _locationService.getById(post.locationId);
            p.images = post.images;
            p.views = post.views.Count();
            p.createdAt = post.createdAt;
            p.tags = post.tags;
            p.ratingscount = post.ratings.Count();
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
                if (!p.views.Any(x => x.Split("|")[0] == userid)) 
                {
                    p.views.Add(userid + "|" + DateTime.Now.ToUniversalTime().ToString());
                    await _posts.ReplaceOneAsync(x => x._id == id, p);
                }
                else
                {
                    var v = p.views.Find(x => x.Split("|")[0] == userid);
                    p.views.Remove(v);
                    p.views.Add(userid + "|" + DateTime.Now.ToUniversalTime().ToString());
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
        public async Task<CommentSend> AddComment(CommentReceive cmnt,string userid,string postid)
        {
            Post p = await _posts.Find(post => post._id == postid).FirstOrDefaultAsync();
            if (p != null)
            {
                Comment c = new Comment();
                CommentSend c1= new CommentSend();
                c.parentId = cmnt.parentId;
                c1.parentId = cmnt.parentId;
                c.userId = userid;
                c1.userId = userid;
                c.comment = cmnt.comment;
                c1.comment = cmnt.comment;
                c.timestamp = DateTime.Now.ToUniversalTime();
                c1.timestamp = c.timestamp;
                c._id = ObjectId.GenerateNewId().ToString();
                c1._id = c._id;
                var user = await _users.Find(x => x._id == c.userId).FirstOrDefaultAsync();
                if (user != null)
                    c1.username = user.username;
                else c1.username = "Deleted user";
                p.comments.Add(c);
                await _posts.ReplaceOneAsync(x => x._id == postid, p);
                return c1;
            }
            return null;
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
        public async Task<PostSendPage> SearchPosts(string locid,int page = 0,int sorttype = 1 ,int filterdate = 1) // for now sorting by number of ratings , not avg rating
        {
            var days = DateEnumToDays(filterdate);
            var tosend = new PostSendPage();
            var pslista = new List<PostSend>();
            var lista = new List<Post>();
            var ls = new List<PostSend>();
            var xd = new List<PostSend>();
            lista = await _posts.Find(x => x.locationId == locid).ToListAsync();
            if (lista != null)
            {
                foreach (var elem in lista)
                {
                    if ((DateTime.Now - elem.createdAt).TotalDays < days)
                        ls.Add(await postToPostSend(elem));
                }
                
            }
            switch (sorttype)
            {
                case 1:
                    xd = ls.OrderByDescending(x => x.views).ToList();
                    break;
                case 2:
                    xd = ls.OrderByDescending(x => x.ratings).ToList();
                    break;
                case 3:
                    xd = ls.OrderByDescending(x => x.createdAt).ToList();
                    break;
                default:
                    xd = ls.OrderByDescending(x => x.views).ToList();
                    break;
            }
            if(xd != null)
            {
                tosend.page = page;
                tosend.index = page * 20;
                tosend.totalposts = xd.Count();
                double pgs = xd.Count / 20;
                tosend.totalpages = (int)Math.Ceiling(pgs);
                var selected = ls.Skip(20 * page).Take(20);
                foreach(var post in selected)
                {
                    pslista.Add(post);
                }
                tosend.posts = pslista;
            }
            return tosend;
        }
        public int DateEnumToDays(int filterdate)
        {
            switch(filterdate)
            {
                case 1: return 365 * 10;
                case 2: return 365;
                case 3: return 90;
                case 4: return 30;
                case 5: return 7;
                default: return 365 * 10;
            }
        }
        public async Task<List<PostSend>> GetUsersPosts(string id)
        {
            var userposts = await _posts.Find(x => x.ownerId == id).ToListAsync();
            if (userposts == null)
                return null;
            var tosend = new List<PostSend>();
            foreach (var post in userposts)
            {
                var x = await postToPostSend(post);
                tosend.Add(x);
            }
            return tosend;
        }
        public async Task<List<PostSend>> UserHistory(string userid)
        {
            var posts = await _posts.Find(_ => true).ToListAsync();
            if (posts == null)
                return null;
            var tosend = new List<PostSend>();
            foreach (var post in posts)
            {
                if (post.views.Any(x => x.Split("|")[0] == userid))
                {
                    var t = post.views.Find(x => x.Split("|")[0] == userid);
                    var x = await postToPostSend(post);
                    x.lastViewed = DateTime.Parse(t.Split("|")[1]).ToUniversalTime();
                    tosend.Add(x);
                }
            }
            tosend = tosend.OrderByDescending(x => x.lastViewed).ToList();
            return tosend;
        }

        public async Task<List<PostSend>> Get10Best()
        {
            List<Post> posts = await _posts.Find(_ => true).ToListAsync();
            List<PostSend> temp = new List<PostSend>();
            foreach (var post in posts)
            {
                temp.Add(await postToPostSend(post));
            }
            List<PostSend> best = temp.OrderByDescending(o => o.ratings).Take(10).ToList();
            return best;
        }

        public async Task<List<PostSend>> Get10MostViewed()
        {
            List<Post> posts = await _posts.Find(_ => true).ToListAsync();
            List<PostSend> temp = new List<PostSend>();
            foreach (var post in posts)
            {
                temp.Add(await postToPostSend(post));
            }
            List<PostSend> mostViewed = temp.OrderByDescending(o => o.views).Take(10).ToList();
            return mostViewed;
        }

        public async Task<List<PostSend>> Get10Newest()
        {
            List<Post> posts = await _posts.Find(_ => true).ToListAsync();
            List<PostSend> temp = new List<PostSend>();
            foreach (var post in posts)
            {
                temp.Add(await postToPostSend(post));
            }
            List<PostSend> newest = temp.OrderByDescending(o => o.createdAt).Take(10).ToList();
            return newest;
        }
    }
}
