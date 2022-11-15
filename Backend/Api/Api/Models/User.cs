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
        public String emailToken { get; set; }
        public Boolean verified { get; set; }
        public String password { get; set; }
        public DateTime creationDate { get; set; }
        public File? pfp { get; set; }
    }

    public class Login
    {
        public String email { get; set; }
        public String password { get; set; }
    }

    public class Register
    {
        public String name { get; set; }
        public String username { get; set; }
        public String email { get; set; }
        public String password { get; set; }
    }

    public class JustMail
    {
        public String email { get; set; }
    }

    public class ResetPass
    {
        public String email { get; set; }
        public String newpass { get; set; }
        public String kod { get; set; }
    }
    public class VerifyUser
    {
        public String email { get; set; }
        public String password { get; set; }
        public String kod { get; set; }
    }
    public class UserSend
    {
        public String _id { get; set; }
        public String name { get; set; }
        public String username { get; set; }
        public String email { get; set; }
        public DateTime creationDate { get; set; }
        public File? pfp { get; set; }
        public int postcount { get; set; }
    }
}
