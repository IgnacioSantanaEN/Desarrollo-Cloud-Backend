package api.Azure.service;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
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
        if(message.contains ("ERROR") || message.contains("corrupto")) {
            System.out.println("[FALLO SIMULADO] El mensaje está corrupto. redirigiendo a la DLQ... ");
            throw new AmqpRejectAndDontRequeueException("Error intencional: Mensaje rechazado y enviado a DeadLetterQueue");
        } 

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("[ALERTA CRITICA] Error detectado: " + message);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    // @RabbitListener(queues = "deadLetterQueue")
    public void receiveDeadLetter(String message) {
        System.out.println("[DEAD LETTER CAPTURADO] Mensaje no procesado: " + message);
    }
}
