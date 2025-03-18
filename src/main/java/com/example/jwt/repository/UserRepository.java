package com.example.jwt.repository;

import com.example.jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 用户存储库接口，继承 JpaRepository 提供基本的 CRUD 操作。
 * 提供根据用户名查找用户的方法。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户。
     *
     * @param username 用户名
     * @return 如果找到用户，返回一个包含用户的 Optional；否则，返回一个空的 Optional。
     */
    Optional<User> findByUsername(String username);
}
