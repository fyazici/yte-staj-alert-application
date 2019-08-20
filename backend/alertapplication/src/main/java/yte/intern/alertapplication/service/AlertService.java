package yte.intern.alertapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.repository.AlertRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public void createAlert(AlertDTO alertDTO) {
        alertRepository.save(new Alert(
                alertDTO.getAlertName(), alertDTO.getAlertURL(), alertDTO.getHttpMethod(), alertDTO.getControlPeriod(),
                LocalDateTime.now().plusSeconds(alertDTO.getControlPeriod())
        ));
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

    public List<AlertDTO> getAlerts() {
        List<Alert> alerts = alertRepository.findAll();
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
}
