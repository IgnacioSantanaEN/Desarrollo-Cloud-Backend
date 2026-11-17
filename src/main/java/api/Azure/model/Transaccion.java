package api.Azure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    private String usuario;
    private Double monto;
    private String origen;
    private Long timestamp;
}
