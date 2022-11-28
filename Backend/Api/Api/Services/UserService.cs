using Api.Interfaces;
using Api.Models;
using MongoDB.Driver;
using Microsoft.AspNetCore.Http;
using System.Security.Claims;
using MimeKit;
using MailKit.Net.Smtp;
using DnsClient;

namespace Api.Services
{
    public class UserService : IUserService
    {
        private readonly IHttpContextAccessor _httpContext;
        private readonly IMongoCollection<User> _users;
        private readonly IMongoCollection<Post> _posts;
        private readonly IJwtService _jwtService;
        private IConfiguration _configuration;
        private readonly IFileService _fileService;

        private readonly IMongoCollection<UserSend> _usersSend; 
        public UserService(IDatabaseConnection settings, IMongoClient mongoClient, IJwtService jwtService, IHttpContextAccessor httpContextAccessor, IConfiguration configuration, IFileService fileService)
        {
            var database = mongoClient.GetDatabase(settings.DatabaseName);
            _users = database.GetCollection<User>(settings.UserCollectionName);
            _posts = database.GetCollection<Post>(settings.PostCollectionName);
            _jwtService = jwtService;
            this._httpContext = httpContextAccessor;
            this._configuration = configuration;
            _fileService = fileService;
        }

        public async Task<int> createUser(User user)
        {
            if (await _users.Find(x => x.email == user.email).FirstOrDefaultAsync() != null)
                return -1; //email already exists
            if (await _users.Find(x => x.username == user.username).FirstOrDefaultAsync() != null)
                return -2; //username already
                           //
            user.password = hashPassword(user.password);
  
            await _users.InsertOneAsync(user);
            return 1;
        }

        public async Task<User> deleteUser(string email)
        {
             return await _users.FindOneAndDeleteAsync(x => x.email == email);
        }

        public async Task<User> getUserByEmail(string email)
        {
            return await _users.Find(x => x.email == email).SingleOrDefaultAsync();
        }

        public async Task<User> getUserByUsername(string username)
        {
            return await _users.Find(x => x.username == username).SingleOrDefaultAsync();
        }

        public async Task<List<User>> getUsers()
        {
            return await _users.Find(_=>true).ToListAsync();
        }

        public async Task<User> getUserById(string id)
        {
            return  await _users.Find(user => user._id == id).SingleOrDefaultAsync();

        }

        public async Task<long> updateUser(User user)
        {
            /* vraca broj izmenjenih korisnika
             * ovako je odradjeno da bi radilo i kada se posalje potpuno novi objekat User-a bez generisanog _id polja
             */
            User foundUser = await _users.Find(x => x.email == user.email).SingleOrDefaultAsync();
            if (foundUser!=null && user._id==null)
            {
                user._id = foundUser._id;
            }
            ReplaceOneResult res=await _users.ReplaceOneAsync(x => x.email == user.email, user);
            if(res.IsAcknowledged)
                return res.ModifiedCount;
            return 0;
        }

        private static int difficulty = 10;

        public static String hashPassword(String password)
        {
            String salt = BCrypt.Net.BCrypt.GenerateSalt(difficulty);
            String passwordHash = BCrypt.Net.BCrypt.HashPassword(password, salt);

            return passwordHash;
        }

        public static Boolean checkPassword(String plainText, String hash)
        {
            Boolean verified = false;

            if (hash == null || !hash.StartsWith("$2a$"))
                return false;

            verified = BCrypt.Net.BCrypt.Verify(plainText, hash);

            return verified;

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
                        if (_jwtService.EmailTokenToClaim(usr.emailToken,"id") == null)
                            await _users.FindOneAndDeleteAsync(x => x._id == usr._id);
                    }
                    foreach (var usr in unverified)
                    {
                        if (usr.email == register.email && _jwtService.EmailTokenToClaim(usr.emailToken,"id") != null)
                            return "Unverified Email Exists, check your inbox";
                        if (usr.username == register.username && _jwtService.EmailTokenToClaim(usr.emailToken,"id") != null)
                            return "Unverified Username Exists, please select another";
                    }
                }
            }

            var user = new User();
            user.email = register.email;
            user.username = register.username;
            user.name = register.name;
            user.verified = false;
            user.password = hashPassword(register.password);
            user.creationDate = DateTime.Now.ToUniversalTime();
            user.emailToken = "";

            await _users.InsertOneAsync(user);

            user.emailToken = _jwtService.GenEmailToken(user);
            await _users.ReplaceOneAsync(x => x._id == user._id, user);
            SendEmailKod(user,1);

            return "User Registered";
        }

        public async Task<Boolean> VerifyUser(VerifyUser login)
        {
            User user = await _users.FindAsync(x => x.email == login.email).Result.FirstOrDefaultAsync();
            if (user != null && checkPassword(login.password, user.password))
            {
                var basekod = _jwtService.EmailTokenToClaim(user.emailToken,"kod");
                if (basekod != null)
                    if (String.Compare(login.kod,basekod) == 0)
                    {
                        user.verified = true;
                        user.emailToken = "";
                        await _users.ReplaceOneAsync(x => x._id == user._id, user);
                        return true;
                    }
            }
            return false;
        }

        public async Task<string> RenewToken()
        {
            var id = await UserIdFromJwt();
            if (id == null)
                return null;
            var user = await getUserById(id);

            return _jwtService.GenToken(user);
        }

        public async Task<string> Login(Login login)
        {
            User user = await _users.FindAsync(x => x.email == login.email && x.verified == true).Result.FirstOrDefaultAsync();
            if (user != null && checkPassword(login.password, user.password))
            {
                return _jwtService.GenToken(user);
            }
            return null;
        }

        public async Task<string> UserIdFromJwt()
        {
            string id = null;
            if (_httpContext.HttpContext.User.FindFirstValue("id") != null)
            {
                id = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
                var _id = await _users.FindAsync(x => x._id == id).Result.FirstOrDefaultAsync();
                if (_id == null)
                    id = null;
            }
            return id;
        }

        public async Task<Boolean> ResendVerifyEmail(Login login)
        {
            User user = await _users.FindAsync(x => x.email == login.email).Result.FirstOrDefaultAsync();
            if (user != null && checkPassword(login.password, user.password))
            {
                user.emailToken = _jwtService.GenEmailToken(user);
                await _users.ReplaceOneAsync(x => x._id == user._id, user);
                SendEmailKod(user,1);

                return true;
            }
            return false;
        }


        public Boolean SendEmailKod(User user,int msgid) //1 - email verification, 2 - password reset
        {
            MimeMessage message = new MimeMessage();
            message.From.Add(new MailboxAddress("Tim Oddyssey", _configuration.GetSection("EmailCfg:Email").Value));
            message.To.Add(MailboxAddress.Parse(user.email));
            message.Subject = "Vas Oddyssey verifikacioni kod"; //think of something better yeah?

            var kod = _jwtService.EmailTokenToClaim(user.emailToken,"kod");
            if (kod == null)
                return false;

            var bodybuilder = new BodyBuilder();
            switch(msgid){
                case 1:
                    //bodybuilder.HtmlBody = String.Format(@"<h3>Verfikacioni kod:</h3><h2>" + kod + "</h2><br><p>Kod traje <b>30</b> minuta</p>");
                    bodybuilder.HtmlBody = String.Format(@"<h3>Link za verifikaciju emaila:</h3><br>" +
                    "<form method='get' action='" + _configuration.GetSection("URLs:localhost").Value + "api/auth/verifytoken/" + user.emailToken + "'>" +
                    "<input type='submit' style='background:gray; color:white' value='Verify Email'>" +
                    "</form>" + 
                    "<br><p>Link traje <b>30</b> minuta</p>");
                    break;
                case 2:
                    bodybuilder.HtmlBody = String.Format(@"<h3>Verfikacioni kod:</h3><h2>" + kod + "</h2><br><p>Kod traje <b>30</b> minuta</p>");
                    break;
            }
            
            message.Body = bodybuilder.ToMessageBody();

            SmtpClient client = new SmtpClient();
            try
            {
                client.Connect(_configuration.GetSection("EmailCfg:SmtpServer").Value, 465, true);
                client.Authenticate(_configuration.GetSection("EmailCfg:Email").Value, _configuration.GetSection("EmailCfg:Password").Value);
                client.Send(message);

            }
            catch (Exception ex)
            {
                return false;
            }
            finally
            {
                client.Disconnect(true);
                client.Dispose();
            }
            return true;
        }

        public async Task<Boolean> ForgotPassword(JustMail jm)
        {
            User user = await _users.FindAsync(x => x.email == jm.email && x.verified == true).Result.FirstOrDefaultAsync();
            if (user != null)
            {
                user.emailToken = _jwtService.GenEmailToken(user);
                await _users.ReplaceOneAsync(x => x._id == user._id, user);
                SendEmailKod(user,2);

                return true;
            }
            return false;
        }

        public async Task<Boolean> ResetPassword(ResetPass rp)
        {
            User user = await _users.FindAsync(x => x.email == rp.email && x.verified == true).Result.FirstOrDefaultAsync();
            if (user != null)
            {
                var basekod = _jwtService.EmailTokenToClaim(user.emailToken,"kod");
                if (basekod != null)
                    if (String.Compare(rp.kod, basekod) == 0)
                    {
                        user.password = hashPassword(rp.newpass);
                        user.emailToken = "";
                        await _users.ReplaceOneAsync(x => x._id == user._id, user);
                        return true;
                    }
            }
            return false;
        }
        public async Task<Boolean> CheckVerification(Login login)
        {
            User user = await _users.FindAsync(x => x.email == login.email).Result.FirstOrDefaultAsync();
            if (user != null && checkPassword(login.password, user.password) && user.verified == true)
            {
                return true;
            }
            return false;
        }
        public async Task<Boolean> VerifyFromToken(string token)
        {
            User user = await _users.FindAsync(x => x.emailToken == token).Result.FirstOrDefaultAsync();
            if(user != null)
            {
                user.verified = true;
                user.emailToken = "";
                await _users.ReplaceOneAsync(x => x._id == user._id, user);
                return true;
            }
            return false;
        }

        public async Task<Boolean> AddOrChangePfp(string userid,IFormFile image)
        {
            var user = await _users.Find(x => x._id == userid).FirstOrDefaultAsync();
            if (user == null)
                return false;
            var folderPath = Path.Combine(Directory.GetCurrentDirectory(), "Files", userid);
            if (!Directory.Exists(folderPath))
                Directory.CreateDirectory(folderPath);
            var filename = image.FileName;
            var ext = Path.GetExtension(filename).ToLowerInvariant();
            var name =user._id;
            var fullPath = Path.Combine(folderPath, name+ext);
            if (System.IO.File.Exists(fullPath))
                System.IO.File.Delete(fullPath);
            using (var stream = new FileStream(fullPath, FileMode.Create))
            {
                await image.CopyToAsync(stream);
            }
            var f = new Models.File();
            f.path = fullPath;
            f._id = "";
            f = await _fileService.add(f);
            user.pfp = f;
            await _users.ReplaceOneAsync(x => x._id == user._id, user);
            return true;
        }

        public async Task<UserSend> GetUserData(string username)
        {
            var user = await _users.Find(x => x.username == username).FirstOrDefaultAsync();
            if(user == null)
                return null;
            var tosend = new UserSend();
            tosend.name = user.name;
            tosend.pfp = user.pfp;
            tosend.username = user.username;
            tosend._id= user._id;
            tosend.creationDate = user.creationDate;
            tosend.email="";
            var userposts = await _posts.Find(x => x.ownerId == user._id).ToListAsync();
            tosend.postcount = userposts.Count();
            return tosend;
        }
        public async Task<UserSend> GetSelfUserData(string id)
        {
            var user = await _users.Find(x => x._id == id).FirstOrDefaultAsync();
            if (user == null)
                return null;
            var tosend = new UserSend();
            tosend.name = user.name;
            tosend.pfp = user.pfp;
            tosend.username = user.username;
            tosend._id = user._id;
            tosend.creationDate = user.creationDate;
            tosend.email = user.email;
            var userposts = await _posts.Find(x => x.ownerId == user._id).ToListAsync();
            tosend.postcount = userposts.Count();
            return tosend;
        }

        public async Task<Boolean> AddFollower(string followerId)
        {
            string id = null;
            if (_httpContext.HttpContext.User.FindFirstValue("id") != null)
            {
                id = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
            }
            User f = await _users.Find(user => user._id == followerId).FirstOrDefaultAsync();
            User u = await _users.Find(user => user._id == id).FirstOrDefaultAsync();
           
            if (id != null && followerId!=null)
            {
                if (u.followers == null)
                    u.followers = new List<string>();
                u.followers.Add(followerId);
                if (f.following == null)
                    f.following = new List<string>();
                f.following.Add(id);
                _users.ReplaceOne(user=>user._id==id, u);
                _users.ReplaceOne(user => user._id == followerId, f);
                return true;
            }


            return false;
        }

        public async Task<List<UserSend>> GetFollowers(string id)
        {
            User u = await _users.Find(user => user._id == id).FirstOrDefaultAsync();
            List<UserSend> followers = new List<UserSend>();
            if (u != null)
            {
                //List<UserSend> followers = u.followers;
                if (u.followers!=null &&u.followers.Count() > 0)
                {
                    foreach (string userid in u.followers)
                    {
                        User utemp = await _users.Find(user => user._id == userid).FirstOrDefaultAsync();
                        if (utemp == null)
                        {
                            continue;
                        }
                        UserSend follower = new UserSend();
                        follower.pfp = utemp.pfp;
                        follower.username = utemp.username;
                        follower.email = utemp.username;
                        follower.followers = utemp.followers;
                        follower._id = utemp._id;

                        followers.Add((UserSend)follower);  
                    }
                }
                u.followersCount=followers.Count() ;   
                return followers;
            }
            return null;
        }

        public async Task<List<UserSend>> GetFollowing(string id)
        {
            User u = await _users.Find(user => user._id == id).FirstOrDefaultAsync();
            List<UserSend> following = new List<UserSend>();
            if (u != null)
            {
                
                if (u.following!=null &&u.following.Count() > 0)
                {
                    foreach (string userid in u.following)
                    {
                        User utemp = await _users.Find(user => user._id == userid).FirstOrDefaultAsync();
                        if (utemp == null)
                        {
                            continue;
                        }
                        UserSend follower = new UserSend();
                        follower.pfp = utemp.pfp;
                        follower.username = utemp.username;
                        follower.email = utemp.username;
                        follower.followers = utemp.followers;
                        follower._id = utemp._id;

                        following.Add((UserSend)follower);
                    }
                }
                u.followersCount = following.Count();
                return following;
            }
            return null;
        }

        public async Task<List<UserSend>> GetMyFollowings()
        {
            string id = null;

            if (_httpContext.HttpContext.User.FindFirstValue("id") != null)
            {
                id = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
            }
            User u = await _users.Find(user => user._id == id).FirstOrDefaultAsync();
            List<UserSend> myFollowings = new List<UserSend>();
            if (u != null)
            {

                if (u.following != null && u.following.Count() > 0)
                {
                    foreach (string userid in u.following)
                    {
                        User utemp = await _users.Find(user => user._id == userid).FirstOrDefaultAsync();
                        if (utemp == null)
                        {
                            continue;
                        }
                        UserSend following = new UserSend();
                        following.pfp = utemp.pfp;
                        following.username = utemp.username;
                        following.email = utemp.username;
                        following.followers = utemp.followers;
                        following._id = utemp._id;

                        myFollowings.Add((UserSend)following);
                    }
                }
                return myFollowings;
            }
            return null;
        }

        public async Task<bool> CheckIfAlreadyFollow(string id)
        {
            string myId = null;

            if (_httpContext.HttpContext.User.FindFirstValue("id") != null)
            {
                myId = _httpContext.HttpContext.User.FindFirstValue("id").ToString();
            }

            User u = await _users.Find(user => user._id == myId).FirstOrDefaultAsync();
            User f = await _users.Find(user => user._id == id).FirstOrDefaultAsync();

            if (u != null)
            {

                if (u.following != null && u.following.Count() > 0)
                {
                    foreach (string userid in u.following)
                    {
                        User utemp = await _users.Find(user => user._id == userid).FirstOrDefaultAsync();
                        if (utemp == null)
                        {
                            continue;
                        }
                        if (utemp._id == f._id)
                        {
                            return true;
                        }
                    }
                }
                
            }

            return false;
        }
    }
}
