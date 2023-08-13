package com.sde.project.user.controllers;

import com.sde.project.user.models.tables.User;
import com.sde.project.user.services.UserService;
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
    public User getUser(@CookieValue("jwt") String token) {
        return userService.getUserFromToken(token);
    }
}
