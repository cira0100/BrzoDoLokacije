using Api.Interfaces;
using Api.Models;
using Geocoding;
using Geocoding.Google;
using Geocoding.MapQuest;
using MongoDB.Driver;
using ZstdSharp.Unsafe;
using Location = Api.Models.Location;

namespace Api.Services
{
    public class LocationService : ILocationService
    {
        private readonly MongoClient _client;
        private readonly IMongoCollection<Location> _locations;
        private readonly IHttpContextAccessor _httpContext;
        private IConfiguration _configuration;
        private MapQuestGeocoder _geocoder;
        public LocationService(IDatabaseConnection settings, IMongoClient mongoClient, IConfiguration configuration)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _locations = database.GetCollection<Location>(settings.LocationCollectionName);
            _configuration = configuration;
            var _mapQuestApiKey = _configuration.GetSection("AppSettings:MapQuestApiKey").Value;
            _geocoder = new MapQuestGeocoder(_mapQuestApiKey);

        }
        public async Task<Location> add(Location loc)
        {
            IEnumerable<Address> adresses = await _geocoder.GeocodeAsync(loc.name+" "+loc.address+" "+loc.city+" "+loc.country);
            loc.country = loc.name;
            loc.latitude = adresses.First().Coordinates.Latitude;
            loc.longitude=adresses.First().Coordinates.Longitude;
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
