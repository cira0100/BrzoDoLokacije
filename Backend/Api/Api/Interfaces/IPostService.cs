using Api.Models;

namespace Api.Interfaces
{
    public interface IPostService
    {
        PostSend addPost(PostReceive post);
    }
}