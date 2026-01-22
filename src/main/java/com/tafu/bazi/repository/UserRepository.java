package com.tafu.bazi.repository;

import com.tafu.bazi.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepository
 *
 * <p>描述: 用户数据访问接口。
 *
 * <p>包含内容: 1. 根据手机号查询用户 2. 根据用户名查询用户
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByPhone(String phone);

  Optional<User> findByUsername(String username);

  boolean existsByPhone(String phone);

  boolean existsByUsername(String username);
}
