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

        Task<string> RenewToken(string existingToken);
        Task<string> Login(Login login);
        Task<string> Register(Register register);
        Task<Boolean> VerifyUser(string _id);
        Task<string> UserIdFromJwt();

    }
}
