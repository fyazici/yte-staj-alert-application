package yte.intern.alertapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yte.intern.alertapplication.entity.Alert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByNextDeadlineLessThan(final LocalDateTime now);
    Optional<Alert> findByName(final String alertName);
    List<Alert> findByNameIgnoreCaseContaining(final String name);
}
