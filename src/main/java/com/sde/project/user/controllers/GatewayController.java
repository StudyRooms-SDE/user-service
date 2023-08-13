package com.sde.project.user.controllers;

import com.sde.project.user.models.responses.SessionDetailsResponse;
import com.sde.project.user.models.responses.SessionResponse;
import com.sde.project.user.models.tables.User;
import com.sde.project.user.services.GatewayService;
import com.sde.project.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/")
public class GatewayController {
    private final GatewayService gatewayService;
    private final UserService userService;

    public GatewayController(GatewayService gatewayService, UserService userService) {
        this.gatewayService = gatewayService;
        this.userService = userService;
    }

    @GetMapping(path = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SessionResponse> getUserSessions(@CookieValue("jwt") String token) {
        User user = userService.getUserFromToken(token);
        return gatewayService.getUserSessions(user.getId().toString());
    }

    @GetMapping(path = "/sessions/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionDetailsResponse getSessionDetails(@CookieValue("jwt") String token, @PathVariable String sessionId) {
        User user = userService.getUserFromToken(token);
        return gatewayService.getSessionDetails(user.getId().toString(), sessionId);
    }
}