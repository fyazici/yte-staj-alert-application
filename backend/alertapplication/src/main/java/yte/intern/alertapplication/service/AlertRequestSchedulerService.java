package yte.intern.alertapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.entity.Result;
import yte.intern.alertapplication.repository.AlertRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class AlertRequestSchedulerService {

    private final AlertRepository alertRepository;

    @Scheduled(fixedDelay = 1000)
    public void scheduleRequests() {
        List<Alert> alerts = alertRepository.findByNextDeadlineLessThan(LocalDateTime.now());
        alerts.forEach(this::requestWorker);
    }

    @Async
    @SuppressWarnings("WeakerAccess")
    public void requestWorker(Alert alert) {
        try {
            System.out.println(LocalDateTime.now().toString() + " (" + alert.getName() + ") async request worker running");
            Thread.sleep(500L);
        } catch (InterruptedException ex) {
            System.err.println("request worker caught: " + ex.toString());
        } finally {
            alert.getResults().add(new Result(alert, LocalDateTime.now(), 100L, 200L));
            alert.setNextDeadline(LocalDateTime.now().plusSeconds(alert.getPeriod()));
            alertRepository.save(alert);
        }
    }
}
