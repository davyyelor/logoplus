package com.logopeda.homework.mapper;

import com.logopeda.homework.dto.HomeworkResponse;
import com.logopeda.homework.model.Homework;
import org.springframework.stereotype.Component;

@Component
public class HomeworkMapper {

    public HomeworkResponse toResponse(Homework homework) {
        return new HomeworkResponse(
                homework.getId(),
                homework.getClinicId(),
                homework.getPatientId(),
                homework.getSessionId(),
                homework.getCreatedByUserId(),
                homework.getTitle(),
                homework.getDescription(),
                homework.getInstructions(),
                homework.getDueDate(),
                homework.getStatus(),
                homework.isVisibleToFamily(),
                homework.getCreatedAt(),
                homework.getUpdatedAt());
    }
}
