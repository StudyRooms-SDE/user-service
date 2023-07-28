package com.sde.project.database.controllers;

import com.sde.project.database.models.LoginRequest;
import com.sde.project.database.models.User;
import com.sde.project.database.repositories.UserRepository;
import com.sde.project.database.security.jwt.JwtUtils;
import com.sde.project.database.security.user.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController()
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    AuthenticationManager authenticationManager;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(path = "/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        System.err.println(username + " " + password);

        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(auth);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(null);
    }

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody User authRequest) {
        userRepository.findByUsername(authRequest.getUsername()).ifPresent(user -> {
            throw new DataIntegrityViolationException("Username is already taken!");
        });

        userRepository.findByEmail(authRequest.getEmail()).ifPresent(user -> {
            throw new DataIntegrityViolationException("Email is already taken!");
        });
        User user = new User(authRequest.getUsername(),
                passwordEncoder.encode(authRequest.getPassword()),
                authRequest.getEmail());

        userRepository.save(user);

        return ResponseEntity.created(null).body(null);
    }

    @PostMapping(path = "/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(null);
    }

}
