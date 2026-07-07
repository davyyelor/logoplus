package com.logopeda.homework.dto;

import com.logopeda.homework.enums.HomeworkStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateHomeworkStatusRequest(@NotNull HomeworkStatus status) {
}
