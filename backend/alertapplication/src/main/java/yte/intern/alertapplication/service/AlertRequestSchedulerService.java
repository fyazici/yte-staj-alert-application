package yte.intern.alertapplication.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = 1000)
    public void scheduleRequests() {
        List<Alert> alerts = alertRepository.findByNextDeadlineLessThan(LocalDateTime.now());
        alerts.forEach(this::requestWorker);
    }

    @Async
    @SuppressWarnings("WeakerAccess")
    public void requestWorker(Alert alert) {
        System.out.println(LocalDateTime.now().toString() + " (" + alert.getName() + ") async request worker running");

        LocalDateTime requestTime = LocalDateTime.now();
        alert.setNextDeadline(requestTime.plusSeconds(alert.getPeriod()));

        Result result = new Result();
        result.setAlert(alert);
        result.setRequestedAt(requestTime);

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ResponseEntity<String> resp = restTemplate.exchange(
                    alert.getUrl(),
                    HttpMethod.resolve(alert.getMethod()),
                    null,
                    String.class
            );
            stopWatch.stop();
            result.setElapsed(stopWatch.getLastTaskTimeMillis());
            result.setStatusCode(resp.getStatusCode().value());
        } catch (HttpClientErrorException ex) {
            System.err.println("request to " + alert.getName() + " threw: " + ex.toString());
            result.setStatusCode(ex.getRawStatusCode());
        } catch (Exception ex) {
            System.err.println("request to " + alert.getName() + " threw: " + ex.toString());
            result.setStatusCode(-1);
        } finally {
            alert.getResults().add(result);
            alertRepository.save(alert);
        }
    }
}