package com.sde.project.database.controllers;

import com.sde.project.database.models.User;
import com.sde.project.database.repositories.UserRepository;
import com.sde.project.database.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/")
public class UserController {
    UserRepository userRepository;

    JwtUtils jwtUtils;

    @Autowired
    public UserController(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }
    @GetMapping(path = "/me")
    public User getUser(@RequestHeader("Authorization") String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));
        return userRepository.findByUsername(username).orElseThrow(() -> new DataRetrievalFailureException("User not found"));
    }
}
