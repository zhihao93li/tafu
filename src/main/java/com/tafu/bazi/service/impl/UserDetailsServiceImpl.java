package com.tafu.bazi.service.impl;

import com.tafu.bazi.entity.User;
import com.tafu.bazi.repository.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsServiceImpl
 *
 * <p>描述: Spring Security 用户加载服务实现。
 *
 * <p>包含内容: 1. loadUserByUsername 实现
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameOrPhone) throws UsernameNotFoundException {
    // 尝试按用户名查找
    User user =
        userRepository
            .findByUsername(usernameOrPhone)
            .or(() -> userRepository.findByPhone(usernameOrPhone))
            .or(() -> userRepository.findById(usernameOrPhone))
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User not found with username, phone or id: " + usernameOrPhone));

    // 返回 Spring Security User 对象 (无权限信息)
    return new org.springframework.security.core.userdetails.User(
        user.getId(), // 使用 ID 作为这里的 username，以便在 Token 中存储 ID
        user.getPasswordHash() != null ? user.getPasswordHash() : "",
        new ArrayList<>());
  }
}
