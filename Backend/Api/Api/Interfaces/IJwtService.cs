using Api.Models;

namespace Api.Interfaces
{
    public interface IJwtService
    {
        Task<string> GenToken(User user);
    }
}