package com.sde.project.user.services;

import com.sde.project.user.models.tables.User;
import com.sde.project.user.repositories.UserRepository;
import com.sde.project.user.config.security.jwt.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public UserRepository getUserRepository(){
        return userRepository;
    }

    @Override
    @Transactional
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserFromToken(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsername(username).orElseThrow(() -> new DataRetrievalFailureException("User not found"));
    }

    public void checkUniqueUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new DataIntegrityViolationException("Username is already taken!");
        });

        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new DataIntegrityViolationException("Email is already taken!");
        });
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
