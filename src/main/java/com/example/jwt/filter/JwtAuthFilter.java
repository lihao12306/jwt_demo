package com.example.jwt.filter;

import com.example.jwt.config.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // 标记为 Spring 组件，以便自动注入到 Spring 上下文
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // 注入 JwtUtil 用于解析和验证 JWT

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 在每个请求到达时执行的过滤器方法，用于从请求头中获取 JWT Token 并进行验证。
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException 异常
     * @throws IOException 输入输出异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取 Authorization 头部的内容
        String authHeader = request.getHeader("Authorization");

        // 如果请求头中没有 Authorization，或者其不以 "Bearer " 开头，直接跳过该过滤器
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 继续执行下一个过滤器
            return; // 直接返回
        }

        // 提取 JWT Token（去掉 "Bearer " 前缀）
        String token = authHeader.substring(7);

        try {
            // 使用 JwtUtil 提取 JWT 中的 Claims 信息
            Claims claims = jwtUtil.extractClaims(token);
            // 从 Claims 中获取用户名（即 token 的 subject 部分）
            String username = claims.getSubject();

            // 如果用户名不为空，并且当前 SecurityContext 中没有已认证的信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 创建 UsernamePasswordAuthenticationToken 认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null);
                // 设置认证细节
                //authentication相当于一张“通行证”
                //这个 authentication 就是我们在 JwtAuthFilter 中放入的对象，是 Spring Security 通过 ThreadLocal 机制存储的，确保请求之间隔离。
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息保存到 SecurityContext 中，Spring Security 会在后续进行权限验证
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 如果 JWT 解析失败，打印错误信息
            System.out.println("JWT 解析失败：" + e.getMessage());
        }

        // 继续执行后续的过滤器
        filterChain.doFilter(request, response);
    }
}
