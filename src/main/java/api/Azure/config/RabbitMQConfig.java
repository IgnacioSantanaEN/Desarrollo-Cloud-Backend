package api.Azure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

@Configuration 
public class RabbitMQConfig {
    
    // Queues =====================================================================
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("deadLetterQueue", true);
    }
    
    @Bean
    public Queue allLogsQueue() {
        return QueueBuilder.durable("AllLogs")
        .withArgument("x-dead-letter-exchange", "DeadLetterExchange")
        .withArgument("x-dead-letter-routing-key", "deadLetter")
        .build();
    }

    @Bean
    public Queue errorOnlyQueue() {
        return QueueBuilder.durable("ErrorLogs")
        .withArgument("x-dead-letter-exchange", "DeadLetterExchange")
        .withArgument("x-dead-letter-routing-key", "deadLetter")
        .build();
    }

    // Exchanges =====================================================================
    @Bean
    public DirectExchange directexchange() {
        return new DirectExchange("DirectExchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("DeadLetterExchange");
    }

    // Bindings =====================================================================
    @Bean
    public Binding bindAllInfo(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("INFO");
    }

    @Bean 
    public Binding bindAllWarnings(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("WARNING");
    }

    @Bean
    public Binding bindAllErrors(DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("ERROR");
    }

    @Bean
    public Binding onlyErrors(DirectExchange directExchange, Queue errorOnlyQueue) {
        return BindingBuilder.bind(errorOnlyQueue).to(directExchange).with("ERROR");
    }

    @Bean
    public Binding deadLetterBinding(DirectExchange deadLetterExchange, Queue deadLetterQueue) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("deadLetter");
    }
}
