package api.Azure.controller;

import api.Azure.dto.CreateQueueRequest;
import api.Azure.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class RabbitAdminController {

    private final RabbitAdminService adminService;

    public RabbitAdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<String> createQueue(@Valid @RequestBody CreateQueueRequest request) {
        adminService.createQueue(request.getQueueName());

        if (request.getExchangeName() != null && request.getRoutingKey() != null) {
            adminService.createBinding(
                request.getQueueName(), 
                request.getExchangeName(), 
                request.getRoutingKey()
            );
        }

        return ResponseEntity.ok("Cola '" + request.getQueueName() + "' creada exitosamente.");
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<String> deleteQueue(@PathVariable String name) {
        boolean deleted = adminService.deleteQueue(name);
        if (deleted) {
            return ResponseEntity.ok("Cola '" + name + "' eliminada exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar la cola o no existía.");
        }
    }

    // En RabbitAdminController.java agrega:
    @PostMapping("/exchanges/{name}")
    public ResponseEntity<String> createExchange(@PathVariable String name) {
        adminService.createExchange(name);
        return ResponseEntity.ok("Exchange '" + name + "' creado exitosamente.");
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<String> deleteExchange(@PathVariable String name) {
        boolean deleted = adminService.deleteExchange(name);
        return deleted
                ? ResponseEntity.ok("Exchange '" + name + "' eliminado exitosamente.")
                : ResponseEntity.badRequest().body("No se pudo eliminar el exchange.");
    }

    @PostMapping("/bindings")
    public ResponseEntity<String> createBinding(@RequestBody CreateQueueRequest request) {
        if (request.getQueueName() == null || request.getExchangeName() == null || request.getRoutingKey() == null) {
            return ResponseEntity.badRequest().body("queueName, exchangeName y routingKey son obligatorios.");
        }
        adminService.createBinding(request.getQueueName(), request.getExchangeName(), request.getRoutingKey());
        return ResponseEntity.ok("Binding creado entre '" + request.getExchangeName() + "' -> '" + request.getQueueName() + "'.");
    }
}