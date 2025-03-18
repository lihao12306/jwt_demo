package com.example.jwt.controller;

import com.example.jwt.service.AuthService;  // 导入 AuthService，用于业务逻辑处理
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController  // 标记为 REST 控制器，能够处理 HTTP 请求并返回响应
@RequestMapping("/auth")  // 所有接口路径的前缀都为 /auth
public class AuthController {

    @Autowired
    private AuthService authService;  // 注入 AuthService，用于处理与认证相关的业务逻辑

    /**
     * 登录接口
     *
     * @param request 请求体，包含用户的用户名和密码
     * @return ResponseEntity 返回登录后的 JWT 令牌或者错误信息
     */
    @PostMapping("/login")  // 映射 POST 请求到 /auth/login 路径
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            // 从请求体中获取用户名和密码，并调用 AuthService 的 login 方法进行登录逻辑处理
            String token = authService.login(request.get("username"), request.get("password"));

            // 如果登录成功，返回包含 token 的响应
            return ResponseEntity.ok(Map.of("token", token));  // 200 OK 状态码，并携带生成的 token

        } catch (RuntimeException e) {
            // 如果发生异常（例如用户名或密码错误），返回 401 未授权错误
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));  // 401 错误，包含错误信息
        }
    }
}
