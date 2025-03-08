package com.project.dadn.dtos.responses;

import com.project.website.enums.ModelEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CarResponse {

    private String name;

    private ModelEnum model;

    private String manufacture;

    private LocalDate buyDate;
}
