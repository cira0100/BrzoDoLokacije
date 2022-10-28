using Api.Models;

namespace Api.Interfaces
{
    public interface IJwtService
    {
        string GenToken(User user);
        string TokenToId(string token);
        Task<string> RenewToken(string existingToken);
        public string GenEmailToken(User user);
        public string EmailTokenToId(string token);
    }
}