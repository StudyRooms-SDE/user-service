package com.sde.project.user.models.responses;

import java.util.UUID;

public record RoomDetailsResponse (UUID id, String name, String building, String description, Double latitude, Double longitude, String address) {
}

