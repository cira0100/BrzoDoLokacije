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
        //public LocationType type { get; set; }

    }


    public enum LocationType
    {
        GRAD,ULICA,JEZERO,REKA,PLAZA,OKEAN, MORE, MOREUZ, MOST,BANJA,
        PLANINA, VISORAVAN, PIRAMIDA, LIVADA, SELO, OSTRVO, POLUOSTRVO, KLISURA, ARHIPELAG,
        ADA, DELTA, FJORD, GEJZIR, IZVOR, KOTLINA, MINERALNI_IZVOR, PECINA ,SUMA, VODOPAD,VULKAN,
        MUZEJ,ZAMAK,TRG,SPOMENIK,PARK,ZGRADA
    }

    public class Coords
    {
        public double latitude { get; set; }
        public double longitude { get; set; }
    }
    public enum SearchType
    {
        BY_NAME = 1 ,
        BY_COORDS =2 
    }
    public class LocationViews
    {
        public Location location { get; set; }
        public int views { get; set; }
    }
}
