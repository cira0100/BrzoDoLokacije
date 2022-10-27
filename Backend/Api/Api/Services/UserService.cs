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

        public async Task createUser(User user)
        {
            await _users.InsertOneAsync(user);
            
        }

        public async Task<User> deleteUser(string email)
        {
             return await _users.FindOneAndDeleteAsync(x => x.email == email);
        }

        public async Task<User> getUserByEmail(string email)
        {
            return await _users.Find(x => x.email == email).SingleAsync();
        }

        public async Task<User> getUserByUsername(string username)
        {
            return await _users.Find(x => x.username == username).SingleAsync();
        }

        public async Task<List<User>> getUsers()
        {
            return await _users.Find(_=>true).ToListAsync();
        }

        public async Task<User> getUserById(string id)
        {
            return  await _users.Find(user => user._id == id).SingleAsync();

        }

        public async Task<long> updateUser(User user)
        {
            /* vraca broj izmenjenih korisnika
             * ovako je odradjeno da bi radilo i kada se posalje potpuno novi objekat User-a bez generisanog _id polja
             */
            User foundUser = await _users.Find(x => x.email == user.email).SingleAsync();
            if (foundUser!=null && user._id==null)
            {
                user._id = foundUser._id;
            }
            ReplaceOneResult res=await _users.ReplaceOneAsync(x => x.email == user.email, user);
            if(res.IsAcknowledged)
                return res.ModifiedCount;
            return 0;
        }

        
    }
}
