package api.Azure.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service 
public class LogConsumer {
    
    @RabbitListener(queues = "AllLogs")
    public void receiveAllLogs(String message) {
        System.out.println("[MONITOR GENERAL] Log recibido: " + message);
    }

    @RabbitListener(queues = "ErrorLogs")
    public void receiveErrorLogs(String message) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("[ALERTA CRITICA] Error detectado: " + message);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}
