package yte.intern.alertapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.service.AlertService;

import java.util.List;

@RestController
@CrossOrigin("*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping("/alerts")
    public void createAlert(@RequestBody AlertDTO alertDTO) {
        alertService.createAlert(alertDTO);
    }

    @GetMapping("/alerts")
    public List<AlertDTO> getAlerts() {
        return alertService.getAlerts();
    }

    @GetMapping("/results/{alertId}")
    public List<ResultDTO> getResultsById(@PathVariable Long alertId) {
        return alertService.getResultsById(alertId);
    }
}
