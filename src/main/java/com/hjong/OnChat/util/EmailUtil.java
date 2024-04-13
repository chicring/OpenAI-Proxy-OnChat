package com.hjong.OnChat.util;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/11
 **/

@Component
public class EmailUtil {


    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String username;

    /**
     * 发送邮件验证码
     * @param email 邮箱
     * @param code 验证码
     * @return Mono<Void>
     */
    public Mono<Void> sendEmailVerifyCode(String email, String code) {
        return Mono.fromRunnable(() -> {
            String title = "OnChat 验证码";
            String content = "您的验证码是：" + code + "，请在3分钟内完成验证。";
            SimpleMailMessage message = createTextMessage(title, content, email);
            mailSender.send(message);
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

    private MimeMessagePreparator createHtmlMessage(String title, String htmlContent, String email) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true); // true表示支持多部分消息
            messageHelper.setSubject(title);
            messageHelper.setTo(email);
            messageHelper.setFrom(username);
            messageHelper.setText(htmlContent, true);
        };
    }
}
