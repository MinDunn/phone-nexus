package com.phonenexus.identities.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "phone.nexus.notifications";
    public static final String USER_ROUTING_KEY = "user.registered";
    public static final String LOYALTY_QUEUE = "loyalty.points.queue";
    public static final String LOYALTY_ROUTING_KEY = "loyalty.points.earned";

    @org.springframework.context.annotation.Bean
    public org.springframework.amqp.core.Queue loyaltyQueue() {
        return new org.springframework.amqp.core.Queue(LOYALTY_QUEUE);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.amqp.core.TopicExchange exchange() {
        return new org.springframework.amqp.core.TopicExchange(EXCHANGE);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.amqp.core.Binding loyaltyBinding(org.springframework.amqp.core.Queue loyaltyQueue,
            org.springframework.amqp.core.TopicExchange exchange) {
        return org.springframework.amqp.core.BindingBuilder.bind(loyaltyQueue).to(exchange).with(LOYALTY_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }
}
