package yte.intern.alertapplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.service.AlertService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/alerts")
    public void createAlert(@RequestBody AlertDTO alertDTO) {
        alertService.createAlert(alertDTO);
    }

    @GetMapping("/alerts")
    public List<AlertDTO> getAlerts() {
        return alertService.getAlerts();
    }

    @GetMapping("/alert/{alertId}")
    public AlertDTO getAlertById(@PathVariable Long alertId) {
        return alertService.getAlertById(alertId);
    }

    @GetMapping("/results/{alertId}")
    public List<ResultDTO> getResultsById(@PathVariable Long alertId) {
        return alertService.getResultsById(alertId);
    }
}