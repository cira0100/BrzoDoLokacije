using Api.Models;

namespace Api.Interfaces
{
    public interface IPostService
    {
        Task<PostSend> addPost(PostReceive post);
        Task<Boolean> deletePost(string postid, string userid);
        Task<List<PostSend>> getAllPosts();
        Task<PostSend> getPostById(string id,string userid);
        Task<PostSend> postToPostSend(Post post);
        Task<Boolean> AddOrReplaceRating(RatingReceive rating, string userid);
        Task<Boolean> RemoveRating(string postid, string userid);
        Task<CommentSend> AddComment(CommentReceive cmnt, string userid, string postid);
        Task<List<CommentSend>> ListComments(string postid);
        Task<List<CommentSend>> CascadeComments(string parentid, Post p);
        Task<Boolean> DeleteComments(string postid, string cmntid,string userid);
        Task CascadeDeleteComments(string cmntid, Post p);
        Task<PostSendPage> SearchPosts(string locid, int page = 0, int sorttype = 1, int filterdate = 1);
        int DateEnumToDays(int filterdate);
        Task<List<PostSend>> GetUsersPosts(string id);
        Task<List<PostSend>> UserHistory(string userid);

        Task<List<PostSend>> Get10Best();

        Task<List<PostSend>> Get10MostViewed();

        Task<List<PostSend>> Get10Newest();
    }
}