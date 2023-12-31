package com.sde.project.user.controllers;

import com.sde.project.user.models.responses.LoginRequest;
import com.sde.project.user.models.responses.RegisterRequest;
import com.sde.project.user.models.tables.User;
import com.sde.project.user.services.UserService;
import com.sde.project.user.config.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    AuthenticationManager authenticationManager;

    UserService userService;

    PasswordEncoder passwordEncoder;

    JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(path = "/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();

        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(auth);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDetails = (User) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(null);
    }

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest userRequest) {
        User user = new User(userRequest.username(), userRequest.password(), userRequest.email());
        userService.checkUniqueUser(user);

        User encodedUser = new User(user.getUsername(),
                passwordEncoder.encode(user.getPassword()),
                user.getEmail());

        userService.getUserRepository().save(encodedUser);
    }

    @PostMapping(path = "/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logoutUser() {

    }

}
