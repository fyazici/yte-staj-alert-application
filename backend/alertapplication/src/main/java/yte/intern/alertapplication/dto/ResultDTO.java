package yte.intern.alertapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultDTO {
    private String timestamp;
    private Boolean success;
    private Long elapsed;
}
