package com.tienda.ms_auditoria.service;

import com.tienda.ms_auditoria.model.EventoAuditoria;
import com.tienda.ms_auditoria.repository.EventoAuditoriaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditoriaServiceTest {

    @Test
    void registraEvento() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.save(any(EventoAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoAuditoria e = new AuditoriaService(repo).registrar("orden.creada", "{\"ordenId\":1}");

        assertEquals("orden.creada", e.getRoutingKey());
        verify(repo).save(any(EventoAuditoria.class));
    }

    @Test
    void resumenAgrupaPorEvento() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.contarPorEvento()).thenReturn(List.<Object[]>of(new Object[] {"orden.creada", 3L}));

        Map<String, Long> resumen = new AuditoriaService(repo).resumen();

        assertEquals(3L, resumen.get("orden.creada"));
    }
}
