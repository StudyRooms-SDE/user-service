package com.sde.project.database.controllers;

import com.sde.project.database.models.User;
import com.sde.project.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping(path = "/me")
    public User getUser(@RequestHeader("Authorization") String token) {
        return userService.getUserFromToken(token);
    }
}
