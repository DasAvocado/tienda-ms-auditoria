package com.tienda.ms_auditoria.service;

import com.tienda.ms_auditoria.model.EventoAuditoria;
import com.tienda.ms_auditoria.repository.EventoAuditoriaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditoriaServiceTest {

    @Test
    void registraEvento() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.save(any(EventoAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoAuditoria e = new AuditoriaService(repo).registrar("pedidos360.transacciones/ORDEN_CREADA",
                "{\"ordenId\":1}", "ev-1", "pedidos360.transacciones-0@5");

        assertEquals("pedidos360.transacciones/ORDEN_CREADA", e.getRoutingKey());
        assertEquals("pedidos360.transacciones-0@5", e.getOrigen());
        verify(repo).save(any(EventoAuditoria.class));
    }

    @Test
    void noDuplicaReentregasDeKafka() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.existsByEventoId("ev-1")).thenReturn(true);

        assertNull(new AuditoriaService(repo).registrar("x/Y", "{}", "ev-1", "x-0@1"));
        verify(repo, never()).save(any());
    }

    @Test
    void resumenAgrupaPorEvento() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.contarPorEvento()).thenReturn(List.<Object[]>of(new Object[] {"pedidos360.pagos/PAGO_APROBADO", 3L}));

        Map<String, Long> resumen = new AuditoriaService(repo).resumen();

        assertEquals(3L, resumen.get("pedidos360.pagos/PAGO_APROBADO"));
    }
}
