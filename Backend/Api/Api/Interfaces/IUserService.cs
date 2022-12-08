using Api.Models;

namespace Api.Interfaces
{
    public interface IUserService
    {
        Task<int> createUser(User user);
        Task<List<User>> getUsers();
        Task<User> getUserByEmail(String email);
        Task<User> getUserByUsername(String username);
        Task<long> updateUser(User user);
        Task<User> deleteUser(String email);
        Task<User> getUserById(string id);

        Task<string> RenewToken();
        Task<string> Login(Login login);
        Task<string> Register(Register register);
        Task<Boolean> VerifyUser(VerifyUser login);
        Task<string> UserIdFromJwt();
        Task<Boolean> ResendVerifyEmail(Login login);
        Boolean SendEmailKod(User user,int msgid);
        Task<Boolean> ForgotPassword(JustMail jm);
        Task<Boolean> ResetPassword(ResetPass rp);
        Task<Boolean> CheckVerification(Login login);
        Task<Boolean> VerifyFromToken(string token);
        Task<Boolean> AddOrChangePfp(string userid, IFormFile image);
        Task<UserSend> GetUserData(string username);
        Task<UserSend> GetSelfUserData(string id);

        Task<Boolean> AddFollower(string followerId);
        Task<List<UserSend>> GetFollowers(string id);
        Task<List<UserSend>> GetFollowing(string id);
        Task<List<UserSend>> GetMyFollowings();

        Task<Boolean> CheckIfAlreadyFollow(string id);
        Task<Boolean> Unfollow(string id);

        Task<List<UserSend>> GetMyFollowers();

        Task<int> ChangeMyProfileUsername(String newUsername);
        Task<bool> ChangeMyProfileName(String newUsername);

        Task<int> ChangePass(string currentPass, string newPass);


    }
}
