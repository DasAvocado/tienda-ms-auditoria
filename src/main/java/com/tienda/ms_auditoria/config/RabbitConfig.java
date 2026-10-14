package com.tienda.ms_auditoria.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** ms-auditoria escucha TODOS los eventos de la exchange (routing key "#"). */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pedidos360.events";
    public static final String QUEUE_AUDITORIA = "auditoria.todos";

    @Bean
    public TopicExchange eventosExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue colaAuditoria() {
        return QueueBuilder.durable(QUEUE_AUDITORIA).build();
    }

    @Bean
    public Binding bindingAuditoria() {
        return BindingBuilder.bind(colaAuditoria()).to(eventosExchange()).with("#");
    }
}
