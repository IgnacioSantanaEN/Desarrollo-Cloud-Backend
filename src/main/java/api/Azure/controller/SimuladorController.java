package api.Azure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.Azure.model.Transaccion;

@RestController
@RequestMapping("/api/banco")
public class SimuladorController {

    private final KafkaTemplate<String, Object> kafka;

    public SimuladorController(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    @PostMapping("/transaccion")
    public ResponseEntity<String> enviar(@RequestBody Transaccion tx) {
        if(tx==null || tx.getUsuario()==null || tx.getUsuario().isBlank()
                || tx.getMonto()==null || tx.getMonto()<0 || tx.getOrigen()==null)
            {
            return ResponseEntity.badRequest().body("Datos Invalidos");
        }if(tx.getTimestamp()==null) tx.setTimestamp(System.currentTimeMillis());
        kafka.send("banco-transacciones", tx.getUsuario(), tx);
        return ResponseEntity.accepted().body("Transacción recibida para el envio");
    }
}
