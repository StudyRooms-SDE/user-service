package com.sde.project.user.controllers;

import com.sde.project.user.models.tables.User;
import com.sde.project.user.services.GatewayService;
import com.sde.project.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/")
public class UserController {
    UserService userService;
    GatewayService gatewayService;

    @Autowired
    public UserController(UserService userService, GatewayService gatewayService) {
        this.userService = userService;
        this.gatewayService = gatewayService;
    }
    @GetMapping(path = "/me")
    public User getUser(@CookieValue("jwt") String token) {
        return userService.getUserFromToken(token);
    }

    @DeleteMapping(path = "/me")
    public void deleteUser(@CookieValue("jwt") String token) {
        User user = userService.getUserFromToken(token);
        gatewayService.deleteUserSessions(user.getId().toString());
        userService.deleteUser(user.getId());
    }
}
