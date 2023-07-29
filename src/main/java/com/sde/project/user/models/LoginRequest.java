package com.sde.project.user.models;

import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @NotNull(message = "Username cannot be empty")
    private String username;
    @NotNull(message = "Password cannot be empty")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

}
