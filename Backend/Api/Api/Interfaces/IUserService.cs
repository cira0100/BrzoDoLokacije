using Api.Models;

namespace Api.Interfaces
{
    public interface IUserService
    {
        User createUser(User user);
        List<User> getUsers();
        User getUserByEmail(String email);
        User getUserByUsername(String username);
        User updateUser(User user);
        Boolean deleteUser(String email);
    }
}
