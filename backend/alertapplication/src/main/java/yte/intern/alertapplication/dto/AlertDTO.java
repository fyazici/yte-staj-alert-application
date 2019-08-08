package yte.intern.alertapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AlertDTO {
    private Long alertId;
    private String alertName;
    private String alertURL;
    private String httpMethod;
    private Long controlPeriod;
}
