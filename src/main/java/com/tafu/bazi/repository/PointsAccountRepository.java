package com.tafu.bazi.repository;

import com.tafu.bazi.entity.PointsAccount;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

/**
 * PointsAccountRepository
 *
 * <p>描述: 积分账户数据访问接口。
 *
 * <p>包含内容: 1. 根据用户ID查询账户 2. 悲观锁查询 (forUpdate)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Repository
public interface PointsAccountRepository extends JpaRepository<PointsAccount, String> {

  Optional<PointsAccount> findByUserId(String userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<PointsAccount> findByUserIdAndIdIsNotNull(String userId);
  // Trick to apply lock on userId lookup. Or verify custom query.
  // Standard approach: findByUserId with Lock annotation works.
}
