package com.tienda.ms_auditoria.messaging;

import com.tienda.ms_auditoria.kafka.EventoInvalidoException;
import com.tienda.ms_auditoria.service.AuditoriaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/**
 * Dominio AUDITORIA - consumidor Kafka de los 4 topicos de negocio (consumer group "ms-auditoria").
 * MIGRADO desde RabbitMQ (cola auditoria.todos con binding "#"): la auditoria es un registro de HECHOS,
 * Kafka los conserva (retencion) y permite volver a leerlos con otro consumer group sin afectar a nadie.
 *
 * El payload se lee como Map generico (auditoria no interpreta el contenido). Esta clase solo valida el payload y extrae datos del mensaje; la persistencia esta en AuditoriaService.
 * Payload invalido -> EventoInvalidoException -> sin reintentos -> topico "<topico>.dlt".
 */
@Component
public class AuditoriaConsumer {

    private final AuditoriaService auditoriaService;
    private final JsonMapper json = JsonMapper.builder().build();

    public AuditoriaConsumer(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @KafkaListener(id = "auditoria-eventos",
            topics = {"${app.kafka.topicos.navegacion.nombre}", "${app.kafka.topicos.pagos.nombre}",
                      "${app.kafka.topicos.fraude.nombre}", "${app.kafka.topicos.transacciones.nombre}"},
            groupId = "${app.kafka.group-id}", containerFactory = "auditoriaFactory")
    public void onEvento(ConsumerRecord<String, Map<String, Object>> registro) {
        Map<String, Object> evento = registro.value();
        if (evento == null || evento.isEmpty()) {
            throw new EventoInvalidoException("Evento vacio o no es un objeto JSON");
        }
        Object tipo = evento.get("tipo");
        if (!(tipo instanceof String t) || t.isBlank()) {
            throw new EventoInvalidoException("Evento sin campo 'tipo'");
        }
        Object id = evento.get("eventoId");
        String origen = registro.topic() + "-" + registro.partition() + "@" + registro.offset();
        auditoriaService.registrar(registro.topic() + "/" + t, json.writeValueAsString(evento),
                id == null ? null : id.toString(), origen);
    }
}
