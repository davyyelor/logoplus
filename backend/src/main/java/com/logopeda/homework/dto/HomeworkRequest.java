package com.logopeda.homework.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record HomeworkRequest(
        String sessionId,
        @NotBlank String title,
        String description,
        String instructions,
        LocalDate dueDate,
        Boolean visibleToFamily) {
}
