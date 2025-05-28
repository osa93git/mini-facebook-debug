package com.ossowski.backend.exceptions.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
   int status,
   String error,
   String message,
   LocalDateTime timestamp
){}
