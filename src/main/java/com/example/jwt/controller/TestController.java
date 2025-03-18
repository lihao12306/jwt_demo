package com.example.jwt.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * TestController 类处理 "/test" 路径下的请求。
 * 该类的目的是测试 JWT Token 是否有效，并显示当前认证用户的用户名。
 */
@RestController // 标记该类为 REST 控制器，Spring 自动将其返回值转换为 JSON 格式
@RequestMapping("/test") // 所有该控制器中的请求都将以 "/test" 开头
public class TestController {

    /**
     * 测试接口，只有提供有效 JWT Token 的请求才能访问该接口。
     * 该方法从 Spring Security 的 SecurityContext 获取当前用户信息，
     * 并返回包含用户信息的响应，表示 JWT Token 是有效的。
     *
     * @return 返回访问成功的信息，表示 JWT Token 有效
     */
    @GetMapping // 将该方法映射到 GET 请求
    public String test() {
        // 从 Spring Security 的 SecurityContext 中获取认证信息。
        // SecurityContextHolder 是 Spring Security 提供的静态方法，
        // 用来访问当前线程的 SecurityContext，获取用户认证信息。
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 获取当前认证用户的用户名（即被认证的主体）
        String username = authentication.getName();
        System.out.println("当前认证用户: " + username); // 打印用户名，用于调试

        // 如果用户名为空，说明认证失败，Token 无效或已过期，可以抛出异常
        if (username == null) {
            throw new RuntimeException("Token 无效或已过期");
        }

        // 如果用户名不为空，表示 Token 验证通过，返回成功的消息，带上当前认证用户的用户名
        return "访问成功，JWT 有效，当前用户：" + username;
    }
}
/*
                                   认证流程
1.请求进入
客户端携带 Authorization: Bearer <token> 请求 API。
2.过滤器执行
JwtAuthFilter 拦截请求，解析 JWT，验证其合法性：
     验证通过：创建 UsernamePasswordAuthenticationToken 对象，设置到当前请求的 SecurityContext 中。
     验证失败：直接返回 401 Unauthorized，控制器不会被调用。
3.认证信息传递
过滤器设置的 Authentication 对象被保存到 SecurityContext 中。
4.控制器访问
通过 SecurityContextHolder.getContext().getAuthentication() 获取当前请求用户的认证信息。
 */