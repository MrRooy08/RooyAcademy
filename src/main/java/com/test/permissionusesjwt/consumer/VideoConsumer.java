package com.test.permissionusesjwt.consumer;

import com.test.permissionusesjwt.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VideoConsumer {

    @RabbitListener (queues = RabbitMQConfig.VIDEO_QUEUE)
    public void handleVideo (String message){

    }
}
