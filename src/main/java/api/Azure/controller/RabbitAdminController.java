package api.Azure.controller;

import api.Azure.dto.CreateQueueRequest;
import api.Azure.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
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
}