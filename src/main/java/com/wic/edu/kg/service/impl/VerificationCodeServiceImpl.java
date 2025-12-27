package com.wic.edu.kg.service.impl;

import com.wic.edu.kg.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务实现（内存存储版本）
 * 生产环境建议使用Redis存储
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    // 验证码存储：key -> (code, expireTime)
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    // 发送时间记录：key -> sendTime（用于防止频繁发送）
    private final Map<String, Long> sendTimeStore = new ConcurrentHashMap<>();

    // 验证码有效期（毫秒）：10分钟
    private static final long CODE_EXPIRE_TIME = 10 * 60 * 1000;

    // 重发冷却时间（毫秒）：60秒
    private static final long RESEND_COOLDOWN = 60 * 1000;

    // 最大错误尝试次数
    private static final int MAX_ATTEMPTS = 5;

    // 错误尝试次数记录
    private final Map<String, Integer> attemptStore = new ConcurrentHashMap<>();

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateCode(String key, String type) {
        String storeKey = buildKey(key, type);

        // 检查重发冷却
        int cooldown = getResendCooldown(key, type);
        if (cooldown > 0) {
            throw new RuntimeException("请等待 " + cooldown + " 秒后再发送");
        }

        // 生成6位数字验证码
        String code = String.format("%06d", random.nextInt(1000000));

        // 存储验证码
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_TIME;
        codeStore.put(storeKey, new CodeEntry(code, expireTime));

        // 记录发送时间
        sendTimeStore.put(storeKey, System.currentTimeMillis());

        // 重置错误尝试次数
        attemptStore.remove(storeKey);

        log.info("生成验证码: key={}, type={}, code={}", key, type, code);

        return code;
    }

    @Override
    public boolean verifyCode(String key, String type, String code) {
        String storeKey = buildKey(key, type);

        // 检查是否超过最大尝试次数
        int attempts = attemptStore.getOrDefault(storeKey, 0);
        if (attempts >= MAX_ATTEMPTS) {
            log.warn("验证码尝试次数过多: key={}, type={}", key, type);
            throw new RuntimeException("验证码错误次数过多，请重新获取验证码");
        }

        CodeEntry entry = codeStore.get(storeKey);

        if (entry == null) {
            attemptStore.put(storeKey, attempts + 1);
            log.warn("验证码不存在: key={}, type={}", key, type);
            return false;
        }

        // 检查是否过期
        if (System.currentTimeMillis() > entry.expireTime) {
            codeStore.remove(storeKey);
            log.warn("验证码已过期: key={}, type={}", key, type);
            return false;
        }

        // 验证码比较（忽略前后空格）
        boolean match = code != null && code.trim().equals(entry.code);

        if (!match) {
            attemptStore.put(storeKey, attempts + 1);
            log.warn("验证码错误: key={}, type={}, 剩余尝试次数={}", key, type, MAX_ATTEMPTS - attempts - 1);
        }

        return match;
    }

    @Override
    public void removeCode(String key, String type) {
        String storeKey = buildKey(key, type);
        codeStore.remove(storeKey);
        sendTimeStore.remove(storeKey);
        attemptStore.remove(storeKey);
        log.info("删除验证码: key={}, type={}", key, type);
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

    @Override
    public int getRemainingTime(String key, String type) {
        String storeKey = buildKey(key, type);
        CodeEntry entry = codeStore.get(storeKey);

        if (entry == null) {
            return 0;
        }

        long remaining = entry.expireTime - System.currentTimeMillis();
        if (remaining <= 0) {
            return 0;
        }

        return (int) (remaining / 1000);
    }

    private String buildKey(String key, String type) {
        return type + ":" + key;
    }

    /**
     * 定时清理过期验证码（每5分钟执行一次）
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        int removed = 0;

        for (Map.Entry<String, CodeEntry> entry : codeStore.entrySet()) {
            if (now > entry.getValue().expireTime) {
                codeStore.remove(entry.getKey());
                sendTimeStore.remove(entry.getKey());
                attemptStore.remove(entry.getKey());
                removed++;
            }
        }

        if (removed > 0) {
            log.info("清理过期验证码: {} 个", removed);
        }
    }

    /**
     * 验证码存储条目
     */
    private static class CodeEntry {
        final String code;
        final long expireTime;

        CodeEntry(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
}
