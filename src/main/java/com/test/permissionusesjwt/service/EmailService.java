package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.entity.OtpVerification;
import com.test.permissionusesjwt.repository.OtpVerificationRepository;
import com.test.permissionusesjwt.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.NonFinal;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    JavaMailSender mailSender;
    TemplateEngine templateEngine;
    private final OtpVerificationRepository otpRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @NonFinal
    @Value("${spring.mail.username}")
    String from;

    @NonFinal
    @Value("${app.frontend-url}")
    String baseUrl;

    public void sendDashboardLinkEmail(String to, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject != null ? subject : "Truy cập Dashboard của bạn");

            // Gắn Content-ID logo
            Context ctx = new Context();
            ctx.setVariable("greeting", "Xin chào,");
            ctx.setVariable("message", "Nhấn vào nút bên dưới để truy cập dashboard của bạn:");
            ctx.setVariable("url", baseUrl + "/dashboard");
            ctx.setVariable("buttonText", "Mở Dashboard");
            ctx.setVariable("year", java.time.Year.now().getValue());
            ctx.setVariable("logoCid", "logoImage"); // CID phải trùng với cid bên HTML

            String html = templateEngine.process("email/dashboard-link.html", ctx);
            helper.setText(html, true);

            // Gắn ảnh logo từ resources
            FileSystemResource res = new FileSystemResource(new File("src/main/resources/static/images/minhrom.png"));
            helper.addInline("logoImage", res); // logoImage = CID

            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Email sending failed", e);
            throw new RuntimeException("Không thể gửi email", e);
        }
    }

    public OtpVerification sendOtp(String email) {

        // Tạo OTP ngẫu nhiên
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));

        // Lưu OTP vào DB
        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);
        otpRepository.save(otpEntity);

        return otpEntity;
    }


    @Scheduled(fixedRate = 60000)
    @Transactional// chạy mỗi 60 giây
    public void deleteExpiredOtps() {
        otpRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
    }
}
