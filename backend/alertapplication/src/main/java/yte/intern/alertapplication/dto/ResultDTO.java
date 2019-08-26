package yte.intern.alertapplication.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private String timestamp;
    private Boolean success;
    private Long elapsed;
}
