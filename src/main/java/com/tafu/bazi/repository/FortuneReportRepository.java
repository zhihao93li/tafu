package com.tafu.bazi.repository;

import com.tafu.bazi.entity.FortuneReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * FortuneReportRepository
 *
 * <p>描述: 命理报告数据访问接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface FortuneReportRepository extends JpaRepository<FortuneReport, String> {
  List<FortuneReport> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(String userId);

  List<FortuneReport> findByUserIdAndSubjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(
      String userId, String subjectId);
}
