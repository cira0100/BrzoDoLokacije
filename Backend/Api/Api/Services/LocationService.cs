using Api.Interfaces;
using Api.Models;
using Geocoding;
using Geocoding.Google;
using Geocoding.MapQuest;
using MongoDB.Bson;
using MongoDB.Driver;
using System.Text.RegularExpressions;
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
        private readonly IMongoCollection<Post> _posts;
        public LocationService(IDatabaseConnection settings, IMongoClient mongoClient, IConfiguration configuration)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _locations = database.GetCollection<Location>(settings.LocationCollectionName);
            _posts = database.GetCollection<Post>(settings.PostCollectionName);
            _configuration = configuration;
            var _mapQuestApiKey = _configuration.GetSection("AppSettings:MapQuestApiKey").Value;
            _geocoder = new MapQuestGeocoder(_mapQuestApiKey);

        }
        public async Task<Location> add(Location loc)
        {

            //On Client side
            //IEnumerable<Address> adresses = await _geocoder.GeocodeAsync(loc.name+" "+loc.address+" "+loc.city+" "+loc.country);
            //loc.country = loc.name;
            //loc.latitude = adresses.First().Coordinates.Latitude;
            //loc.longitude=adresses.First().Coordinates.Longitude;
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

        public async Task<List<Location>> SearchLocation(Coords coords) // returns all locations within coord radius 1/5th of a degree of lat and long
        {
            if (coords == null)
                return null;
            var lista = await _locations.Find(_ => true).ToListAsync();
            var tosend = new List<Location>();
            if (lista != null)
            {
                foreach (var elem in lista)
                {
                    if (Math.Abs(elem.latitude - coords.latitude) < 0.2 && Math.Abs(elem.longitude - coords.longitude) < 0.2)
                        tosend.Add(elem);
                }
            }
            return tosend;
        }
        public async Task<List<Location>> SearchLocation(string query) //returns 10 (n) most relevant locations when searching by name
        {
            if (query == null) query = "";
            else query = query.ToLower();
            var lista = await _locations.Find(x => x.name.ToLower().Contains(query)).ToListAsync();
            var tosend = new List<Location>();
            if (lista != null)
            {
                var locviews = new List<LocationViews>();
                foreach (var elem in lista)
                {
                    var totalviews = 0;
                    var posts = await _posts.Find(x => x.locationId == elem._id).ToListAsync();
                    if(posts != null)
                    {
                        foreach(var post in posts)
                        {
                            totalviews += post.views.Count();
                        }
                    }
                    var novi = new LocationViews();
                    novi.location = elem;
                    novi.views = totalviews;
                    locviews.Add(novi);
                }
                var top10 = locviews.OrderByDescending(x => x.views).Take(10).ToList();
                foreach(var view in top10)
                {
                    tosend.Add(view.location);
                }
            }
            return tosend;
        }

        
    }
}
