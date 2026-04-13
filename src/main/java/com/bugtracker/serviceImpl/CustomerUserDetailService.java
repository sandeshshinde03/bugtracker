package com.bugtracker.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.bugtracker.entity.User;
import com.bugtracker.repository.UserRepository;

@Service
public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.Collections.singleton(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole())
                )
        );
    }
}