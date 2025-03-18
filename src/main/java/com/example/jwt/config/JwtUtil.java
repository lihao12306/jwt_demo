package com.example.jwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component  // 这个类会被 Spring 容器管理，作为一个 Bean 注入到其他类中
public class JwtUtil {

    // JWT 使用的密钥，保持私密并用于签名
    private final String secret = "yTqO/F4g2PflcPfHmbj07kSeF1nApC+uYmT3qSwcfVg=";

    // JWT 的有效期，单位为毫秒，这里设置为 1 小时
    private final long expiration = 3600000; // 1 小时

    /**
     * 生成 JWT Token
     *
     * @param username 用户名
     * @return 生成的 JWT Token 字符串
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // 设置 JWT 的主题（用户名）
                .setIssuedAt(new Date())  // 设置 JWT 的签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 设置 JWT 的过期时间
                .signWith(SignatureAlgorithm.HS256, secret)  // 使用 HS256 签名算法和密钥生成 JWT
                .compact();  // 构建并返回 JWT 字符串
    }
    /**
     * 提取 JWT 中的 Claims（声明）
     *
     * @param token JWT Token
     * @return JWT 中的 Claims 对象
     */
    public Claims extractClaims(String token) {
        // 使用密钥解析 JWT，并获取 Claims 对象
        //成功解析后，提取 Claims（有效负载），包括用户信息、过期时间、角色
        return Jwts.parser()
                .setSigningKey(secret)// 使用你的密钥验证签名
                .parseClaimsJws(token) // 解析 JWT
                .getBody();// 返回负载（Claims）
    }
}
