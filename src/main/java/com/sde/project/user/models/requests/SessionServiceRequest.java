package com.sde.project.user.models.requests;

import java.util.Optional;
import java.util.UUID;

public record SessionServiceRequest(UUID userId, UUID roomId, String subject, Optional<String> topic, String startTime, String endTime) {
}

