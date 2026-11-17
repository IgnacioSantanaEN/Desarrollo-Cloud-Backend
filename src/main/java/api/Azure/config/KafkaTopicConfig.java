package api.Azure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic transaccionesTopic() {
        return TopicBuilder.name("banco-transacciones")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertasTopic() {
        return TopicBuilder.name("banco-alertas")
                .partitions(3)
                .replicas(1)
                .build();
    }
}