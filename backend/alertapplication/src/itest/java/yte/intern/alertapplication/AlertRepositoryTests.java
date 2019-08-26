package yte.intern.alertapplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import yte.intern.alertapplication.entity.Alert;
import yte.intern.alertapplication.repository.AlertRepository;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureWebClient
public class AlertRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepository alertRepository;

    private final LocalDateTime LOCAL_DATETIME = LocalDateTime.of(2019, 1, 1, 0, 0, 0);

    @Before
    public void setup() {
        Alert alertDue = new Alert(
                "Test Alert 1",
                "http://test.com",
                "GET",
                10L,
                LOCAL_DATETIME.minusSeconds(1)
        );
        entityManager.persist(alertDue);

        Alert alertNotDue1 = new Alert(
                "Test Alert 2",
                "http://test.com",
                "GET",
                10L,
                LOCAL_DATETIME
        );
        entityManager.persist(alertNotDue1);

        Alert alertNotDue2 = new Alert(
                "Alert 3",
                "http://test.com",
                "GET",
                10L,
                LOCAL_DATETIME.plusSeconds(1)
        );
        entityManager.persist(alertNotDue2);

        entityManager.flush();
    }

    @Test
    public void whenFindByNextDeadlineLessThan_thenReturnDueAlerts() {
        // when
        ArrayList<Alert> dueAlerts = new ArrayList<>(
                alertRepository.findByNextDeadlineLessThan(LOCAL_DATETIME)
        );

        // then
        Assert.assertEquals(1, dueAlerts.size());
        Assert.assertEquals("Test Alert 1", dueAlerts.get(0).getName());
    }

    @Test
    public void whenFindByName_thenReturnAlert() {
        // when
        Optional<Alert> maybeAlert = alertRepository.findByName("Test Alert 2");

        // then
        Assert.assertTrue(maybeAlert.isPresent());
        Alert alert = maybeAlert.get();
        Assert.assertEquals("Test Alert 2", alert.getName());
    }

    @Test
    public void whenFindByNameIgnoreCaseContaining_thenReturnAlert() {
        // when
        ArrayList<Alert> alerts = new ArrayList<>(
                alertRepository.findByNameIgnoreCaseContaining("Test")
        );

        // then
        Assert.assertEquals(2, alerts.size());
        Assert.assertTrue(alerts.stream().noneMatch(alert -> "Alert 3".equals(alert.getName())));
    }
}
