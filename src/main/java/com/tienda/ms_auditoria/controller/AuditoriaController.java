package com.tienda.ms_auditoria.controller;

import com.tienda.ms_auditoria.model.EventoAuditoria;
import com.tienda.ms_auditoria.repository.EventoAuditoriaRepository;
import com.tienda.ms_auditoria.security.Seguridad;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *   GET /api/auditoria          -> ultimos 100 eventos (solo ADMIN)
 *   GET /api/auditoria/resumen  -> cantidad de eventos por tipo (solo ADMIN)
 */
@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize(Seguridad.ADMIN)
public class AuditoriaController {

    private final EventoAuditoriaRepository repo;

    public AuditoriaController(EventoAuditoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<EventoAuditoria> ultimos() {
        return repo.findTop100ByOrderByFechaDesc();
    }

    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Object[] fila : repo.contarPorEvento()) {
            resultado.put((String) fila[0], (Long) fila[1]);
        }
        return resultado;
    }
}
