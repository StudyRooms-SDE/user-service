package com.sde.project.user.models.responses;

import java.util.UUID;

public record RoomResponse(UUID id, String name, String building, String description)  {
}
