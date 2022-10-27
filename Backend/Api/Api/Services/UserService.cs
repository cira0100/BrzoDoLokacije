using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;

namespace Api.Services
{
    public class UserService : IUserService
    {
        private readonly IMongoCollection<User> _users;
        public UserService(IDatabaseConnection settings, IMongoClient mongoClient)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _users = database.GetCollection<User>(settings.UserCollectionName);

        }

        public User createUser(User user)
        {
            _users.InsertOne(user);
            return user;
        }

        public bool deleteUser(string email)
        {
            _users.FindOneAndDelete(x => x.email == email);
            return true;
        }

        public User getUserByEmail(string email)
        {
            return _users.Find(x => x.email == email).First();
        }

        public User getUserByUsername(string username)
        {
            return _users.Find(x => x.username == username).First();
        }

        public List<User> getUsers()
        {
            return _users.Find(_=>true).ToList();
        }

        public User updateUser(User user)
        {
            /*
             * ovako je odradjeno da bi radilo i kada se posalje potpuno novi objekat User-a bez generisanog _id polja
             */
            User foundUser = _users.Find(x => x.email == user.email).First();
            if (foundUser!=null && user._id==null)
            {
                user._id = foundUser._id;
            }
            _users.ReplaceOne(x => x.email == user.email, user);
            return user;
        }
    }
}
