package api.Azure.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@CrossOrigin(origins = "http://localhost:5173")
public class LogConsumerController {
    
    private final RabbitTemplate rabbitTemplate;

    public LogConsumerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping ("/log")
    public String sendLog(@RequestBody LogMessage LogMessage){
        rabbitTemplate.convertAndSend(
            "DirectExchange",
            LogMessage.getLevel(),
            LogMessage.getMessage()
        );
        return "Log enviado con éxito [" + LogMessage.getLevel() + "]: " + LogMessage.getMessage();
    }

    public static class LogMessage {
        private String level;
        private String message;

        public LogMessage() {}

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
