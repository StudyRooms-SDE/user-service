package com.sde.project.user.services;

import com.sde.project.user.models.responses.SessionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GatewayService {
    private final RestTemplate restTemplate;

    @Value("${service.session.url}")
    private String sessionServiceUrl;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SessionResponse> getUserSessions(String userId) {
         SessionResponse[] array = restTemplate.getForObject(sessionServiceUrl+"?userId="+userId, SessionResponse[].class);
            return List.of(array);
    }


}
