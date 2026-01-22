package com.tafu.bazi.repository;

import com.tafu.bazi.entity.PointsPackage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PointsPackageRepository
 *
 * <p>描述: 积分套餐数据访问接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Repository
public interface PointsPackageRepository extends JpaRepository<PointsPackage, String> {

  List<PointsPackage> findByIsActiveTrueOrderBySortOrderAsc();
}
