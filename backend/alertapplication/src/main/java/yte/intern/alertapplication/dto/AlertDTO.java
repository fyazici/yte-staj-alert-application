package yte.intern.alertapplication.dto;

import lombok.*;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlertDTO {
    private Long alertId;
    private String alertName;
    private String alertURL;
    private String httpMethod;
    private Long controlPeriod;
}
