package com.neuedu.yyzx.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类 —— 采用 RS256 非对称签名。
 * <p>
 * 签发时使用 RSA 私钥（jwt-private.pem，PKCS#8）；
 * 校验时使用 RSA 公钥（jwt-public.pem，X.509）。
 * <p>
 * 私钥仅保存在服务端，公钥可下发给第三方做无密钥验证，安全性高于 HS256。
 */
@Component
public class JwtUtils {

    @Value("${jwt.private-key:classpath:jwt/jwt-private.pem}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key:classpath:jwt/jwt-public.pem}")
    private Resource publicKeyResource;

    @Value("${jwt.expiration}")
    private long expiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
    }

    /**
     * 从 PEM 文件加载 PKCS#8 格式的 RSA 私钥
     */
    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        try (InputStream in = resource.getInputStream()) {
            String pem = new String(StreamUtils.copyToByteArray(in));
            String base64 = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }

    /**
     * 从 PEM 文件加载 X.509 格式的 RSA 公钥
     */
    private PublicKey loadPublicKey(Resource resource) throws Exception {
        try (InputStream in = resource.getInputStream()) {
            String pem = new String(StreamUtils.copyToByteArray(in));
            String base64 = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
    }

    /**
     * 使用 RSA 私钥签发 JWT（RS256）
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /**
     * 使用 RSA 公钥校验并解析 JWT
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /** 暴露公钥，供登录接口下发给前端做 RSA 传输加密 */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /** 暴露私钥，供登录接口解密前端传来的密文 */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
