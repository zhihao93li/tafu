package com.tafu.bazi.repository;

import com.tafu.bazi.entity.PaymentOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PaymentOrderRepository
 *
 * <p>描述: 支付订单数据访问接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Repository
public interface PaymentOrderRepository
    extends JpaRepository<PaymentOrder, String>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<PaymentOrder> {

  Optional<PaymentOrder> findByOrderNo(String orderNo);
}
