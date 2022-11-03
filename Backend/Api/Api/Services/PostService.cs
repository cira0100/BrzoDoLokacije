using System.Security.Claims;
using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;

namespace Api.Services
{
    public class PostService : IPostService
    {
        private readonly MongoClient _client;
        private readonly IMongoCollection<Post> _posts;
        private readonly IHttpContextAccessor _httpContext;
        private readonly IFileService _fileService;
        public PostService(IDatabaseConnection settings, IMongoClient mongoClient, IHttpContextAccessor httpContext, IFileService fileService)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _posts = database.GetCollection<Post>(settings.PostCollectionName);
            _httpContext = httpContext;
            _fileService = fileService;
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








            return postToPostSend(p);

        }
        public PostSend postToPostSend(Post post)
        {
            PostSend p = new PostSend();
            //Convert post to post send (TODO)
            p._id = post._id;
            return p;
        }

        public async Task<List<PostSend>> getAllPosts()
        {
            List<Post> posts = await _posts.Find(_ => true).ToListAsync();
            List<PostSend> temp = new List<PostSend>();
            foreach (var post in posts)
            {
                temp.Add(postToPostSend(post));
            }
            return temp;
        }

        public async Task<PostSend> getPostById(string id)
        {
            Post p = await _posts.Find(post => post._id == id).FirstOrDefaultAsync();
            return postToPostSend(p);

        }
        //(TODO) ADD Delete and update
    }
}
