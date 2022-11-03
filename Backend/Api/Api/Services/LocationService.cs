using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;

namespace Api.Services
{
    public class LocationService : ILocationService
    {
        private readonly MongoClient _client;
        private readonly IMongoCollection<Location> _locations;
        private readonly IHttpContextAccessor _httpContext;
        public LocationService(IDatabaseConnection settings, IMongoClient mongoClient)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _locations = database.GetCollection<Location>(settings.FileCollectionName);
        }
        public async Task<Location> add(Location loc)
        {
            //TODO GOOGLE MAPS API CALL FOR info
            await _locations.InsertOneAsync(loc);
            return loc;
        }
        public async Task<Location> getById(string id)
        {
            return await _locations.Find(loc => loc._id == id).FirstOrDefaultAsync();
        }
        public async Task<List<Location>> getAllLocation()
        {
            return await _locations.Find(_=>true).ToListAsync();
        }
    }
}
