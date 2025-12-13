package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.exception.BusinessException;
import com.wic.edu.kg.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.backend-url}")
    private String backendUrl;

    @Override
    @Async
    public void sendVerificationCode(String to, String code) {
        String subject = "【WIC教育平台】验证码";
        String content = buildVerificationCodeHtml(code);
        sendHtmlEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendActivationEmail(String to, String code, String username) {
        String subject = "【WIC教育平台】账号激活";
        String content = buildActivationEmailHtml(code, username);
        sendHtmlEmail(to, subject, content);
    }
    
    @Override
    @Async
    public void sendActivationLinkEmail(String to, String token, String username) {
        String subject = "【WIC教育平台】账号激活 - 点击链接立即激活";
        String activationLink = backendUrl + "/api/auth/activate-by-link?token=" + token;
        String content = buildActivationLinkEmailHtml(activationLink, username);
        sendHtmlEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String code, String username) {
        String subject = "【WIC教育平台】密码重置";
        String content = buildPasswordResetHtml(code, username);
        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("邮件发送失败(消息异常): to={}, subject={}, error={}", to, subject, e.getMessage());
            throw new BusinessException(500, "邮件发送失败，请稍后重试");
        } catch (MailException e) {
            // 处理邮箱地址无效、用户不存在等情况
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("User not found")) {
                log.warn("邮件发送失败(邮箱不存在): to={}, subject={}", to, subject);
                // 由于是异步方法，这里只记录日志，不抛出异常
                // 用户需要检查邮箱地址是否正确
            } else if (errorMsg != null && errorMsg.contains("Invalid Addresses")) {
                log.warn("邮件发送失败(无效地址): to={}, subject={}, error={}", to, subject, errorMsg);
            } else {
                log.error("邮件发送失败(邮件异常): to={}, subject={}, error={}", to, subject, errorMsg);
            }
        } catch (Exception e) {
            log.error("邮件发送失败(未知异常): to={}, subject={}, error={}", to, subject, e.getMessage(), e);
        }
    }

    private String buildVerificationCodeHtml(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 24px; }
                    .content { padding: 40px 30px; text-align: center; }
                    .code { font-size: 36px; font-weight: bold; color: #667eea; letter-spacing: 8px; background-color: #f8f9fa; padding: 20px 40px; border-radius: 8px; display: inline-block; margin: 20px 0; }
                    .note { color: #6c757d; font-size: 14px; margin-top: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎓 WIC教育平台</h1>
                    </div>
                    <div class="content">
                        <h2>您的验证码</h2>
                        <p>您正在进行账号操作，验证码如下：</p>
                        <div class="code">%s</div>
                        <p class="note">验证码有效期为10分钟，请勿将验证码透露给他人。</p>
                    </div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿直接回复。</p>
                        <p>© 2025 WIC教育平台 版权所有</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(code);
    }

    private String buildActivationEmailHtml(String code, String username) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); padding: 30px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 24px; }
                    .content { padding: 40px 30px; text-align: center; }
                    .welcome { font-size: 18px; color: #333; margin-bottom: 10px; }
                    .code { font-size: 36px; font-weight: bold; color: #11998e; letter-spacing: 8px; background-color: #f8f9fa; padding: 20px 40px; border-radius: 8px; display: inline-block; margin: 20px 0; }
                    .note { color: #6c757d; font-size: 14px; margin-top: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎓 WIC教育平台</h1>
                    </div>
                    <div class="content">
                        <p class="welcome">亲爱的 <strong>%s</strong>，欢迎加入WIC教育平台！</p>
                        <h2>账号激活验证码</h2>
                        <p>请输入以下验证码完成账号激活：</p>
                        <div class="code">%s</div>
                        <p class="note">验证码有效期为10分钟，请尽快完成激活。</p>
                        <p class="note">如果您没有注册WIC教育平台账号，请忽略此邮件。</p>
                    </div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿直接回复。</p>
                        <p>© 2025 WIC教育平台 版权所有</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, code);
    }
    
    private String buildActivationLinkEmailHtml(String activationLink, String username) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); padding: 30px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 24px; }
                    .content { padding: 40px 30px; text-align: center; }
                    .welcome { font-size: 18px; color: #333; margin-bottom: 10px; }
                    .btn { display: inline-block; background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); color: #ffffff !important; text-decoration: none; padding: 15px 40px; border-radius: 50px; font-size: 18px; font-weight: bold; margin: 25px 0; box-shadow: 0 4px 15px rgba(17, 153, 142, 0.4); transition: transform 0.2s; }
                    .btn:hover { transform: translateY(-2px); }
                    .link-text { color: #6c757d; font-size: 12px; word-break: break-all; margin-top: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 5px; }
                    .note { color: #6c757d; font-size: 14px; margin-top: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎓 WIC教育平台</h1>
                    </div>
                    <div class="content">
                        <p class="welcome">亲爱的 <strong>%s</strong>，欢迎加入WIC教育平台！</p>
                        <h2>🎉 只需一步，激活您的账号</h2>
                        <p>点击下方按钮立即激活您的账号：</p>
                        <a href="%s" class="btn">✨ 立即激活账号</a>
                        <p class="note">如果按钮无法点击，请复制以下链接到浏览器打开：</p>
                        <p class="link-text">%s</p>
                        <p class="note">⏰ 链接有效期为24小时，请尽快完成激活。</p>
                        <p class="note">如果您没有注册WIC教育平台账号，请忽略此邮件。</p>
                    </div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿直接回复。</p>
                        <p>© 2025 WIC教育平台 版权所有</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, activationLink, activationLink);
    }

    private String buildPasswordResetHtml(String code, String username) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #fc4a1a 0%%, #f7b733 100%%); padding: 30px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 24px; }
                    .content { padding: 40px 30px; text-align: center; }
                    .welcome { font-size: 18px; color: #333; margin-bottom: 10px; }
                    .code { font-size: 36px; font-weight: bold; color: #fc4a1a; letter-spacing: 8px; background-color: #f8f9fa; padding: 20px 40px; border-radius: 8px; display: inline-block; margin: 20px 0; }
                    .note { color: #6c757d; font-size: 14px; margin-top: 20px; }
                    .warning { color: #dc3545; font-size: 14px; margin-top: 10px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 密码重置</h1>
                    </div>
                    <div class="content">
                        <p class="welcome">亲爱的 <strong>%s</strong></p>
                        <h2>密码重置验证码</h2>
                        <p>您正在重置密码，验证码如下：</p>
                        <div class="code">%s</div>
                        <p class="note">验证码有效期为10分钟。</p>
                        <p class="warning">⚠️ 如果您没有请求重置密码，请忽略此邮件并确保账号安全。</p>
                    </div>
                    <div class="footer">
                        <p>此邮件由系统自动发送，请勿直接回复。</p>
                        <p>© 2025 WIC教育平台 版权所有</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, code);
    }
}
