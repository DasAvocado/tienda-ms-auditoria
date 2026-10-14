package com.tienda.ms_auditoria.controller;

import com.tienda.ms_auditoria.model.EventoAuditoria;
import com.tienda.ms_auditoria.security.Seguridad;
import com.tienda.ms_auditoria.service.AuditoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventoAuditoria> ultimos() {
        return service.ultimos();
    }

    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return service.resumen();
    }
}
