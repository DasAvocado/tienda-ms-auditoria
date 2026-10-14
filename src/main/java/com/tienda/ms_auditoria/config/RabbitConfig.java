package com.tienda.ms_auditoria.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ de ms-auditoria. Los NOMBRES se definen solo en application.yml (app.rabbit.*).
 *
 *   [pedidos360.events] --"#" (todos los eventos)--> auditoria.todos   (consume AuditoriaConsumer)
 *   auditoria.todos --(rechazo/reintentos agotados)--> [pedidos360.dlx] --> auditoria.todos.dlq
 */
@Configuration
public class RabbitConfig {

    private final String exchange;
    private final String dlx;
    private final String patronTodos;
    private final String colaAuditoria;
    private final String colaAuditoriaDlq;

    public RabbitConfig(@Value("${app.rabbit.exchange}") String exchange,
                        @Value("${app.rabbit.dlx}") String dlx,
                        @Value("${app.rabbit.routing-keys.todos}") String patronTodos,
                        @Value("${app.rabbit.queues.auditoria}") String colaAuditoria,
                        @Value("${app.rabbit.queues.auditoria-dlq}") String colaAuditoriaDlq) {
        this.exchange = exchange;
        this.dlx = dlx;
        this.patronTodos = patronTodos;
        this.colaAuditoria = colaAuditoria;
        this.colaAuditoriaDlq = colaAuditoriaDlq;
    }

    @Bean
    public TopicExchange eventosExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlx, true, false);
    }

    // ---------- Caso de uso: registrar todos los eventos de la tienda ----------
    @Bean
    public Queue colaAuditoria() {
        return QueueBuilder.durable(colaAuditoria)
                .deadLetterExchange(dlx)
                .deadLetterRoutingKey(colaAuditoriaDlq)
                .build();
    }

    @Bean
    public Binding bindingAuditoria() {
        return BindingBuilder.bind(colaAuditoria()).to(eventosExchange()).with(patronTodos);
    }

    @Bean
    public Queue colaAuditoriaDlq() {
        return QueueBuilder.durable(colaAuditoriaDlq).build();
    }

    @Bean
    public Binding bindingAuditoriaDlq() {
        return BindingBuilder.bind(colaAuditoriaDlq()).to(deadLetterExchange()).with(colaAuditoriaDlq);
    }
}
