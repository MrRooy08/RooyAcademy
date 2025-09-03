package com.test.permissionusesjwt.producer;

import com.test.permissionusesjwt.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMailMessage(String email, String otp) {
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("otp", otp);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.MAIN_EXCHANGE,
                RabbitMQConfig.MAIL_ROUTING_KEY,
                data
        );
    }

    public void sendVideoMessage(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.MAIN_EXCHANGE,
                RabbitMQConfig.VIDEO_ROUTING_KEY,
                message
        );
    }

}
