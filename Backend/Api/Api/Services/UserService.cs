using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;

namespace Api.Services
{
    public class UserService : IUserService
    {
        private readonly IMongoCollection<User> _users;
        private readonly IJwtService _jwtService;
        public UserService(IDatabaseConnection settings, IMongoClient mongoClient, IJwtService jwtService)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _users = database.GetCollection<User>(settings.UserCollectionName);
            _jwtService=jwtService;
        }

        public async Task<int> createUser(User user)
        {
            if (await _users.Find(x => x.email == user.email).FirstOrDefaultAsync() != null)
                return -1; //email already exists
            if (await _users.Find(x => x.username == user.username).FirstOrDefaultAsync() != null)
                return -2; //username already exists
            
            await _users.InsertOneAsync(user);
            return 1;
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

        public async Task<string> Register(Register register)
        {
            if (await _users.FindAsync(x => x.email == register.email && x.verified==true).Result.AnyAsync())
                return "Email Exists";
            else if (await _users.FindAsync(x => x.username == register.username && x.verified==true).Result.AnyAsync())
                return "Username Exists";
            else
            {
                List<User> unverified = await _users.Find(x => (x.username == register.username || x.email == register.email) && x.verified == false).ToListAsync();
                if (unverified.Count > 0)
                {
                    foreach(var usr in unverified)
                    {
                        //ako user nema validan emailtoken, a nije verifikovan prethodno, onda se brise iz baze
                        if (_jwtService.EmailTokenToId(usr.emailToken) == null)
                            await _users.FindOneAndDeleteAsync(x => x._id == usr._id);
                    }
                }
            }
            var user = new User();
            user.email = register.email;
            user.username = register.username;
            user.name = register.name;
            user.verified = false;
            user.password = register.password; // unhashed for now


            return "";
        }

        public async Task<Boolean> VerifyUser(string _id)
        {
            User user = await _users.FindAsync(x => x._id==_id).Result.FirstAsync();
            if(user != null)
            {
                user.verified = true;
                await _users.ReplaceOneAsync(x => x._id == _id, user);
                return true;
            }
            return false;
        }

        public async Task<string> RenewToken(string existingToken)
        {
            var id = _jwtService.TokenToId(existingToken);
            if (id == null)
                return null;
            var user = await getUserById(id);

            return _jwtService.GenToken(user);

        }
    }
}
