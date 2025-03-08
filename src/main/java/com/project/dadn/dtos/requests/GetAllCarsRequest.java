package com.project.dadn.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllCarsRequest {
        private String keyword = "";
        private Long manufactureId = 0L;
        Pageable pageable;


}
