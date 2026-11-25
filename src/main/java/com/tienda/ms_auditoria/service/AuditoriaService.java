package com.tienda.ms_auditoria.service;

import com.tienda.ms_auditoria.model.EventoAuditoria;
import com.tienda.ms_auditoria.repository.EventoAuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Logica de negocio de auditoria (no conoce Kafka: recibe clave, payload y origen ya extraidos). */
@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final EventoAuditoriaRepository repo;

    public AuditoriaService(EventoAuditoriaRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public EventoAuditoria registrar(String routingKey, String payload) {
        return registrar(routingKey, payload, null, null);
    }

    /** Registra el evento una sola vez: si el eventoId ya existe (reentrega de Kafka) no lo duplica. */
    @Transactional
    public EventoAuditoria registrar(String clave, String payload, String eventoId, String origen) {
        if (eventoId != null && repo.existsByEventoId(eventoId)) {
            log.info("[AUDITORIA] {} ({}) ya registrado: reentrega ignorada", clave, eventoId);
            return null;
        }
        EventoAuditoria evento = repo.save(new EventoAuditoria(clave, payload, Instant.now(), eventoId, origen));
        log.info("[AUDITORIA] {} registrado desde {}", clave, origen);
        return evento;
    }

    @Transactional(readOnly = true)
    public List<EventoAuditoria> ultimos() {
        return repo.findTop100ByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> resumen() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Object[] fila : repo.contarPorEvento()) {
            resultado.put((String) fila[0], (Long) fila[1]);
        }
        return resultado;
    }
}
