package yte.intern.alertapplication.entity;

import lombok.*;

import javax.persistence.*;
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
    private String url;
    private String method;
    private Long period;

    @OneToMany(mappedBy = "alert")
    private Set<Result> results;

    public Alert(String name, String url, String method, Long period) {
        this.name = name;
        this.url = url;
        this.method = method;
        this.period = period;
    }
}
