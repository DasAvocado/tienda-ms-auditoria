package com.tienda.ms_auditoria.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "eventos_auditoria")
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "routing_key")
    private String routingKey;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Instant fecha;

    /** Id del evento (lo genera el productor): evita registrar dos veces el mismo mensaje (Kafka entrega "al menos una vez"). */
    @Column(name = "evento_id", unique = true)
    private String eventoId;

    /** Ubicacion exacta del mensaje en Kafka: topico-particion@offset */
    private String origen;

    public EventoAuditoria() {}

    public EventoAuditoria(String routingKey, String payload, Instant fecha, String eventoId, String origen) {
        this(routingKey, payload, fecha);
        this.eventoId = eventoId;
        this.origen = origen;
    }

    public EventoAuditoria(String routingKey, String payload, Instant fecha) {
        this.routingKey = routingKey;
        this.payload = payload;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
    public Instant getFecha() { return fecha; }
    public String getEventoId() { return eventoId; }
    public String getOrigen() { return origen; }
}
