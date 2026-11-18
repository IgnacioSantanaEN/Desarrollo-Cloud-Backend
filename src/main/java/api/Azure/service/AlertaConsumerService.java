package api.Azure.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import api.Azure.model.AlertaFraude;

@Service
public class AlertaConsumerService {

    private final RabbitTemplate rabbitTemplate;

    public AlertaConsumerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @KafkaListener(topics = "banco-alertas", groupId = "grupo-alertas-fraude")
    public void consumirAlerta(AlertaFraude alerta) {
        try {
            if (alerta == null || alerta.getUsuario() == null) {
                System.err.println("⚠️ [CONSUMIDOR KAFKA] Alerta recibida con payload incompleto.");
                return;
            }

            System.out.println("🚨 [CONSUMIDOR @KafkaListener] Alerta procesada -> Usuario: "
                    + alerta.getUsuario() + " | Monto: " + alerta.getMonto()
                    + " | Motivo: " + alerta.getMotivo()
                    + " | Severidad: " + alerta.getSeveridad());
            
            String mensajeLog = String.format(
                    "[FRAUDE KAFKA] Usuario: %s | Monto: %.2f | Motivo: %s",
                    alerta.getUsuario(), alerta.getMonto(), alerta.getMotivo()
            );
            rabbitTemplate.convertAndSend("DirectExchange", "ERROR", mensajeLog);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando alerta de fraude o reenviando a RabbitMQ:" + e.getMessage());
        }
    }
}
