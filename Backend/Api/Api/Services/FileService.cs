using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;
using File = Api.Models.File;

namespace Api.Services
{
    public class FileService : IFileService
    {
        private readonly MongoClient _client;
        private readonly IMongoCollection<File> _files;
        private readonly IHttpContextAccessor _httpContext;
        public FileService(IDatabaseConnection settings, IMongoClient mongoClient)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _files = database.GetCollection<File>(settings.FileCollectionName);
        }
        public async Task<File> add(File file)
        {
            await _files.InsertOneAsync(file);
            return file;
        }
        public async Task<File> getById(string id)
        {
            return await _files.Find(file => file._id == id).FirstOrDefaultAsync();
        }
    }
}
