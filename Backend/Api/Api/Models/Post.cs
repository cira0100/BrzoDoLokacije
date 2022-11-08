﻿using MongoDB.Bson.Serialization.Attributes;
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
        public List<string> views { get; set; }
        public List<string> reports { get; set; }
        public List<Rating> ratings { get; set; }
        public List<Comment> comments { get; set; }
        public List<File> images { get; set; }
    }
    public class PostReceive
    {
        public string? _id { get; set; }
        public string locationId { get; set; }
        public string description { get; set; }
        public List<IFormFile> images { get; set; }


    }
    public class PostSend
    {
        public string _id { get; set; }
        public string ownerId { get; set; }
        public Location location { get; set; }
        public string description { get; set; }
        public int views { get; set; }
        public double ratings { get; set; }
        public List<CommentSend> comments { get; set; }
        public List<File> images { get; set; }
    }
    public class Rating
    {
        public string userId { get; set; }
        public int rating { get; set; }
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
}