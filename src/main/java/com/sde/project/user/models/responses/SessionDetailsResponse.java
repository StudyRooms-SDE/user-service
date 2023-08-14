package com.sde.project.user.models.responses;

import java.util.List;
import java.util.UUID;

public record SessionDetailsResponse(UUID id, String subject, String topic, String startTime, String endTime, Boolean createdByUser, RoomDetailsResponse room, List<FileResponse> files) {
}