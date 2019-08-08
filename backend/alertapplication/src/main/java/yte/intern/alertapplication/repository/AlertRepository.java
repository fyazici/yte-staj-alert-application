package yte.intern.alertapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yte.intern.alertapplication.entity.Alert;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByNextDeadlineLessThan(final LocalDateTime now);
}
