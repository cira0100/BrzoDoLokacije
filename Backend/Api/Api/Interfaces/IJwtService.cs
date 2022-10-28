using Api.Models;

namespace Api.Interfaces
{
    public interface IJwtService
    {
        string GenToken(User user);
        string TokenToId(string token);
        public string GenEmailToken(User user);
        public string EmailTokenToId(string token);
    }
}