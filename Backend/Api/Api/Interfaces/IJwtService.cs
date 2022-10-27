using Api.Models;

namespace Api.Interfaces
{
    public interface IJwtService
    {
        Task<string> GenToken(User user);
        Task<string> TokenToId(string token);
        Task<string> RenewToken(string existingToken);

    }
}