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
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    @Override
    public void createAlert(AlertDTO alertDTO) {
        alertRepository.save(new Alert(
                alertDTO.getAlertName(), alertDTO.getAlertURL(), alertDTO.getHttpMethod(), alertDTO.getControlPeriod(),
                LocalDateTime.now().plusSeconds(alertDTO.getControlPeriod())
        ));
    }

    @Override
    public List<AlertDTO> getAlerts() {
        List<Alert> alerts = alertRepository.findAll();
        return alerts.stream().map(alert -> new AlertDTO(
                alert.getId(), alert.getName(), alert.getUrl(), alert.getMethod(), alert.getPeriod()
        )).collect(Collectors.toList());
    }

    @Override
    public List<ResultDTO> getResultsById(Long alertId) {
        Optional<Alert> alert = alertRepository.findById(alertId);
        if (alert.isEmpty()) {
            return new ArrayList<>();
        } else {
            return alert.get().getResults().stream().map((result) -> new ResultDTO(
                    result.getRequestedAt().toString(), result.getStatusCode().equals(200L)
            )).collect(Collectors.toList());
        }
    }
}
