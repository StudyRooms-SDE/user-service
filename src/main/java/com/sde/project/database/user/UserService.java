package com.sde.project.database.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UserService {
    protected final UserRepository userRepository;
    protected final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository studentRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(Map<String, String> user) {
        String email = user.get("email");
        String username = user.get("username");
        String password = passwordEncoder.encode(user.get("password"));
        if (checkEmailUnique(email) && checkUsernameUnique(username)) {
            userRepository.save(new User(username, password, email));
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DataRetrievalFailureException("User does not exist") );
    }

    public void deleteUser(UUID id) {
        userRepository.findById(id).orElseThrow(() -> new DataRetrievalFailureException("User does not exist") );

        userRepository.deleteById(id);
    }

    public void updateUser(UUID id, Map<String, String> userMap) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataRetrievalFailureException("User does not exist") );
        String email = userMap.get("email");

        if (email != null && email.length() > 0 && checkEmailUnique(email)) {
            user.setEmail(email);
            userRepository.save(user);
        } else {
            throw new DataIntegrityViolationException("Email not specified");
        }

    }

    public void login(Map<String, String> userMap) {
        String username = userMap.get("username");
        String password = userMap.get("password");
        if (username != null && password != null) {
            User user = userRepository.findByUsername(username).orElseThrow(() -> new PermissionDeniedDataAccessException("Wrong credentials", new Throwable()));
            String encodedPassword = passwordEncoder.encode(password);
            if (passwordEncoder.matches(encodedPassword, user.getPassword())) {
                throw new PermissionDeniedDataAccessException("Wrong credentials", new Throwable());
            }
        } else {
            throw new PermissionDeniedDataAccessException("Wrong credentials", new Throwable());
        }
    }
    private boolean checkEmailUnique(String email) {
        try (Stream<User> userStream = userRepository.findAll().stream()) {
            if (userStream.anyMatch(user1 -> user1.getEmail().equals(email))) {
                throw new DataIntegrityViolationException("Email already in use");
            }
        }
        return true;
    }

    private boolean checkUsernameUnique(String username) {
        try (Stream<User> userStream = userRepository.findAll().stream()) {
            if (userStream.anyMatch(user1 -> user1.getUsername().equals(username))) {
                throw new DataIntegrityViolationException("Username already in use");
            }
        }
        return true;
    }

}
