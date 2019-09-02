package yte.intern.alertapplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import yte.intern.alertapplication.controller.AlertController;
import yte.intern.alertapplication.dto.AlertDTO;
import yte.intern.alertapplication.dto.ResultDTO;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.entity.Result;
import yte.intern.alertapplication.service.AlertService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AlertController.class)
@ContextConfiguration(classes = {
        AlertController.class,
        AlertControllerTestConfiguration.class
})
@AutoConfigureRestDocs
public class AlertControllerTests {

    private final LocalDateTime LOCAL_DATETIME = LocalDateTime.of(2019, 1, 1, 0, 0, 0);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AlertService alertService;

    @Before
    public void setup() {
        AlertDTO alertDTO1 = new AlertDTO(1L, "Test Alert 1", "http://test.com", "GET", 10L);
        AlertDTO alertDTO2 = new AlertDTO(2L, "Test Alert 2", "http://test.com", "GET", 10L);

        ResultDTO resultDTO = new ResultDTO(LOCAL_DATETIME.toString(), true, 100L);

        ArrayList<AlertDTO> alertDTOS = new ArrayList<>();
        alertDTOS.add(alertDTO1);
        alertDTOS.add(alertDTO2);

        ArrayList<AlertDTO> alertDTOS1 = new ArrayList<>();
        alertDTOS1.add(alertDTO1);

        ArrayList<ResultDTO> resultDTOS = new ArrayList<>();
        resultDTOS.add(resultDTO);

        when(alertService.getAlerts("")).thenReturn(alertDTOS);
        when(alertService.getAlerts("1")).thenReturn(alertDTOS1);
        when(alertService.getAlertById(2L)).thenReturn(alertDTO2);
        when(alertService.getResultsById(1L)).thenReturn(resultDTOS);
    }

    @Test
    public void whenCreateAlert_thenServiceCreateAlertCalled() throws Exception {
        AlertDTO alertDTO = new AlertDTO(null, "Test Alert", "http://test.com", "GET", 10L);

        mvc.perform(post("/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(alertDTO)))
                .andExpect(status().isOk());

        ArgumentCaptor<AlertDTO> captor = ArgumentCaptor.forClass(AlertDTO.class);
        verify(alertService, times(1)).createAlert(captor.capture());

        Assert.assertEquals(alertDTO, captor.getValue());
    }

    @Test
    public void givenAlerts_whenGetAllAlerts_thenReturnAllAlerts() throws Exception {
        MvcResult reqResult = mvc.perform(get("/alerts"))
                .andExpect(status().isOk())
                .andReturn();

        AlertDTO[] alertDTOS = new ObjectMapper().readValue(reqResult.getResponse().getContentAsString(), AlertDTO[].class);

        Assert.assertEquals(2, alertDTOS.length);
        Assert.assertTrue(Arrays.stream(alertDTOS).anyMatch(
                alertDTO -> alertDTO.getAlertId().equals(1L)));
        Assert.assertTrue(Arrays.stream(alertDTOS).anyMatch(
                alertDTO -> alertDTO.getAlertId().equals(2L)));

        verify(alertService, times(1)).getAlerts(argThat(s -> s.equals("")));
    }

    @Test
    public void givenAlerts_whenGetAlertNameLike_thenReturnAlert() throws Exception {
        MvcResult reqResult = mvc.perform(get("/alerts")
                .param("alertNameLike", "1"))
                .andExpect(status().isOk())
                .andReturn();

        AlertDTO[] alertDTOS = new ObjectMapper().readValue(reqResult.getResponse().getContentAsString(), AlertDTO[].class);

        Assert.assertEquals(1, alertDTOS.length);
        Assert.assertEquals(1L, alertDTOS[0].getAlertId().longValue());

        verify(alertService, times(1)).getAlerts(argThat(s -> s.equals("1")));
    }

    @Test
    public void givenAlerts_whenGetAlertById_thenReturnAlert() throws Exception {
        MvcResult reqResult = mvc.perform(get("/alert/{alertId}", "2"))
                .andExpect(status().isOk())
                .andReturn();

        AlertDTO alertDTO = new ObjectMapper().readValue(reqResult.getResponse().getContentAsString(), AlertDTO.class);

        Assert.assertEquals(2L, alertDTO.getAlertId().longValue());

        verify(alertService, times(1)).getAlertById(argThat(aLong -> aLong.equals(2L)));
    }

    @Test
    public void givenAlerts_whenGetResultsById_thenReturnResults() throws Exception {
        MvcResult reqResult = mvc.perform(get("/results/{alertId}", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ResultDTO[] resultDTOS = new ObjectMapper().readValue(reqResult.getResponse().getContentAsString(), ResultDTO[].class);

        Assert.assertEquals(1, resultDTOS.length);
        Assert.assertEquals(LOCAL_DATETIME.toString(), resultDTOS[0].getTimestamp());

        verify(alertService, times(1)).getResultsById(argThat(aLong -> aLong.equals(1L)));
    }

    @Test
    public void givenAlerts_whenDeleteAlertById_thenAlertServiceDeleteCalled() throws Exception {
        MvcResult reqResult = mvc.perform(delete("/alert/{alertId}", "1"))
                .andExpect(status().isOk())
                .andReturn();

        verify(alertService, times(1)).deleteAlert(argThat(aLong -> aLong.equals(1L)));
    }
}
