package com.tafu.bazi.service.impl;

import com.tafu.bazi.dto.response.PointsResponse;
import com.tafu.bazi.entity.PointsAccount;
import com.tafu.bazi.entity.PointsTransaction;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.PointsAccountRepository;
import com.tafu.bazi.repository.PointsTransactionRepository;
import com.tafu.bazi.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PointsServiceImpl
 *
 * <p>描述: 积分业务逻辑实现。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PointsServiceImpl implements PointsService {

  private final PointsAccountRepository accountRepository;
  private final PointsTransactionRepository transactionRepository;

  @Override
  public PointsResponse getMyPoints(String userId) {
    ensureAccountExists(userId);
    PointsAccount account = accountRepository.findByUserId(userId).orElseThrow();

    Page<PointsTransaction> transactions =
        transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 50));

    return PointsResponse.builder()
        .balance(account.getBalance())
        .transactions(transactions.getContent())
        .total((int) transactions.getTotalElements())
        .build();
  }

  @Override
  @Transactional
  public void addPoints(String userId, int amount, String type, String description) {
    PointsAccount account = getAccountWithLock(userId);

    int oldBalance = account.getBalance();
    int newBalance = oldBalance + amount;

    account.setBalance(newBalance);
    accountRepository.save(account);

    createTransaction(userId, type, amount, newBalance, description);
  }

  @Override
  @Transactional
  public void deductPoints(String userId, int amount, String type, String description) {
    PointsAccount account = getAccountWithLock(userId);

    int oldBalance = account.getBalance();
    if (oldBalance < amount) {
      throw new BusinessException(StandardErrorCode.BALANCE_NOT_ENOUGH);
    }

    int newBalance = oldBalance - amount;
    account.setBalance(newBalance);
    accountRepository.save(account);

    createTransaction(userId, type, -amount, newBalance, description);
  }

  @Override
  public void ensureAccountExists(String userId) {
    if (accountRepository.findByUserId(userId).isEmpty()) {
      PointsAccount account =
          PointsAccount.builder()
              .userId(userId)
              .balance(0)
              .createdAt(java.time.LocalDateTime.now())
              .updatedAt(java.time.LocalDateTime.now())
              .build();
      accountRepository.save(account);
    }
  }

  private PointsAccount getAccountWithLock(String userId) {
    ensureAccountExists(userId);
    return accountRepository
        .findByUserIdAndIdIsNotNull(userId)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.SYSTEM_ERROR));
  }

  private void createTransaction(
      String userId, String type, int amount, int balance, String description) {
    PointsTransaction transaction =
        PointsTransaction.builder()
            .userId(userId)
            .type(type)
            .amount(amount)
            .balance(balance)
            .description(description)
            .createdAt(java.time.LocalDateTime.now())
            .build();
    transactionRepository.save(transaction);
  }
}
