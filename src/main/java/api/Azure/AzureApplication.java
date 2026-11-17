package api.Azure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class AzureApplication {

	public static void main(String[] args) {
		SpringApplication.run(AzureApplication.class, args);
	}

}
