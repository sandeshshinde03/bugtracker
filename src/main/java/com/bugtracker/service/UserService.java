package com.bugtracker.service;

import java.util.List;

import com.bugtracker.entity.User;

public interface UserService {

    List<User> getAllUsers();

    List<User> getDevelopers();
    
    User getUserById(Long id);
}