package com.ossowski.backend.model.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

}
