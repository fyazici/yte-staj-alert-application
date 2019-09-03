package yte.intern.alertapplication.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.repository.AlertRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final Clock clock;
    private final AlertRepository alertRepository;

    public Alert createAlert(AlertDTO alertDTO) {
        Alert alert = alertRepository.save(new Alert(
                alertDTO.getAlertName(), alertDTO.getAlertURL(), alertDTO.getHttpMethod(), alertDTO.getControlPeriod(),
                LocalDateTime.now(clock).plusSeconds(alertDTO.getControlPeriod())
        ));

        return alert;
    }

    public AlertDTO getAlertById(Long alertId) {
        Optional<Alert> maybeAlert = alertRepository.findById(alertId);
        if (maybeAlert.isEmpty()) {
            return null;
        } else {
            Alert alert = maybeAlert.get();
            return new AlertDTO(
                    alert.getId(), alert.getName(), alert.getUrl(), alert.getMethod(), alert.getPeriod()
            );
        }
    }

    public AlertDTO getAlertByName(String alertName) {
        Optional<Alert> maybeAlert = alertRepository.findByName(alertName);
        if (maybeAlert.isEmpty()) {
            return null;
        } else {
            Alert alert = maybeAlert.get();
            return new AlertDTO(
                    alert.getId(), alert.getName(), alert.getUrl(), alert.getMethod(), alert.getPeriod()
            );
        }
    }

    public List<AlertDTO> getAlerts(String alertNameLike) {
        List<Alert> alerts = alertRepository.findByNameIgnoreCaseContaining(alertNameLike);
        return alerts.stream().map(alert -> new AlertDTO(
                alert.getId(), alert.getName(), alert.getUrl(), alert.getMethod(), alert.getPeriod()
        )).collect(Collectors.toList());
    }

    public List<ResultDTO> getResultsById(Long alertId) {
        Optional<Alert> maybeAlert = alertRepository.findById(alertId);
        if (maybeAlert.isEmpty()) {
            return new ArrayList<>();
        } else {
            return maybeAlert.get().getResults().stream().map((result) -> new ResultDTO(
                    result.getRequestedAt().toString(), result.getStatusCode().equals(200), result.getElapsed()
            )).collect(Collectors.toList());
        }
    }

    public void deleteAlert(Long alertId) {
        if (alertId != null) {
            alertRepository.deleteById(alertId);
        }
    }

    public List<ResultDTO> getResultsByIdSinceMinutes(Long alertId, Long sinceMinutes) {
        Optional<Alert> maybeAlert = alertRepository.findById(alertId);
        if (maybeAlert.isEmpty()) {
            return new ArrayList<>();
        } else {
            // TODO: do time filtering in dbms
            LocalDateTime since = LocalDateTime.now(clock).minusMinutes(sinceMinutes);
            return maybeAlert.get().getResults().stream()
                    .filter((result) -> result.getRequestedAt().isAfter(since))
                    .map((result) -> new ResultDTO(
                        result.getRequestedAt().toString(), result.getStatusCode().equals(200), result.getElapsed()
            )).collect(Collectors.toList());
        }
    }
}
