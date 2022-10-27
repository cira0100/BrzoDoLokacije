﻿using Api.Models;

namespace Api.Interfaces
{
    public interface IUserService
    {
        Task createUser(User user);
        Task<List<User>> getUsers();
        Task<User> getUserByEmail(String email);
        Task<User> getUserByUsername(String username);
        Task<long> updateUser(User user);
        Task<User> deleteUser(String email);
        Task<User> getUserById(string id);
    }
}
