package com.sde.project.database.models;

import java.util.Objects;

public record ExceptionResponse(String timestamp, int status, String error, String message, String path) {

    @Override
    public String toString() {
        return "{" +
                "timestamp:" + timestamp + ", " +
                "status:" + status + ", " +
                "error:" + error + ", " +
                "message:" + message + ", " +
                "path:" + path + '}';
    }


}




