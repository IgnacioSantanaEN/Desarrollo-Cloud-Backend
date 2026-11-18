package api.Azure.service;

import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public void createQueue(String queueName) {
        Queue queue = new Queue(queueName, true);
        amqpAdmin.declareQueue(queue);
    }
    
    public void createExchange(String exchangeName) {
        DirectExchange exchange = new DirectExchange(exchangeName);
        amqpAdmin.declareExchange(exchange);
    }

    public void createBinding(String queueName, String exchangeName, String routingKey) {
        Binding binding = new Binding(
            queueName,
            Binding.DestinationType.QUEUE,
            exchangeName,
            routingKey,
            null
        );
        amqpAdmin.declareBinding(binding);
    }

    public boolean deleteQueue(String queueName) {
        return amqpAdmin.deleteQueue(queueName);
    }

    // En RabbitAdminService.java agrega:
    public boolean deleteExchange(String exchangeName) {
        return amqpAdmin.deleteExchange(exchangeName);
    }

    public void removeBinding(String queueName, String exchangeName, String routingKey) {
        Binding binding = new Binding(
        queueName,
        Binding.DestinationType.QUEUE,
        exchangeName,
        routingKey,
        null
    );
        amqpAdmin.removeBinding(binding);
    }
}