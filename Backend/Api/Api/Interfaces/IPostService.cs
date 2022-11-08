using Api.Models;

namespace Api.Interfaces
{
    public interface IPostService
    {
        Task<PostSend> addPost(PostReceive post);
        Task<List<PostSend>> getAllPosts();
        Task<PostSend> getPostById(string id,string userid);
        Task<PostSend> postToPostSend(Post post);
        Task<Boolean> AddOrReplaceRating(RatingReceive rating, string userid);
        Task<Boolean> RemoveRating(string postid, string userid);
        Task<Boolean> AddComment(CommentReceive cmnt, string userid, string postid);
        Task<List<CommentSend>> ListComments(string postid);
        Task<List<CommentSend>> CascadeComments(string parentid, Post p);
        Task<Boolean> DeleteComments(string postid, string cmntid,string userid);
        Task CascadeDeleteComments(string cmntid, Post p);
    }
}