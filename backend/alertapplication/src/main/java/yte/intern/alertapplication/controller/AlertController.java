package yte.intern.alertapplication.controller;

import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.service.AlertService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/alerts")
    public List<AlertDTO> getAlerts(@RequestParam(required = false) final String alertNameLike) {
        return alertService.getAlerts((alertNameLike == null) ? "" : alertNameLike);
    }

    @PostMapping("/alerts")
    public void createAlert(@RequestBody AlertDTO alertDTO) {
        alertService.createAlert(alertDTO);
    }

    @DeleteMapping("/alert/{alertId}")
    public void deleteAlertById(@PathVariable Long alertId) {
        alertService.deleteAlert(alertId);
    }

    @GetMapping("/alert/{alertId}")
    public AlertDTO getAlertById(@PathVariable Long alertId) {
        return alertService.getAlertById(alertId);
    }

    @GetMapping("/results/{alertId}")
    public List<ResultDTO> getResultsById(@PathVariable Long alertId, @RequestParam(required = false) Long sinceMinutes) {
        if (sinceMinutes != null) {
            return alertService.getResultsByIdSinceMinutes(alertId, sinceMinutes);
        } else {
            return alertService.getResultsById(alertId);
        }
    }
}
