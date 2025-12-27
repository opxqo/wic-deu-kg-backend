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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        String subject = "【武汉城市学院】验证码通知";
        String content = buildVerificationCodeHtml(code);
        sendHtmlEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendActivationEmail(String to, String code, String username) {
        String subject = "【武汉城市学院】账号激活通知";
        String content = buildActivationEmailHtml(code, username);
        sendHtmlEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendActivationLinkEmail(String to, String token, String username) {
        String subject = "【武汉城市学院】账号激活通知";
        String activationLink = backendUrl + "/api/auth/activate-by-link?token=" + token;
        String content = buildActivationLinkEmailHtml(activationLink, username);
        sendHtmlEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String code, String username) {
        String subject = "【武汉城市学院】密码重置通知";
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
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("User not found")) {
                log.warn("邮件发送失败(邮箱不存在): to={}, subject={}", to, subject);
            } else if (errorMsg != null && errorMsg.contains("Invalid Addresses")) {
                log.warn("邮件发送失败(无效地址): to={}, subject={}, error={}", to, subject, errorMsg);
            } else {
                log.error("邮件发送失败(邮件异常): to={}, subject={}, error={}", to, subject, errorMsg);
            }
        } catch (Exception e) {
            log.error("邮件发送失败(未知异常): to={}, subject={}, error={}", to, subject, e.getMessage(), e);
        }
    }

    private String getCommonStyles() {
        return """
                body { font-family: 'Microsoft YaHei', 'SimHei', Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; line-height: 1.6; }
                .wrapper { max-width: 600px; margin: 20px auto; }
                .container { background-color: #ffffff; border: 1px solid #e0e0e0; }
                .header { background-color: #1a5c37; padding: 25px 30px; text-align: center; }
                .header-logo { width: 60px; height: 60px; margin-bottom: 10px; }
                .header-title { color: #ffffff; margin: 0; font-size: 18px; font-weight: normal; }
                .header-subtitle { color: #b8d4c5; font-size: 13px; margin-top: 5px; }
                .body { padding: 30px; color: #333333; font-size: 14px; }
                .greeting { margin-bottom: 20px; }
                .content-block { margin: 25px 0; }
                .code-box { background-color: #f8f9fa; border: 2px solid #1a5c37; border-radius: 4px; padding: 20px; text-align: center; margin: 20px 0; }
                .code { font-size: 32px; font-weight: bold; color: #1a5c37; letter-spacing: 6px; font-family: 'Courier New', monospace; }
                .info-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                .info-table td { padding: 10px 0; border-bottom: 1px solid #eeeeee; font-size: 14px; }
                .info-table td:first-child { color: #666666; width: 100px; }
                .info-table td:last-child { color: #333333; }
                .notice { background-color: #fff8e6; border-left: 4px solid #f0ad4e; padding: 15px; margin: 20px 0; font-size: 13px; color: #856404; }
                .warning { background-color: #fdf2f2; border-left: 4px solid #dc3545; padding: 15px; margin: 20px 0; font-size: 13px; color: #721c24; }
                .btn { display: inline-block; background-color: #1a5c37; color: #ffffff !important; text-decoration: none; padding: 12px 30px; border-radius: 4px; font-size: 14px; margin: 15px 0; }
                .link-fallback { background-color: #f8f9fa; padding: 10px; margin: 15px 0; font-size: 12px; word-break: break-all; color: #666666; border-radius: 4px; }
                .footer { background-color: #f8f9fa; padding: 20px 30px; border-top: 1px solid #e0e0e0; text-align: center; }
                .footer-text { font-size: 12px; color: #666666; margin: 5px 0; }
                .divider { height: 1px; background-color: #e0e0e0; margin: 20px 0; }
                """;
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"));
    }

    private String buildVerificationCodeHtml(String code) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>%s</style>
                </head>
                <body>
                    <div class="wrapper">
                        <div class="container">
                            <div class="header">
                                <img src="https://r2.wic.edu.kg/images/favicon.svg" alt="校徽" class="header-logo">
                                <h1 class="header-title">武汉城市学院 教务服务平台</h1>
                                <div class="header-subtitle">City University of Wuhan</div>
                            </div>
                            <div class="body">
                                <div class="greeting">尊敬的用户：</div>
                                <p>您正在进行身份验证操作，请使用以下验证码完成验证：</p>
                                <div class="code-box">
                                    <div class="code">%s</div>
                                </div>
                                <table class="info-table">
                                    <tr><td>有效期限</td><td>10分钟</td></tr>
                                    <tr><td>发送时间</td><td>%s</td></tr>
                                </table>
                                <div class="notice">
                                    <strong>安全提示：</strong>验证码仅用于本次操作验证，请勿将验证码透露给任何人，包括自称平台工作人员的人。
                                </div>
                            </div>
                            <div class="footer">
                                <p class="footer-text">本邮件由系统自动发送，请勿直接回复。</p>
                                <p class="footer-text">如有疑问，请联系学校信息技术中心。</p>
                                <div class="divider"></div>
                                <p class="footer-text">武汉城市学院 教务服务平台</p>
                                <p class="footer-text">地址：湖北省武汉市东湖生态旅游风景区黄家大湾1号</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(getCommonStyles(), code, getCurrentTime());
    }

    private String buildActivationEmailHtml(String code, String username) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>%s</style>
                </head>
                <body>
                    <div class="wrapper">
                        <div class="container">
                            <div class="header">
                                <img src="https://r2.wic.edu.kg/images/favicon.svg" alt="校徽" class="header-logo">
                                <h1 class="header-title">武汉城市学院 教务服务平台</h1>
                                <div class="header-subtitle">账号激活通知</div>
                            </div>
                            <div class="body">
                                <div class="greeting">尊敬的 %s：</div>
                                <p>感谢您注册武汉城市学院教务服务平台账号。请使用以下验证码完成账号激活：</p>
                                <div class="code-box">
                                    <div class="code">%s</div>
                                </div>
                                <table class="info-table">
                                    <tr><td>用户名</td><td>%s</td></tr>
                                    <tr><td>有效期限</td><td>10分钟</td></tr>
                                    <tr><td>申请时间</td><td>%s</td></tr>
                                </table>
                                <div class="notice">
                                    <strong>温馨提示：</strong>如非本人操作，请忽略此邮件，您的账号信息不会受到影响。
                                </div>
                            </div>
                            <div class="footer">
                                <p class="footer-text">本邮件由系统自动发送，请勿直接回复。</p>
                                <p class="footer-text">如有疑问，请联系学校信息技术中心。</p>
                                <div class="divider"></div>
                                <p class="footer-text">武汉城市学院 教务服务平台</p>
                                <p class="footer-text">地址：湖北省武汉市东湖生态旅游风景区黄家大湾1号</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(getCommonStyles(), username, code, username, getCurrentTime());
    }

    private String buildActivationLinkEmailHtml(String activationLink, String username) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>%s</style>
                </head>
                <body>
                    <div class="wrapper">
                        <div class="container">
                            <div class="header">
                                <img src="https://r2.wic.edu.kg/images/favicon.svg" alt="校徽" class="header-logo">
                                <h1 class="header-title">武汉城市学院 教务服务平台</h1>
                                <div class="header-subtitle">账号激活通知</div>
                            </div>
                            <div class="body">
                                <div class="greeting">尊敬的 %s：</div>
                                <p>感谢您注册武汉城市学院教务服务平台账号。请点击下方按钮完成账号激活：</p>
                                <div style="text-align: center; margin: 25px 0;">
                                    <a href="%s" class="btn">立即激活账号</a>
                                </div>
                                <p style="font-size: 13px; color: #666666;">如按钮无法点击，请复制以下链接至浏览器地址栏打开：</p>
                                <div class="link-fallback">%s</div>
                                <table class="info-table">
                                    <tr><td>用户名</td><td>%s</td></tr>
                                    <tr><td>有效期限</td><td>24小时</td></tr>
                                    <tr><td>申请时间</td><td>%s</td></tr>
                                </table>
                                <div class="notice">
                                    <strong>温馨提示：</strong>如非本人操作，请忽略此邮件，您的账号信息不会受到影响。
                                </div>
                            </div>
                            <div class="footer">
                                <p class="footer-text">本邮件由系统自动发送，请勿直接回复。</p>
                                <p class="footer-text">如有疑问，请联系学校信息技术中心。</p>
                                <div class="divider"></div>
                                <p class="footer-text">武汉城市学院 教务服务平台</p>
                                <p class="footer-text">地址：湖北省武汉市东湖生态旅游风景区黄家大湾1号</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(getCommonStyles(), username, activationLink, activationLink, username, getCurrentTime());
    }

    private String buildPasswordResetHtml(String code, String username) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>%s</style>
                </head>
                <body>
                    <div class="wrapper">
                        <div class="container">
                            <div class="header">
                                <img src="https://r2.wic.edu.kg/images/favicon.svg" alt="校徽" class="header-logo">
                                <h1 class="header-title">武汉城市学院 教务服务平台</h1>
                                <div class="header-subtitle">密码重置通知</div>
                            </div>
                            <div class="body">
                                <div class="greeting">尊敬的 %s：</div>
                                <p>您正在申请重置账号密码，请使用以下验证码完成密码重置：</p>
                                <div class="code-box">
                                    <div class="code">%s</div>
                                </div>
                                <table class="info-table">
                                    <tr><td>用户名</td><td>%s</td></tr>
                                    <tr><td>有效期限</td><td>10分钟</td></tr>
                                    <tr><td>申请时间</td><td>%s</td></tr>
                                </table>
                                <div class="warning">
                                    <strong>安全警告：</strong>如非本人操作，说明您的邮箱可能存在安全风险，请立即修改邮箱密码并检查账号安全设置。
                                </div>
                            </div>
                            <div class="footer">
                                <p class="footer-text">本邮件由系统自动发送，请勿直接回复。</p>
                                <p class="footer-text">如有疑问，请联系学校信息技术中心。</p>
                                <div class="divider"></div>
                                <p class="footer-text">武汉城市学院 教务服务平台</p>
                                <p class="footer-text">地址：湖北省武汉市东湖生态旅游风景区黄家大湾1号</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(getCommonStyles(), username, code, username, getCurrentTime());
    }
}
