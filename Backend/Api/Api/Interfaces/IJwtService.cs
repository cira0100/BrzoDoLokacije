using Api.Models;

namespace Api.Interfaces
{
    public interface IJwtService
    {
        string GenToken(User user);
        string TokenToId(string token);
        public string GenEmailToken(string username);
        public string EmailTokenToId(string token);
    }
}