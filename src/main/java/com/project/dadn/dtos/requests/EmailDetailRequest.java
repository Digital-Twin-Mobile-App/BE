package com.project.dadn.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetailRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recipient;
    private String msgBody;
    private String subject;
}

