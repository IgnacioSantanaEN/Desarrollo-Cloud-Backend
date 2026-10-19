package api.Azure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQueueRequest {

    @NotBlank(message = "El nombre de la cola no puede estar vacío")
    private String queueName;

    private String exchangeName;
    private String routingKey;
}