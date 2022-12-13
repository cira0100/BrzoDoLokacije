using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using System.ComponentModel.DataAnnotations;

namespace Api.Models
{
    public class Post
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string _id { get; set; }
        public string ownerId { get; set; }
        public string locationId { get; set; }
        public string description { get; set; }
        public DateTime createdAt { get; set; }
        public List<string> views { get; set; }
        public List<string> reports { get; set; }
        public List<Rating> ratings { get; set; }
        public List<Comment> comments { get; set; }
        public List<File> images { get; set; }
        public List<string>? tags { get; set; }
        public List<string>? favourites { get; set; }
        
    }
    public class PostReceive
    {
        public string? _id { get; set; }
        public string locationId { get; set; }
        public string description { get; set; }
        public List<IFormFile> images { get; set; }
        public string? tags { get; set; }
    }
    public class PostSend
    {
        public string _id { get; set; }
        public string ownerId { get; set; }
        public Location location { get; set; }
        public string description { get; set; }
        public DateTime createdAt { get; set; }
        public int views { get; set; }
        public double ratings { get; set; }
        public int ratingscount { get; set; }
        public List<CommentSend> comments { get; set; }
        public List<File> images { get; set; }
        public List<string>? tags { get; set; }
        public DateTime? lastViewed { get; set; }
        public List<string>? favourites { get; set; }
    }
    public class Rating
    {
        public string userId { get; set; }
        public int rating { get; set; }
    }
    public class RatingSend
    {
        public int ratingscount { get; set; }
        public double ratings { get; set; }
        public int myrating { get; set; }
    }
    public class Comment
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string _id { get; set; }
        public string userId { get; set; }
        public string comment { get; set; }
        public string parentId { get; set; }
        public DateTime timestamp { get; set; }
    }

    public class RatingReceive
    {
        public int rating { get; set; }
        public string postId { get; set; }
    }
    public class CommentSend
    {
        public string _id { get; set; }
        public string userId { get; set; }
        public string comment { get; set; }
        public string? parentId { get; set; }
        public DateTime timestamp { get; set; }
        public string username { get; set; }
        public List<CommentSend> replies { get; set; }
    }
    public class CommentReceive
    {
        public string comment { get; set; }
        public string parentId { get; set; }
    }
    public enum SortType
    {
        VIEWS_DESC=1,
        RATING_DESC=2,
        DATE =3,
        DATE_ASC=4,

    }
    public enum FilterDate
    {
        ALL =1,
        ONE_YEAR=2 ,
        THREE_MONTHS=3 ,
        ONE_MONTH=4 ,
        ONE_WEEK=5
    }
    public class PostSendPage
    {
        public int page { get; set; }
        public int index { get; set; }
        public int totalpages { get; set; }
        public int totalposts { get; set; }
        public List<PostSend> posts { get; set; }
    }

    public class TagR
    {
        public int counter { get; set; }
        public string tag { get; set; } 
        public int? views { get; set; }
    }

    public class Trending
    {
        public TagR tagr { get; set; }
        public PostSendPage page { get; set; }
    }

    public class FilterSort
    {
        public List<PostSend> posts { get; set; }
        public bool sort { get; set; }
        public bool filter { get; set; }
        public bool filterDate { get; set; }
        public bool filterRating { get; set; }
        public bool filterViews{ get; set; }
        public DateTime filterDateFrom { get; set; }
        public DateTime filterDateTo { get; set; }
        public int filterRatingFrom { get; set; }
        public int filterRatingTo { get; set; }
        public int filterViewsFrom { get; set; }
        public int filterViewsTo { get; set; }

        public bool sortLatest { get; set; }    
        public bool sortOldest { get; set; }
        public bool sortBest{ get; set; }
        public bool sortMostViews{ get; set; }






    }
}
