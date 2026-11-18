package api.Azure.controller;

import java.util.*;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.*;
import org.springframework.http.ResponseEntity;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka/admin")
public class KafkaAdminController {
    
    private final KafkaAdmin kafkaAdmin;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaAdminController(KafkaAdmin kafkaAdmin, KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @GetMapping("/topics")
    public ResponseEntity<?> listarTopicos() {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Set<String> nombres = admin.listTopics().names().get();
            Map<String, TopicDescription> descripciones = admin.describeTopics(nombres).allTopicNames().get();

            List<Map<String, Object>> resultado = new ArrayList<>();
            for (TopicDescription desc : descripciones.values()) {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("nombre", desc.name());
                info.put("particiones", desc.partitions().size());
                info.put("interno", desc.isInternal());
                resultado.add(info);
            }
            return ResponseEntity.ok(resultado);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().body("Error listando tópicos: " + e.getMessage());
        }
    }

    @PostMapping("/topics")
    public ResponseEntity<?> crearTopico(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        int particiones = body.get("particiones") != null ? ((Number) body.get("particiones")).intValue() : 3;
        short replicas = body.get("replicas") != null ? ((Number) body.get("replicas")).shortValue() : 1;
        String retencionMs = body.get("retentionMs") != null ? String.valueOf(body.get("retentionMs")) : null;

        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del tópico es obligatorio");
        }

        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            NewTopic nuevoTopico = new NewTopic(nombre, particiones, replicas);
            if (retencionMs != null && !retencionMs.isBlank()) {
                nuevoTopico.configs(Map.of("retention.ms", retencionMs));
            }
            admin.createTopics(Collections.singletonList(nuevoTopico)).all().get();
            return ResponseEntity.ok(Map.of("mensaje", "Tópico creado correctamente", "topic", nombre));
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.badRequest().body("No se pudo crear el tópico: " + e.getMessage());
        }
    }

    @PutMapping("/topics/{nombre}")
    public ResponseEntity<?> modificarTopico(@PathVariable String nombre, @RequestBody Map<String, Object> body) {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            if (body.get("particiones") != null) {
                int nuevasParticiones = ((Number) body.get("particiones")).intValue();
                admin.createPartitions(Map.of(nombre, NewPartitions.increaseTo(nuevasParticiones))).all().get();
            }
            if (body.get("retentionMs") != null) {
                String retencion = String.valueOf(body.get("retentionMs"));
                ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, nombre);
                AlterConfigOp op = new AlterConfigOp(
                        new ConfigEntry("retention.ms", retencion),
                        AlterConfigOp.OpType.SET
                );
                admin.incrementalAlterConfigs(Map.of(resource, Collections.singletonList(op))).all().get();
            }
            return ResponseEntity.ok(Map.of("mensaje", "Tópico actualizado correctamente", "topic", nombre));
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.badRequest().body("Error modificando tópico: " + e.getMessage());
        }
    }

    @DeleteMapping("/topics/{nombre}")
    public ResponseEntity<?> eliminarTopico(@PathVariable String nombre) {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            admin.deleteTopics(Collections.singletonList(nombre)).all().get();
            return ResponseEntity.ok(Map.of("mensaje", "Tópico eliminado", "topic", nombre));
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.badRequest().body("Error eliminando tópico: " + e.getMessage());
        }
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<?> listarConsumerGroups() {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Collection<GroupListing> grupos = admin
                    .listGroups(ListGroupsOptions.forConsumerGroups())
                    .all()
                    .get();

            List<Map<String, Object>> resultado = new ArrayList<>();
            for (GroupListing g : grupos) {
                resultado.add(Map.of(
                        "groupId", g.groupId(),
                        "estado", g.groupState().map(Enum::name).orElse("UNKNOWN")
                ));
            }
            return ResponseEntity.ok(resultado);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().body("Error listando consumer groups: " + e.getMessage());
        }
    }

    @PostMapping("/publicar")
    public ResponseEntity<?> publicarEnTopicoDinamico(@RequestBody Map<String, Object> body) {
        String topico = (String) body.get("topico");
        String key = body.get("key") != null ? String.valueOf(body.get("key")) : "default-key";
        Object mensaje = body.get("mensaje");

        if (topico == null || topico.isBlank() || mensaje == null || String.valueOf(mensaje).isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El tópico y el mensaje son obligatorios"));
        }

        kafkaTemplate.send(topico.trim(), key, mensaje);
        return ResponseEntity.accepted().body(Map.of(
                "mensaje", "Evento publicado en el tópico '" + topico.trim() + "'",
                "topic", topico.trim(),
                "key", key
        ));
    }
}
