package com.tienda.ms_auditoria.repository;

import com.tienda.ms_auditoria.model.EventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {

    List<EventoAuditoria> findTop100ByOrderByFechaDesc();

    @Query("select e.routingKey, count(e) from EventoAuditoria e group by e.routingKey")
    List<Object[]> contarPorEvento();
}
