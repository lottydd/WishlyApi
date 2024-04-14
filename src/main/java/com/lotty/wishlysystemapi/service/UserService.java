package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    boolean deleteUser(Long id);
    User getUserById(Long id);
    List<User> getAllUsers();
}