package yte.intern.alertapplication.service;

import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;

import java.util.List;

public interface AlertService {
    void createAlert(AlertDTO alertDTO);
    List<AlertDTO> getAlerts();
    List<ResultDTO> getResultsById(Long alertId);
}
