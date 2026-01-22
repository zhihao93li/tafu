package com.tafu.bazi.repository;

import com.tafu.bazi.entity.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AdminRepository
 *
 * <p>描述: 管理员数据访问接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface AdminRepository extends JpaRepository<Admin, String> {
  Optional<Admin> findByUsername(String username);
}
