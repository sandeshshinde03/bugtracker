package com.bugtracker.serviceImpl;

import com.bugtracker.entity.User;
import com.bugtracker.repository.UserRepository;
import com.bugtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public List<User> getDevelopers() {

        return userRepository.findByRole("ROLE_DEVELOPER");
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}