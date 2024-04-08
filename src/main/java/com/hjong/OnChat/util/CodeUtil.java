package com.hjong.OnChat.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Random;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/30
 **/

@Slf4j
@Component
public class CodeUtil {
    private static final String key = "s8cks92c"; // 8位秘钥

    // 加密
    private String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        Key secretKey = new SecretKeySpec(key.getBytes(), "DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    //解密
    private String decrypt(String message) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(message);
        Cipher cipher = Cipher.getInstance("DES");
        Key secretKey = new SecretKeySpec(key.getBytes(), "DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(bytes));
    }

    // 生成4位随机数
    private String random4() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    // 生成验证码
    public String generate(String email) throws Exception {
        String time = String.valueOf(System.currentTimeMillis());
        String code = random4();
        String token = encrypt(email + "&" + code + "&" + time);
        return code + "&" + token;
    }

    // 验证验证码
    public boolean verify(String email, String code, String token) throws Exception {
        String text;
        try {
            text = decrypt(token);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        String[] items = text.split("&");
        if (items.length == 3
                && items[0].equals(email)
                && items[1].equals(code)
                && System.currentTimeMillis() - Long.parseLong(items[2]) < 10 * 60 * 500)
            return true;
        else
            return false;
    }
}
