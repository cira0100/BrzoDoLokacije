﻿using Api.Models;

namespace Api.Interfaces
{
    public interface IPostService
    {
        Task<PostSend> addPost(PostReceive post);
        Task<List<PostSend>> getAllPosts();
        Task<PostSend> getPostById(string id);
        Task<PostSend> postToPostSend(Post post);
    }
}