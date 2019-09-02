package yte.intern.alertapplication.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.entity.Result;
import yte.intern.alertapplication.repository.AlertRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertRequestSchedulerService {

    private final Clock clock;
    private final AlertRepository alertRepository;
    private final RestTemplate restTemplate;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    @Scheduled(fixedDelay = 1000)
    public void scheduleRequests() {
        List<Alert> alerts = alertRepository.findByNextDeadlineLessThan(LocalDateTime.now(clock));
        alerts.forEach(this::requestWorker);
    }

    @Async
    @SuppressWarnings("WeakerAccess")
    public void requestWorker(Alert alert) {
        System.out.println(LocalDateTime.now(clock).toString() + " (" + alert.getName() + ") async request worker running");

        LocalDateTime requestTime = LocalDateTime.now(clock);
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

        ResultDTO resultDTO = new ResultDTO(
                result.getRequestedAt().toString(), result.getStatusCode().equals(200), result.getElapsed());

        brokerMessagingTemplate.convertAndSend("/topic/" + result.getAlert().getId(), resultDTO);
    }
}
