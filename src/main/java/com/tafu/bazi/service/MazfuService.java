package com.tafu.bazi.service;

import com.tafu.bazi.dto.MazfuCreatePaymentRequest;
import com.tafu.bazi.dto.MazfuCreatePaymentResult;
import com.tafu.bazi.dto.MazfuNotifyParams;

/**
 * MazfuService Interface
 *
 * <p>描述: 码支付业务逻辑接口。
 *
 * <p>包含内容: 1. 创建支付请求 2. 验证签名 3. 处理支付回调
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
public interface MazfuService {

  /**
   * 创建码支付请求
   *
   * @param request 支付请求参数
   * @return 支付结果，包含二维码或跳转链接
   */
  MazfuCreatePaymentResult createPayment(MazfuCreatePaymentRequest request);

  /**
   * 验证码支付回调签名
   *
   * @param params 回调参数
   * @return 签名是否有效
   */
  boolean verifySign(MazfuNotifyParams params);

  /**
   * 处理码支付异步通知
   *
   * @param params 回调参数
   * @return 处理结果: "success" 或 "fail"
   */
  String handleNotify(MazfuNotifyParams params);
}
