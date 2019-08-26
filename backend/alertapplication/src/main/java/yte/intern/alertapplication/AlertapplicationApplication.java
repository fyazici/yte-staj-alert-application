package yte.intern.alertapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Duration;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AlertapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertapplicationApplication.class, args);
	}

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
				.setConnectTimeout(Duration.ofSeconds(1))
				.setReadTimeout(Duration.ofSeconds(1))
				.build();
	}

}
