using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace Api.Models
{
    public class Message
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public String _id { get; set; }
        public String senderId { get; set; }
        public String receiverId { get; set; }
        public String messagge { get; set; }
        public DateTime timestamp { get; set; }
    }
    public class MessageSend
    {
        public String senderId { get; set; }
        public String messagge { get; set; }
        public DateTime timestamp { get; set; }
    }
    public class MessageReceive
    {
        public String receiverId { get; set; }
        public String messagge { get; set; }
    }
}
