package yte.intern.alertapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name="results")
@Getter
@Setter
@NoArgsConstructor
public class Result {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_alert_id")
    private Alert alert;

    private LocalDateTime requestedAt;
    private Long elapsed;
    private Integer statusCode;

    public Result(Alert alert, LocalDateTime requestedAt, Long elapsed, Integer statusCode) {
        this.alert = alert;
        this.requestedAt = requestedAt;
        this.elapsed = elapsed;
        this.statusCode = statusCode;
    }
}
