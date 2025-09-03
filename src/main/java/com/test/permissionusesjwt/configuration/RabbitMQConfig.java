package com.test.permissionusesjwt.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    public static final String EMAIL_QUEUE = "email_queue";
    public static final String VIDEO_QUEUE = "video_queue";
    public static final String MAIN_EXCHANGE = "main_exchange";
    public static final String MAIL_ROUTING_KEY = "mail";
    public static final String VIDEO_ROUTING_KEY = "video";

    public static final String EMAIL_DLQ = "email.queue.dlq";
    public static final String EMAIL_DLQ_ROUTING_KEY = "email.routingKey.dlq";

    // Declare queues individually so they are registered as Spring beans
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")   // tên DLX
                .withArgument("x-dead-letter-routing-key", "dlx.email")
                .build();
    }

    @Bean
    public Queue videoQueue() {
        return new Queue(VIDEO_QUEUE, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

// DLQ
    @Bean
    public Queue emailDLQ() {
        return QueueBuilder.durable("email.dlq").build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx.exchange");
    }

    @Bean
    public Binding bindEmailDLQ() {
        return BindingBuilder.bind(emailDLQ())
                .to(dlxExchange())
                .with("dlx.email");
    }


    // Direct Exchange
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with(MAIL_ROUTING_KEY);
    }

    @Bean
    public Binding videoBinding(Queue videoQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(videoQueue).to(directExchange).with(VIDEO_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);  // set converter
        return template;
    }

    @Bean (name ="mailTaskExecutor")
    public ThreadPoolTaskExecutor mailTaskExecutor () {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MailTaskExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);

        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);

        factory.setPrefetchCount(5);


        return factory;
    }
}
