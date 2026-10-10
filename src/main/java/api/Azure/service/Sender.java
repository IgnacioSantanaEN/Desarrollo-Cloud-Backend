package api.Azure.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component 
public class Sender{
    
    @Autowired 
    private RabbitTemplate rabbitTemplate;

    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        try{
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));

            String fullMessage = String.format("[%s] %s", timestamp, message);

            rabbitTemplate.convertAndSend("Hello", fullMessage);
            System.out.println("[x] Mensaje enviado: " + fullMessage);

            
        } catch (Exception e) {
            System.err.println("[x] Error enviando mensaje: "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
