package com.sde.project.database.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "api/v1/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createUser(@RequestBody Map<String, String> userMap) {
        userService.createUser(userMap);
    }

    @PostMapping(path = "api/v1/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public void login(@RequestParam Map<String, String> userMap) {
        System.out.println(userMap);
        userService.login(userMap);
    }

    @GetMapping(path = "api/v1/user")
    @ResponseStatus(code = HttpStatus.OK)
    public User getUser(Principal principal) {
        System.out.println(principal.getName());
        return userService.getUser(UUID.fromString(principal.getName()));
    }

    @GetMapping(path = "api/v1/users")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }

//
//    @DeleteMapping(path = "api/v1/users/{id}")
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    public void deleteUser(@PathVariable("id") UUID id) {
//        userService.deleteUser(id);
//    }
//
//    @PutMapping(path = "api/v1/users/{id}")
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    public void updateUser(@PathVariable("id") UUID id, @RequestBody Map<String, String> userMap) {
//        userService.updateUser(id, userMap);
//    }


}
