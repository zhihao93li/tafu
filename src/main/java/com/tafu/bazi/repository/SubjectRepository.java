package com.tafu.bazi.repository;

import com.tafu.bazi.entity.Subject;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SubjectRepository
 *
 * <p>描述: 测算对象数据访问接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Repository
public interface SubjectRepository
    extends JpaRepository<Subject, String>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<Subject> {

  Page<Subject> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

  Optional<Subject> findByIdAndUserId(String id, String userId);
}
