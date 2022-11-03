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
        public PostService(IDatabaseConnection settings, IMongoClient mongoClient, IHttpContextAccessor httpContext)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _posts = database.GetCollection<Post>(settings.PostCollectionName);
            _httpContext = httpContext;
        }

        public async Task<PostSend> addPost(PostReceive post)
        {
            Post p = new Post();
            p._id = "";
            p.ownerId = _httpContext.HttpContext.User.FindFirstValue("id");
            p.location = post.location;
            p.description = post.description;
            p.views = new List<string>();
            p.reports = new List<string>();
            p.ratings = new List<Rating>();
            p.comments = new List<Comment>();
            //add file
            //add to database
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
