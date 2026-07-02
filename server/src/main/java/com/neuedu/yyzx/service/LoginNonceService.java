package com.neuedu.yyzx.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录一次性令牌服务：
 * - 每个令牌由 (nonce, timestamp, username, HMAC-SHA256) 拼接而成
 * - HMAC 使用仅服务端持有的 secret，第三方无法伪造
 * - 令牌 60 秒有效、单次使用后立刻失效（防重放）
 * - 绑定 username，不能跨账号复用
 */
@Service
public class LoginNonceService {

    private static final long TTL_MS = 60_000L;   // 60 秒
    private static final int NONCE_BYTES = 16;    // 128 bit 随机

    private final SecureRandom random = new SecureRandom();
    private final ConcurrentHashMap<String, Long> used = new ConcurrentHashMap<>();

    @Value("${jwt.nonce-secret:neuedu-yyzx-login-nonce-secret-please-change}")
    private String secret;

    /**
     * 为即将登录的用户签发一次性令牌。
     * 即使还未输入密码也先下发（绑定 username 即可）。
     */
    public String issue(String username) {
        byte[] nb = new byte[NONCE_BYTES];
        random.nextBytes(nb);
        String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(nb);
        long ts = System.currentTimeMillis();
        String payload = nonce + "." + ts + "." + safe(username);
        String sig = hmac(payload);
        return payload + "." + sig;
    }

    /**
     * 校验并消费令牌：签名合法 + 未过期 + 未重放 + 绑定用户一致 → 返回 true。
     * 任何一项失败返回 false。令牌无论成功失败都会被标记已用（避免试探）。
     */
    public boolean consume(String token, String username) {
        if (token == null) return false;
        String[] parts = token.split("\\.");
        if (parts.length != 4) return false;

        String nonce = parts[0];
        long ts;
        try { ts = Long.parseLong(parts[1]); } catch (Exception e) { return false; }
        String boundUser = parts[2];
        String sig = parts[3];

        // 1. 重放检查
        if (used.putIfAbsent(nonce, ts) != null) return false;

        // 2. 用户绑定检查
        if (!safe(username).equals(boundUser)) return false;

        // 3. 时间窗检查
        if (System.currentTimeMillis() - ts > TTL_MS) return false;

        // 4. 签名检查
        String payload = nonce + "." + ts + "." + boundUser;
        String expected = hmac(payload);
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                                   sig.getBytes(StandardCharsets.UTF_8))) {
            return false;
        }
        return true;
    }

    /** 清理过期记录，防止内存泄漏。由登录接口低频触发即可。 */
    public void gc() {
        long now = System.currentTimeMillis();
        used.entrySet().removeIf(e -> now - e.getValue() > TTL_MS * 2);
    }

    private String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        } catch (Exception e) {
            throw new RuntimeException("HMAC failed", e);
        }
    }

    /** username 可能含 '.'，替换为 '_' 避免破坏分隔符。 */
    private String safe(String s) {
        return s == null ? "" : s.replace('.', '_').replace(' ', '_');
    }
}
