package com.project.dadn.dtos.requests;


import com.project.website.enums.ModelEnum;
import com.project.website.utils.MessageKeys;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CarRequest {

    @Size(min = 3, message = MessageKeys.INVALID_REQUEST)
    private String name;

    @NotNull(message = MessageKeys.INVALID_ENUM)
    private ModelEnum model;

    @NotNull(message = MessageKeys.INVALID_ID)
    private Long manufacture;

    @NotNull(message = MessageKeys.INVALID_BUYDATE)
    private LocalDate buyDate;
}
