package com.wic.edu.kg.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {

    /**
     * 生成并存储验证码
     * 
     * @param key  键（如邮箱、学号等）
     * @param type 验证码类型（ACTIVATION/RESET_PASSWORD等）
     * @return 生成的验证码
     */
    String generateCode(String key, String type);

    /**
     * 验证验证码
     * 
     * @param key  键
     * @param type 验证码类型
     * @param code 用户输入的验证码
     * @return 是否正确
     */
    boolean verifyCode(String key, String type, String code);

    /**
     * 删除验证码（验证成功后调用）
     * 
     * @param key  键
     * @param type 验证码类型
     */
    void removeCode(String key, String type);

    /**
     * 检查是否可以发送验证码（防止频繁发送）
     * 
     * @param key  键
     * @param type 验证码类型
     * @return 距离下次可发送的秒数，0表示可以发送
     */
    int getResendCooldown(String key, String type);

    /**
     * 获取验证码剩余有效时间（秒）
     * 
     * @param key  键
     * @param type 验证码类型
     * @return 剩余秒数，0表示已过期或不存在
     */
    int getRemainingTime(String key, String type);
}
