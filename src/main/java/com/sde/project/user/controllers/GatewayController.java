package com.sde.project.user.controllers;

import com.sde.project.user.models.requests.SessionFrontendRequest;
import com.sde.project.user.models.requests.SessionServiceRequest;
import com.sde.project.user.models.responses.FileResponse;
import com.sde.project.user.models.responses.RoomResponse;
import com.sde.project.user.models.responses.SessionDetailsResponse;
import com.sde.project.user.models.responses.SessionResponse;
import com.sde.project.user.models.tables.User;
import com.sde.project.user.services.GatewayService;
import com.sde.project.user.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    @ResponseStatus(HttpStatus.OK)
    public List<SessionResponse> getSessions(@RequestParam Optional<String> subject) {
        return subject.isPresent() ?
                gatewayService.getSessionsBySubject(subject.get()) :
                gatewayService.getSessions();
    }

    @GetMapping(path = "/sessions/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<SessionResponse> getUserSessions(@CookieValue("jwt") String token) {
        User user = userService.getUserFromToken(token);
        return gatewayService.getUserSessions(user.getId().toString());
    }

    @GetMapping(path = "/sessions/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<String> getSubjects() {
        return gatewayService.getSubjects();
    }

    @GetMapping(path = "/sessions/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SessionDetailsResponse getSessionDetails(@CookieValue("jwt") String token, @PathVariable String sessionId) {
        User user = userService.getUserFromToken(token);
        return gatewayService.getSessionDetails(user.getId().toString(), sessionId);
    }

    @DeleteMapping(path = "/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@CookieValue("jwt") String token, @PathVariable String sessionId) {
        User user = userService.getUserFromToken(token);
        gatewayService.deleteSession(user.getId().toString(), sessionId);
    }

    @PostMapping(path = "/sessions", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createSession(@CookieValue("jwt") String token, @RequestBody SessionFrontendRequest request) {
        User user = userService.getUserFromToken(token);
        gatewayService.createSession(new SessionServiceRequest(
                user.getId(),
                request.roomId(),
                request.subject(),
                request.topic(),
                request.startTime(),
                request.endTime()
        ));
    }

    @PostMapping(path = "/sessions/{sessionId}/participate")
    @ResponseStatus(HttpStatus.CREATED)
    public void participate(@CookieValue("jwt") String token, @PathVariable String sessionId) {
        User user = userService.getUserFromToken(token);
        gatewayService.joinSession(user.getId().toString(), sessionId);
    }

     @PostMapping(path = "/sessions/{sessionId}/leave")
     @ResponseStatus(HttpStatus.NO_CONTENT)
        public void leave(@CookieValue("jwt") String token, @PathVariable String sessionId) {
            User user = userService.getUserFromToken(token);
            gatewayService.leaveSession(user.getId().toString(), sessionId);
        }

    @GetMapping(path = "/sessions/{sessionId}/files", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<FileResponse> getFiles(@PathVariable String sessionId) {
        return gatewayService.getFiles(sessionId);
    }

    @PostMapping(path = "/sessions/{sessionId}/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@CookieValue("jwt") String token, @PathVariable("sessionId") String sessionId, @RequestParam("file") MultipartFile file) {
        User user = userService.getUserFromToken(token);
        gatewayService.uploadFile(sessionId, file);
    }

    @DeleteMapping(path = "/sessions/{sessionId}/files/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable String fileId) {
        gatewayService.deleteFile(fileId);
    }

    @GetMapping(path = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getRooms() {
        return gatewayService.getRooms();
    }
}
