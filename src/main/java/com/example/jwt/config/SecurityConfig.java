package com.example.jwt.config;

import com.example.jwt.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 标记这是一个配置类，Spring 会自动加载它
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // JWT 认证过滤器

    // 构造函数注入 JwtAuthFilter，这个过滤器将用于校验 JWT Token
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * 配置密码加密器，用于用户密码的加密和验证。
     * BCrypt 是一种常见的加密算法，它通过哈希函数生成不可逆的加密字符串。
     * 这种方式可以保护存储在数据库中的用户密码。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt 算法进行加密
    }

    /**
     * 配置 HTTP 安全性，定义哪些请求需要认证，哪些可以公开访问
     * 并且配置我们的自定义 JWT 认证过滤器。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF（跨站请求伪造）保护，避免在 REST API 中干扰请求
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // 允许匿名访问登录接口，不需要认证
                        .anyRequest().authenticated() // 其他所有请求都需要认证，必须有有效的 JWT 才能访问
                )
                // 我们使用 addFilterBefore() 确保 JWT 认证 在 用户名密码认证 之前执行。这样可以优先解析 JWT，确保携带 JWT 的用户无需再走用户名密码登录逻辑。
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // 在认证过滤器之前加入自定义的 JWT 过滤器，保证每次请求都经过 JWT 认证
                .build(); // 构建并返回 SecurityFilterChain 对象，Spring Security 会自动使用它来管理安全配置
    }
}
