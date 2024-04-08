package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.Mail;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import static com.hjong.OnChat.entity.Constants.TEXT_MAIL;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/29
 **/
@Service
public class EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String username;

    public Mono<Void> sendEmail(Mail mail) {
        return Mono.fromRunnable(() -> {
                    if(mail.getTitle().equals(TEXT_MAIL)){
                        SimpleMailMessage message = createTextMessage(mail.getTitle(), mail.getContent(), mail.getEmail());
                        mailSender.send(message);
                    }else {
                        MimeMessagePreparator message = createHtmlMessage(mail.getTitle(), mail.getContent(), mail.getEmail());
                        mailSender.send(message);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private SimpleMailMessage createTextMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }

    public MimeMessagePreparator createHtmlMessage(String title, String htmlContent, String email) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true); // true表示支持多部分消息
            messageHelper.setSubject(title);
            messageHelper.setTo(email);
            messageHelper.setFrom(username);
            messageHelper.setText(htmlContent, true);
        };
    }
}
