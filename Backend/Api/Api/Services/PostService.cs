using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;

namespace Api.Services
{
    public class PostService : IPostService
    {
        private readonly MongoClient _client;
        private readonly IMongoCollection<Post> _posts;
        public PostService(IDatabaseConnection settings, IMongoClient mongoClient)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _posts = database.GetCollection<Post>(settings.PostCollectionName);
        }

        public PostSend addPost(PostReceive post)
        {
            return null;
        }
    }
}
