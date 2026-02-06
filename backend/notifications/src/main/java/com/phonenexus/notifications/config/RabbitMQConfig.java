package com.phonenexus.notifications.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "phone.nexus.notifications";
    public static final String DLX_EXCHANGE = "phone.nexus.notifications.dlx";

    public static final String ORDER_QUEUE = "q.email.order-events";
    public static final String USER_QUEUE = "q.email.user-events";
    public static final String ADMIN_QUEUE = "q.email.admin-alerts";
    public static final String DLQ_QUEUE = "q.email.failed-events";

    public static final String ORDER_ROUTING_KEY = "order.*";
    public static final String USER_ROUTING_KEY = "user.*";
    public static final String ADMIN_ROUTING_KEY = "admin.*";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }

    @Bean
    public Queue adminQueue() {
        return QueueBuilder.durable(ADMIN_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(USER_ROUTING_KEY);
    }

    @Bean
    public Binding adminBinding(Queue adminQueue, TopicExchange exchange) {
        return BindingBuilder.bind(adminQueue).to(exchange).with(ADMIN_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(dlq).to(dlxExchange).with("failed");
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
