package api.Azure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;

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
    public DirectExchange directExchange() {
        return new DirectExchange("DirectExchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("DeadLetterExchange");
    }

    // Bindings =====================================================================
    @Bean
    public Binding bindAllInfo(@Qualifier("directExchange") DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("INFO");
    }

    @Bean 
    public Binding bindAllWarnings(@Qualifier("directExchange") DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("WARNING");
    }

    @Bean
    public Binding bindAllErrors(@Qualifier("directExchange") DirectExchange directExchange, Queue allLogsQueue) {
        return BindingBuilder.bind(allLogsQueue).to(directExchange).with("ERROR");
    }

    @Bean
    public Binding onlyErrors(@Qualifier("directExchange") DirectExchange directExchange, Queue errorOnlyQueue) {
        return BindingBuilder.bind(errorOnlyQueue).to(directExchange).with("ERROR");
    }

    @Bean
    public Binding deadLetterBinding(@Qualifier("deadLetterExchange") DirectExchange deadLetterExchange, Queue deadLetterQueue) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("deadLetter");
    }
}
