using Api.Interfaces;
using Api.Models;
using ImageMagick;
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
        public async Task<Byte[]> getCompressedImage(string id)
        {
            Byte[] res = null;
            Models.File f = await getById(id);
            if (f == null || !System.IO.File.Exists(f.path))
                return res;
            if (System.IO.File.Exists(f.path + "-compress"))
                return System.IO.File.ReadAllBytes(f.path + "-compress");
            using (MagickImage image = new MagickImage(f.path))
            {
                image.Format = image.Format;
                image.Quality = 30;
                res= image.ToByteArray();
                image.Write(f.path + "-compress");
            }


            return res;

        }
    }
}
