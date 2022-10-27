using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Api.Models
{
    public class User
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public String _id { get; set; }
        public String name { get; set; }
        public String username { get; set; }
        public String email { get; set; }
        public String password { get; set; }
        public DateTime creationDate { get; set; }
    }
}
