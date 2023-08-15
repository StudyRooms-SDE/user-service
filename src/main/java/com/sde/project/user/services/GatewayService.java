package com.sde.project.user.services;

import com.sde.project.user.models.requests.SessionServiceRequest;
import com.sde.project.user.models.responses.FileResponse;
import com.sde.project.user.models.responses.SessionDetailsResponse;
import com.sde.project.user.models.responses.SessionResponse;
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

    @Value("${service.session.url}")
    private String sessionServiceUrl;

    @Value("${service.file.url}")
    private String fileServiceUrl;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SessionResponse> getSessions() {
        SessionResponse[] array = restTemplate.getForObject(sessionServiceUrl, SessionResponse[].class);
        return List.of(array);
    }

    public List<SessionResponse> getUserSessions(String userId) {
        SessionResponse[] array = restTemplate.getForObject(sessionServiceUrl + "?userId=" + userId, SessionResponse[].class);
        return List.of(array);
    }

    public List<SessionResponse> getSessionsBySubject(String subject) {
        SessionResponse[] array = restTemplate.getForObject(sessionServiceUrl + "?subject=" + subject, SessionResponse[].class);
        return List.of(array);
    }

    public SessionDetailsResponse getSessionDetails(String userId, String sessionId) {
        return restTemplate.getForObject(sessionServiceUrl + "/" + sessionId + "?userId=" + userId, SessionDetailsResponse.class);
    }

    public void createSession(SessionServiceRequest request) {
        RequestEntity<SessionServiceRequest> requestEntity = RequestEntity.post(sessionServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);
        restTemplate.exchange(sessionServiceUrl, HttpMethod.POST, requestEntity, Void.class);
    }

    public void joinSession(String userId, String sessionId) {
        RequestEntity<Void> requestEntity = RequestEntity.post(sessionServiceUrl + "/" + sessionId + "/participate?userId=" + userId)
                .build();
        restTemplate.exchange(sessionServiceUrl + "/" + sessionId + "/participate?userId=" + userId, HttpMethod.POST, requestEntity, Void.class);
    }

    public void deleteSession(String userId, String sessionId) {
        restTemplate.delete(sessionServiceUrl + "/" + sessionId +  "?userId=" + userId);
    }


    public void uploadFile(String sessionId, MultipartFile file) {
        String url = fileServiceUrl + "/upload?sessionId=" + sessionId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, HttpEntity<?>> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("file", new HttpEntity<>(file.getResource(), headers));

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
        restTemplate.postForEntity(url, httpEntity, Void.class);
    }

    public void deleteFile(String fileId) {
        RequestEntity<Void> requestEntity = RequestEntity.delete(fileServiceUrl + "/" + fileId)
                .build();
        restTemplate.exchange(fileServiceUrl + "/" + fileId, HttpMethod.DELETE, requestEntity, Void.class);
    }

    public List<String> getSubjects() {
        String[] array = restTemplate.getForObject(sessionServiceUrl + "/subjects", String[].class);
        return List.of(array != null ? array : new String[0]);
    }

    public List<FileResponse> getFiles(String sessionId) {
        FileResponse[] array = restTemplate.getForObject(fileServiceUrl + "?sessionId=" + sessionId, FileResponse[].class);
        return List.of(array != null ? array : new FileResponse[0]);
    }

    public void leaveSession(String string, String sessionId) {
        restTemplate.postForEntity(sessionServiceUrl + "/" + sessionId + "/leave?userId=" + string, null, Void.class);
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
}
