package api.Azure.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component 
public class Receiver {
    
    @RabbitListener(queues = "Hello")
    public void receiveMessage(String message) {
        try {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));


            System.out.println("[" + timestamp + "] [✓] Mensaje recibido: '" + message + "'");
        } catch (Exception e) {
            System.err.println("[x] Error recibiendo mensaje: "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
