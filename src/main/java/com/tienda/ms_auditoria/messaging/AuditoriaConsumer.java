package com.tienda.ms_auditoria.messaging;

import com.tienda.ms_auditoria.config.RabbitConfig;
import com.tienda.ms_auditoria.model.EventoAuditoria;
import com.tienda.ms_auditoria.repository.EventoAuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/** Guarda cada evento que circula por RabbitMQ (sin interpretarlo): trazabilidad de la tienda. */
@Component
public class AuditoriaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaConsumer.class);

    private final EventoAuditoriaRepository repo;

    public AuditoriaConsumer(EventoAuditoriaRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_AUDITORIA)
    public void onEvento(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        repo.save(new EventoAuditoria(routingKey, payload, Instant.now()));
        log.info("[AUDITORIA] {} registrado", routingKey);
    }
}
