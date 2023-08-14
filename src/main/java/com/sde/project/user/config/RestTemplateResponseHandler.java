package com.sde.project.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sde.project.user.models.responses.ExceptionResponse;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetailsNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    // throw new exception to be handled by the controller advice
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        ExceptionResponse exceptionResponse = new ObjectMapper().readValue(response.getBody(), ExceptionResponse.class);

        switch (exceptionResponse.status()) {
            case 400:
                throw new IllegalArgumentException(exceptionResponse.message());
            case 401:
                throw new PermissionDeniedDataAccessException(exceptionResponse.message(), new Throwable(exceptionResponse.message()));
            case 404:
                throw new DataRetrievalFailureException(exceptionResponse.message());
            case 409:
                throw new DataIntegrityViolationException(exceptionResponse.message());
            case 500:
                throw new IllegalStateException(exceptionResponse.message());
            case 502:
                throw new ConnectionDetailsNotFoundException(exceptionResponse.message());
            default:
                throw new RuntimeException(exceptionResponse.message());
        }
    }
}

