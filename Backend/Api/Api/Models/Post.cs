using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace Api.Models
{
    public class Post
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string _id { get; set; }
        public string ownerId { get; set; }
        public Location location { get; set; }
        public string description { get; set; }
        public List<string> views { get; set; }
        public List<string> reports { get; set; }
        public List<Rating> ratings { get; set; }
        public List<Comment> comments { get; set; }
        public List<File> images { get; set; }
    }
    public class PostReceive
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string _id { get; set; }
        public Location location { get; set; }
        public string description { get; set; }
        public List<IFormFile> images { get; set; }


    }
    public class PostSend
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string _id { get; set; }
        public string ownerId { get; set; }
        public Location location { get; set; }
        public string description { get; set; }
        public int views { get; set; }
        public float ratings { get; set; }
        public List<Comment> comments { get; set; }
        public List<File> images { get; set; }
    }
    public class Rating
    {
        public string userId { get; set; }
        public int rating { get; set; }
    }
    public class Comment
    {
        public string userId { get; set; }
        public string comment { get; set; }
        public Comment parent { get; set; }
        public DateTime timestamp { get; set; }
    }
}
