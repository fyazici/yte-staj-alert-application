package yte.intern.alertapplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.entity.Result;
import yte.intern.alertapplication.repository.AlertRepository;
import yte.intern.alertapplication.service.AlertRequestSchedulerService;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        AlertRequestSchedulerService.class,
        AlertRequestSchedulerServiceTestConfiguration.class
})
@RestClientTest
public class AlertRequestSchedulerServiceTests {

    private final LocalDateTime LOCAL_DATETIME = LocalDateTime.of(2019, 1, 1, 0, 0, 0);

    @MockBean
    private Clock clock;

    private Clock fixedClock;

    @Autowired
    @InjectMocks
    private AlertRequestSchedulerService alertRequestSchedulerService;

    @MockBean
    private AlertRepository alertRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fixedClock = Clock.fixed(LOCAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        Alert alert = new Alert("Test Alert 1", "http://test.com", "GET", 10L, LOCAL_DATETIME.minusSeconds(1));
        alert.setId(1L);

        ArrayList<Alert> alerts = new ArrayList<>();
        alerts.add(alert);

        when(alertRepository.findByNextDeadlineLessThan(LOCAL_DATETIME)).thenReturn(alerts);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void whenScheduleRequests_thenRequestWorkerCalled() {
        // given
        AlertRequestSchedulerService spyAlertRequestSchedulerService = spy(alertRequestSchedulerService);
        doNothing().when(spyAlertRequestSchedulerService).requestWorker(notNull());

        // when
        spyAlertRequestSchedulerService.scheduleRequests();

        // then
        verify(spyAlertRequestSchedulerService).requestWorker(argThat(alert -> alert.getId().equals(1L)));
    }

    @Test
    public void whenRequestWorkerGracefullyCallsGET_thenAddResultAndUpdateAlert() {
        // given
        Alert alert = new Alert("Gracefully Executing Alert", "http://test.com", "GET", 10L, LOCAL_DATETIME);
        alert.setId(1L);
        alert.setResults(new HashSet<>());
        Alert spyAlert = spy(alert);

        mockServer.expect(
                ExpectedCount.once(),
                requestTo("http://test.com"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        // when
        alertRequestSchedulerService.requestWorker(spyAlert);

        // then
        verify(spyAlert).setNextDeadline(LOCAL_DATETIME.plusSeconds(alert.getPeriod()));
        mockServer.verify();
        Assert.assertEquals(1, alert.getResults().size());
        Result result = alert.getResults().iterator().next();
        Assert.assertNull(result.getId());
        Assert.assertSame(spyAlert, result.getAlert());
        Assert.assertEquals(LOCAL_DATETIME, result.getRequestedAt());
        // TODO: test elapsed time
        Assert.assertEquals(200, result.getStatusCode().intValue());
        verify(alertRepository, times(1)).save(argThat(alert1 -> alert1.getResults().size() == 1));

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResultDTO> payloadCaptor = ArgumentCaptor.forClass(ResultDTO.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        Assert.assertEquals("/topic/1", destinationCaptor.getValue());
        Assert.assertTrue(payloadCaptor.getValue().getSuccess());
    }

    @Test
    public void whenRequestWorkerGracefullyCallsPOST_thenAddResultAndUpdateAlert() {
        // given
        Alert alert = new Alert("Gracefully Executing Alert", "http://test.com", "POST", 10L, LOCAL_DATETIME);
        alert.setId(1L);
        alert.setResults(new HashSet<>());
        Alert spyAlert = spy(alert);

        mockServer.expect(
                ExpectedCount.once(),
                requestTo("http://test.com"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));

        // when
        alertRequestSchedulerService.requestWorker(spyAlert);

        // then
        verify(spyAlert).setNextDeadline(LOCAL_DATETIME.plusSeconds(alert.getPeriod()));
        mockServer.verify();
        Assert.assertEquals(1, alert.getResults().size());
        Result result = alert.getResults().iterator().next();
        Assert.assertNull(result.getId());
        Assert.assertSame(spyAlert, result.getAlert());
        Assert.assertEquals(LOCAL_DATETIME, result.getRequestedAt());
        // TODO: test elapsed time
        Assert.assertEquals(200, result.getStatusCode().intValue());
        verify(alertRepository).save(argThat(alert1 -> alert1.getResults().size() == 1));

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResultDTO> payloadCaptor = ArgumentCaptor.forClass(ResultDTO.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        Assert.assertEquals("/topic/1", destinationCaptor.getValue());
        Assert.assertTrue(payloadCaptor.getValue().getSuccess());
    }

    @Test
    public void whenRequestWorkerFailsWith404_thenAddResultAndUpdateAlert() {
        // given
        Alert alert = new Alert("404 Alert", "http://test.com", "GET", 10L, LOCAL_DATETIME);
        alert.setId(1L);
        alert.setResults(new HashSet<>());
        Alert spyAlert = spy(alert);

        mockServer.expect(
                ExpectedCount.once(),
                requestTo("http://test.com"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // when
        alertRequestSchedulerService.requestWorker(spyAlert);

        // then
        verify(spyAlert).setNextDeadline(LOCAL_DATETIME.plusSeconds(alert.getPeriod()));
        mockServer.verify();
        Assert.assertEquals(1, alert.getResults().size());
        Result result = alert.getResults().iterator().next();
        Assert.assertNull(result.getId());
        Assert.assertSame(spyAlert, result.getAlert());
        Assert.assertEquals(LOCAL_DATETIME, result.getRequestedAt());
        // TODO: test elapsed time
        Assert.assertEquals(404, result.getStatusCode().intValue());
        verify(alertRepository).save(argThat(alert1 -> alert1.getResults().size() == 1));

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResultDTO> payloadCaptor = ArgumentCaptor.forClass(ResultDTO.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        Assert.assertEquals("/topic/1", destinationCaptor.getValue());
        Assert.assertFalse(payloadCaptor.getValue().getSuccess());
    }

    @Test
    public void whenRequestWorkerFailsWithTimeout_thenAddResultAndUpdateAlert() {
        // given
        Alert alert = new Alert("Timeout Alert", "http://test.com", "GET", 10L, LOCAL_DATETIME);
        alert.setId(1L);
        alert.setResults(new HashSet<>());
        Alert spyAlert = spy(alert);

        mockServer.expect(
                ExpectedCount.once(),
                requestTo("http://test.com"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // when
        alertRequestSchedulerService.requestWorker(spyAlert);

        // then
        verify(spyAlert).setNextDeadline(LOCAL_DATETIME.plusSeconds(alert.getPeriod()));
        mockServer.verify();
        Assert.assertEquals(1, alert.getResults().size());
        Result result = alert.getResults().iterator().next();
        Assert.assertNull(result.getId());
        Assert.assertSame(spyAlert, result.getAlert());
        Assert.assertEquals(LOCAL_DATETIME, result.getRequestedAt());
        // TODO: test elapsed time
        Assert.assertEquals(-1, result.getStatusCode().intValue());
        verify(alertRepository).save(argThat(alert1 -> alert1.getResults().size() == 1));

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ResultDTO> payloadCaptor = ArgumentCaptor.forClass(ResultDTO.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        Assert.assertEquals("/topic/1", destinationCaptor.getValue());
        Assert.assertFalse(payloadCaptor.getValue().getSuccess());
    }
}
