package com.tienda.ms_auditoria.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

/**
 * ms-auditoria consume TODOS los topicos sin interpretar su contenido (Map generico) y declara los
 * topicos de mensajes defectuosos (.dlt, 14 dias de retencion) donde termina lo que no se pudo procesar.
 */
@Configuration
public class KafkaTopicosConfig {

    @Value("${app.kafka.replicas}")
    private int replicas;

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ConcurrentKafkaListenerContainerFactory<String, Map<String, Object>> auditoriaFactory(
            KafkaConsumerConfig consumidores, KafkaTemplate<String, Object> template) {
        return (ConcurrentKafkaListenerContainerFactory) consumidores.fabrica(Map.class, template);
    }

    @Bean public NewTopic dltNavegacion(@Value("${app.kafka.topicos.navegacion.nombre}") String t) { return dlt(t); }
    @Bean public NewTopic dltPagos(@Value("${app.kafka.topicos.pagos.nombre}") String t) { return dlt(t); }
    @Bean public NewTopic dltFraude(@Value("${app.kafka.topicos.fraude.nombre}") String t) { return dlt(t); }
    @Bean public NewTopic dltTransacciones(@Value("${app.kafka.topicos.transacciones.nombre}") String t) { return dlt(t); }

    private NewTopic dlt(String topico) {
        return Topicos.crear(topico + ".dlt", 1, replicas, 1209600000L, "delete", 1);
    }
}
