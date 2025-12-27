package com.wic.edu.kg.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     * 
     * @param to   收件人邮箱
     * @param code 验证码
     */
    void sendVerificationCode(String to, String code);

    /**
     * 发送账号激活邮件
     * 
     * @param to       收件人邮箱
     * @param code     激活码
     * @param username 用户名
     */
    void sendActivationEmail(String to, String code, String username);

    /**
     * 发送密码重置邮件
     * 
     * @param to       收件人邮箱
     * @param code     验证码
     * @param username 用户名
     */
    void sendPasswordResetEmail(String to, String code, String username);
}
