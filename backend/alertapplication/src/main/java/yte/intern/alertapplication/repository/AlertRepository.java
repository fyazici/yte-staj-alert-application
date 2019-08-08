package yte.intern.alertapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yte.intern.alertapplication.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
