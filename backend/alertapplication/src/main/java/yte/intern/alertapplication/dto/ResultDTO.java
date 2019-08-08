package yte.intern.alertapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ResultDTO {
    private String timestamp;
    private Boolean success;
}
