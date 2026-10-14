package com.tienda.ms_auditoria.messaging;

import com.rabbitmq.client.Channel;
import com.tienda.ms_auditoria.service.AuditoriaService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Dominio AUDITORIA - consume TODOS los eventos de "pedidos360.events" (sin interpretarlos).
 * Solo extrae routing key y cuerpo y delega en AuditoriaService; el ACK lo maneja ConfirmacionMensajes.
 */
@Component
public class AuditoriaConsumer {

    private final AuditoriaService auditoriaService;
    private final ConfirmacionMensajes confirmacion;

    public AuditoriaConsumer(AuditoriaService auditoriaService, ConfirmacionMensajes confirmacion) {
        this.auditoriaService = auditoriaService;
        this.confirmacion = confirmacion;
    }

    @RabbitListener(queues = "${app.rabbit.queues.auditoria}", ackMode = "MANUAL")
    public void onEvento(Message mensaje, Channel canal) throws IOException {
        confirmacion.procesar(mensaje, canal, () -> {
            MessageProperties props = mensaje.getMessageProperties();
            Object original = props.getHeader(ConfirmacionMensajes.HEADER_RK_ORIGINAL);
            String routingKey = original != null ? original.toString() : props.getReceivedRoutingKey();
            if (mensaje.getBody() == null || mensaje.getBody().length == 0) {
                throw new MensajeInvalidoException("Evento vacio (routingKey=" + routingKey + ")");
            }
            auditoriaService.registrar(routingKey, new String(mensaje.getBody(), StandardCharsets.UTF_8));
        });
    }
}
