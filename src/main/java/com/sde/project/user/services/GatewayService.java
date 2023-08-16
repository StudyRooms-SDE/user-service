package com.sde.project.user.services;

import com.sde.project.user.models.requests.SessionServiceRequest;
import com.sde.project.user.models.responses.FileResponse;
import com.sde.project.user.models.responses.RoomResponse;
import com.sde.project.user.models.responses.SessionDetailsResponse;
import com.sde.project.user.models.responses.SessionResponse;
import com.sun.net.httpserver.Headers;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class GatewayService {
    private final RestTemplate restTemplate;
    private HttpHeaders headers;

    private HttpEntity<?> requestEntity;

    @Value("${x.auth.secret}")
    private String xAuthSecretKey;

    @Value("${service.session.url}")
    private String sessionServiceUrl;

    @Value("${service.file.url}")
    private String fileServiceUrl;

    @Value("${service.room.url}")
    private String roomServiceUrl;

    public GatewayService(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.headers = new HttpHeaders();
    }

    @PostConstruct
    private void initializeToken() {
        headers.add("x-auth-secret-key", xAuthSecretKey);
        requestEntity = new HttpEntity<>(headers);
    }

    public List<SessionResponse> getSessions() {
        SessionResponse[] array = restTemplate.exchange(sessionServiceUrl, HttpMethod.GET, requestEntity, SessionResponse[].class).getBody();
        return List.of(array);
    }

    public List<SessionResponse> getUserSessions(String userId) {
        SessionResponse[] array = restTemplate.exchange(sessionServiceUrl + "?userId=" + userId, HttpMethod.GET, requestEntity, SessionResponse[].class).getBody();
        return List.of(array);
    }

    public List<SessionResponse> getSessionsBySubject(String subject) {
        SessionResponse[] array = restTemplate.exchange(sessionServiceUrl + "?subject=" + subject, HttpMethod.GET, requestEntity, SessionResponse[].class).getBody();
        return List.of(array);
    }

    public SessionDetailsResponse getSessionDetails(String userId, String sessionId) {
        return restTemplate.exchange(sessionServiceUrl + "/" + sessionId + "?userId=" + userId, HttpMethod.GET, requestEntity, SessionDetailsResponse.class).getBody();
    }

    public void createSession(SessionServiceRequest request) {
        RequestEntity<SessionServiceRequest> requestEntity = RequestEntity.post(sessionServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(request);
        restTemplate.exchange(sessionServiceUrl, HttpMethod.POST, requestEntity, Void.class);
    }

    public void joinSession(String userId, String sessionId) {
        RequestEntity<Void> requestEntity = RequestEntity.post(sessionServiceUrl + "/" + sessionId + "/participate?userId=" + userId)
                .headers(headers)
                .build();
        restTemplate.exchange(sessionServiceUrl + "/" + sessionId + "/participate?userId=" + userId, HttpMethod.POST, requestEntity, Void.class);
    }

    public void deleteSession(String userId, String sessionId) {
        restTemplate.exchange(sessionServiceUrl + "/" + sessionId + "?userId=" + userId, HttpMethod.DELETE, requestEntity, Void.class);
    }


    public void uploadFile(String sessionId, MultipartFile file) {
        String url = fileServiceUrl + "/upload?sessionId=" + sessionId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("x-auth-secret-key", xAuthSecretKey);
        MultiValueMap<String, HttpEntity<?>> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("file", new HttpEntity<>(file.getResource(), headers));

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
        restTemplate.postForEntity(url, httpEntity, Void.class);
    }

    public void deleteFile(String fileId) {
        RequestEntity<Void> requestEntity = RequestEntity.delete(fileServiceUrl + "/" + fileId)
                .headers(headers)
                .build();
        restTemplate.exchange(fileServiceUrl + "/" + fileId, HttpMethod.DELETE, requestEntity, Void.class);
    }

    public List<String> getSubjects() {
        String[] array = restTemplate.exchange(sessionServiceUrl + "/subjects", HttpMethod.GET, requestEntity, String[].class).getBody();
        return List.of(array != null ? array : new String[0]);
    }

    public List<FileResponse> getFiles(String sessionId) {
        FileResponse[] array = restTemplate.exchange(fileServiceUrl + "?sessionId=" + sessionId, HttpMethod.GET, requestEntity, FileResponse[].class).getBody();
        return List.of(array != null ? array : new FileResponse[0]);
    }

    public void leaveSession(String string, String sessionId) {
        RequestEntity<Void> requestEntity = RequestEntity.post(sessionServiceUrl + "/" + sessionId + "/leave?userId=" + string)
                .headers(headers)
                .build();
        restTemplate.postForEntity(sessionServiceUrl + "/" + sessionId + "/leave?userId=" + string, requestEntity, Void.class);
    }

    public void deleteUserSessions(String userId) {
        getUserSessions(userId).forEach(session -> {
            if (session.createdByUser()) {
                deleteSession(userId, session.sessionId().toString());
            } else {
                leaveSession(userId, session.sessionId().toString());
            }
        });
    }

    public List<RoomResponse> getRooms() {
        RoomResponse[] array = restTemplate.exchange(roomServiceUrl, HttpMethod.GET, requestEntity, RoomResponse[].class).getBody();
        return List.of(array != null ? array : new RoomResponse[0]);
    }
}
