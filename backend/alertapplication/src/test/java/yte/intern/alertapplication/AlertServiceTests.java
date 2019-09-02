package yte.intern.alertapplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.entity.Result;
import yte.intern.alertapplication.repository.AlertRepository;
import yte.intern.alertapplication.service.AlertService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AlertService.class)
public class AlertServiceTests {

    @TestConfiguration
    static class AlertServiceTestConfiguration {
    }

    private final LocalDateTime LOCAL_DATETIME = LocalDateTime.of(2019, 1, 1, 0, 0, 0);

    @MockBean
    private Clock clock;

    private Clock fixedClock;

    @MockBean
    private AlertRepository alertRepository;

    @Autowired
    @InjectMocks
    private AlertService alertService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fixedClock = Clock.fixed(LOCAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        Alert alert = new Alert("Test Alert 1", "http://test.com", "GET", 10L, LOCAL_DATETIME.plusSeconds(10L));
        alert.setId(1L);

        Result result = new Result(alert, LOCAL_DATETIME, 100L, 200);
        HashSet<Result> results = new HashSet<>();
        results.add(result);
        alert.setResults(results);

        ArrayList<Alert> allAlerts = new ArrayList<>();
        allAlerts.add(alert);

        when(alertRepository.save(notNull())).then(AdditionalAnswers.returnsFirstArg());

        when(alertRepository.findById(any())).thenReturn(Optional.empty());
        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));

        when(alertRepository.findByName(any())).thenReturn(Optional.empty());
        when(alertRepository.findByName(alert.getName())).thenReturn(Optional.of(alert));

        when(alertRepository.findByNameIgnoreCaseContaining(any())).thenReturn(new ArrayList<>());
        when(alertRepository.findByNameIgnoreCaseContaining("")).thenReturn(allAlerts);
        when(alertRepository.findByNameIgnoreCaseContaining("Test")).thenReturn(allAlerts);

        when(alertRepository.findAll()).thenReturn(allAlerts);
    }

    @Test
    public void whenCreateAlert_thenAlertCreated() {
        // given
        AlertDTO alertDTO = new AlertDTO(null, "Test Alert 1", "http://test.com", "GET", 10L);

        // when
        Alert alert = alertService.createAlert(alertDTO);

        // then
        Assert.assertEquals(alertDTO.getAlertId(), alert.getId());
        Assert.assertEquals(alertDTO.getAlertName(), alert.getName());
        Assert.assertEquals(LOCAL_DATETIME.plusSeconds(10L), alert.getNextDeadline());
    }

    @Test
    public void whenGetAlertByIdNotFound_thenReturnNull() {
        // when
        AlertDTO notFoundAlertDTO = alertService.getAlertById(0L);

        // then
        Assert.assertNull(notFoundAlertDTO);
    }

    @Test
    public void whenGetAlertById_thenReturnAlert() {
        // when
        AlertDTO foundAlertDTO = alertService.getAlertById(1L);

        // then
        Assert.assertEquals(1L, foundAlertDTO.getAlertId().longValue());
        Assert.assertEquals("Test Alert 1", foundAlertDTO.getAlertName());
    }

    @Test
    public void whenGetAlertByNameNotFound_thenReturnNull() {
        // when
        AlertDTO notFoundAlertDTO = alertService.getAlertByName("Not existing name");

        // then
        Assert.assertNull(notFoundAlertDTO);
    }

    @Test
    public void whenGetAlertByName_thenReturnAlert() {
        // when
        AlertDTO foundAlertDTO = alertService.getAlertByName("Test Alert 1");

        // then
        Assert.assertEquals(1L, foundAlertDTO.getAlertId().longValue());
        Assert.assertEquals("Test Alert 1", foundAlertDTO.getAlertName());
    }

    @Test
    public void whenGetAlerts_thenReturnAlerts() {
        // when
        ArrayList<AlertDTO> alertDTOS = new ArrayList<>(alertService.getAlerts(""));

        // then
        Assert.assertEquals(1, alertDTOS.size());

        AlertDTO alertDTO = alertDTOS.get(0);
        Assert.assertEquals(1L, alertDTO.getAlertId().longValue());
        Assert.assertEquals("Test Alert 1", alertDTO.getAlertName());
    }

    @Test
    public void whenGetAlertsByNameLikeNotFound_thenReturnEmptyList() {
        // when
        ArrayList<AlertDTO> notFoundAlertDTOS = new ArrayList<>(
                alertService.getAlerts("No similar names")
        );

        // then
        Assert.assertEquals(0, notFoundAlertDTOS.size());
    }

    @Test
    public void whenGetAlertsByNameLike_thenReturnAlerts() {
        // when
        ArrayList<AlertDTO> alertDTOS = new ArrayList<>(
                alertService.getAlerts("Test")
        );

        // then
        Assert.assertEquals(1, alertDTOS.size());

        AlertDTO alertDTO = alertDTOS.get(0);
        Assert.assertEquals(1L, alertDTO.getAlertId().longValue());
        Assert.assertEquals("Test Alert 1", alertDTO.getAlertName());
    }

    @Test
    public void whenGetResultsByIdNotFound_thenReturnEmptyList() {
        // when
        ArrayList<ResultDTO> notFoundResultDTOS = new ArrayList<>(
                alertService.getResultsById(0L)
        );

        // then
        Assert.assertEquals(0, notFoundResultDTOS.size());
    }

    @Test
    public void whenGetResultsById_thenReturnResults() {
        // when
        ArrayList<ResultDTO> resultDTOS = new ArrayList<>(
                alertService.getResultsById(1L)
        );

        // then
        Assert.assertEquals(1, resultDTOS.size());
        ResultDTO result = resultDTOS.get(0);
        Assert.assertEquals(LOCAL_DATETIME, LocalDateTime.parse(result.getTimestamp()));
        Assert.assertEquals(true, result.getSuccess());
        Assert.assertEquals(100L, result.getElapsed().longValue());
    }

    @Test
    public void whenDeleteAlertById_thenDeleteCalled() {
        // given
        doNothing().when(alertRepository).deleteById(notNull());

        // when
        alertService.deleteAlert(1L);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(alertRepository, times(1)).deleteById(captor.capture());
        Assert.assertEquals(1L, captor.getValue().longValue());
    }

}
