package com.test.permissionusesjwt.consumer;

import com.test.permissionusesjwt.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailConsumer {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    @Qualifier ("mailTaskExecutor")
    ThreadPoolTaskExecutor mailTaskExecutor;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consume(Map<String, String> data) {
        try {
            String email = data.get("email");
            String otp = data.get("otp");
            mailTaskExecutor.execute(() -> sendEmail(email, otp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail (String email, String otp){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Xác thực tài khoản của bạn");
        simpleMailMessage.setText("Mã OTP của bạn là: " + otp + "\nOTP có hiệu lực trong 5 phút.");
        mailSender.send(simpleMailMessage);
    }

}
