package yte.intern.alertapplication.entity;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="alerts")
@Getter
@Setter
@NoArgsConstructor
public class Alert {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @URL
    private String url;

    private String method;
    private Long period;
    private LocalDateTime nextDeadline;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "alert")
    private Set<Result> results;

    public Alert(String name, String url, String method, Long period, LocalDateTime nextDeadline) {
        this.name = name;
        this.url = url;
        this.method = method;
        this.period = period;
        this.nextDeadline = nextDeadline;
    }
}
