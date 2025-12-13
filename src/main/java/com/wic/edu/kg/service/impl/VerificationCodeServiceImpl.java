package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.service.VerificationCodeService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务实现（内存存储版本）
 * 生产环境建议使用Redis存储
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    
    // 验证码存储：key -> (code, expireTime)
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();
    
    // 激活令牌存储：token -> (email, expireTime)
    private final Map<String, TokenEntry> tokenStore = new ConcurrentHashMap<>();
    
    // 发送时间记录：key -> sendTime（用于防止频繁发送）
    private final Map<String, Long> sendTimeStore = new ConcurrentHashMap<>();
    
    // 验证码有效期（毫秒）：10分钟
    private static final long CODE_EXPIRE_TIME = 10 * 60 * 1000;
    
    // 激活令牌有效期（毫秒）：24小时
    private static final long TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000;
    
    // 重发冷却时间（毫秒）：60秒
    private static final long RESEND_COOLDOWN = 60 * 1000;
    
    private final SecureRandom random = new SecureRandom();
    
    @Override
    public String generateCode(String key, String type) {
        String storeKey = buildKey(key, type);
        
        // 生成6位数字验证码
        String code = String.format("%06d", random.nextInt(1000000));
        
        // 存储验证码
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_TIME;
        codeStore.put(storeKey, new CodeEntry(code, expireTime));
        
        // 记录发送时间
        sendTimeStore.put(storeKey, System.currentTimeMillis());
        
        // 清理过期验证码
        cleanExpiredCodes();
        
        return code;
    }
    
    @Override
    public String generateActivationToken(String email) {
        // 生成安全的随机令牌
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        
        // 存储令牌
        long expireTime = System.currentTimeMillis() + TOKEN_EXPIRE_TIME;
        tokenStore.put(token, new TokenEntry(email, expireTime));
        
        // 记录发送时间（用于防止频繁发送）
        String storeKey = buildKey(email, "ACTIVATION_LINK");
        sendTimeStore.put(storeKey, System.currentTimeMillis());
        
        // 清理过期令牌
        cleanExpiredTokens();
        
        return token;
    }
    
    @Override
    public String verifyActivationToken(String token) {
        TokenEntry entry = tokenStore.get(token);
        
        if (entry == null) {
            return null;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() > entry.expireTime) {
            tokenStore.remove(token);
            return null;
        }
        
        return entry.email;
    }
    
    @Override
    public void removeActivationToken(String token) {
        tokenStore.remove(token);
    }
    
    @Override
    public boolean verifyCode(String key, String type, String code) {
        String storeKey = buildKey(key, type);
        CodeEntry entry = codeStore.get(storeKey);
        
        if (entry == null) {
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() > entry.expireTime) {
            codeStore.remove(storeKey);
            return false;
        }
        
        // 验证码比较（忽略大小写和前后空格）
        return code != null && code.trim().equals(entry.code);
    }
    
    @Override
    public void removeCode(String key, String type) {
        String storeKey = buildKey(key, type);
        codeStore.remove(storeKey);
        sendTimeStore.remove(storeKey);
    }
    
    @Override
    public int getResendCooldown(String key, String type) {
        String storeKey = buildKey(key, type);
        Long lastSendTime = sendTimeStore.get(storeKey);
        
        if (lastSendTime == null) {
            return 0;
        }
        
        long elapsed = System.currentTimeMillis() - lastSendTime;
        if (elapsed >= RESEND_COOLDOWN) {
            return 0;
        }
        
        return (int) ((RESEND_COOLDOWN - elapsed) / 1000);
    }
    
    private String buildKey(String key, String type) {
        return type + ":" + key;
    }
    
    private void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        codeStore.entrySet().removeIf(entry -> now > entry.getValue().expireTime);
    }
    
    private void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenStore.entrySet().removeIf(entry -> now > entry.getValue().expireTime);
    }
    
    private static class CodeEntry {
        final String code;
        final long expireTime;
        
        CodeEntry(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
    
    private static class TokenEntry {
        final String email;
        final long expireTime;
        
        TokenEntry(String email, long expireTime) {
            this.email = email;
            this.expireTime = expireTime;
        }
    }
}
