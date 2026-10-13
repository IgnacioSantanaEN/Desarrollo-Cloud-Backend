package api.Azure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

@Configuration 
public class RabbitMQConfig {
    
    @Bean 
    public Queue myQueue() {
        return new Queue("Hello", false);
    }

    @Bean
    public DirectExchange directexchange() {
        return new DirectExchange("DirectExchange");
    }
    
    @Bean
    public Queue allLogsQueue() {
        return new Queue("AllLogs", false);
    }

    @Bean
    public Queue errorOnlyQueue() {
        return new Queue("ErrorLogs", false);
    }

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
}
