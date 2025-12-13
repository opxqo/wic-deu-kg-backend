package com.wic.edu.kg.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {
    
    /**
     * 生成并存储验证码
     * @param key 键（如邮箱、学号等）
     * @param type 验证码类型（ACTIVATION/RESET_PASSWORD等）
     * @return 生成的验证码
     */
    String generateCode(String key, String type);
    
    /**
     * 生成激活令牌（用于链接激活）
     * @param email 邮箱
     * @return 激活令牌
     */
    String generateActivationToken(String email);
    
    /**
     * 验证激活令牌
     * @param token 激活令牌
     * @return 对应的邮箱，如果无效或过期返回null
     */
    String verifyActivationToken(String token);
    
    /**
     * 删除激活令牌
     * @param token 激活令牌
     */
    void removeActivationToken(String token);
    
    /**
     * 验证验证码
     * @param key 键
     * @param type 验证码类型
     * @param code 用户输入的验证码
     * @return 是否正确
     */
    boolean verifyCode(String key, String type, String code);
    
    /**
     * 删除验证码
     * @param key 键
     * @param type 验证码类型
     */
    void removeCode(String key, String type);
    
    /**
     * 检查是否可以发送验证码（防止频繁发送）
     * @param key 键
     * @param type 验证码类型
     * @return 距离下次可发送的秒数，0表示可以发送
     */
    int getResendCooldown(String key, String type);
}
