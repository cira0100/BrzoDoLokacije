using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace Api.Models
{
    public class Location
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public String _id { get; set; }
        public String name { get; set; }
        public String city { get; set; }
        public String country { get; set; }
        public String address { get; set; }
        public double latitude { get; set; }
        public double longitude { get; set; }
        public LocationType type { get; set; }

    }


    public enum LocationType
    {
        Plaza,
        Grad,
        Zgrada,
        Itd
    }
}
