package com.example.jwt.service;

import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository 用于与数据库交互，查询用户信息

    @Autowired
    private PasswordEncoder passwordEncoder;  // 注入 PasswordEncoder 用于密码加密（虽然当前代码没有用到加密）

    @Autowired
    private JwtUtil jwtUtil;  // 注入 JwtUtil 用于生成 JWT 令牌

    /**
     * 用户登录验证方法
     *
     * @param username 用户名
     * @param password 用户输入的密码
     * @return 生成的 JWT 令牌
     * @throws RuntimeException 如果用户名不存在或密码错误，抛出异常
     */
    public String login(String username, String password) {
        // 查找用户信息，如果用户不存在则抛出异常
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));  // 如果找不到用户，则抛出“用户不存在”的异常

        // 打印调试信息，显示请求传来的密码和从数据库查询到的用户密码
        System.out.println("request password :" + password);  // 打印传入的密码（未加密）
        System.out.println("user password :" + user.getPassword());  // 打印存储在数据库中的密码（加密后）

        // 直接比较明文密码
        if (!password.equals(user.getPassword())) {  // 明文密码比较（不建议直接用明文比较）
            throw new RuntimeException("密码错误");  // 如果密码不匹配，抛出密码错误异常
        }

        // 如果密码正确，生成 JWT 令牌
        return jwtUtil.generateToken(user.getUsername());  // 返回生成的 JWT 令牌
    }
}
