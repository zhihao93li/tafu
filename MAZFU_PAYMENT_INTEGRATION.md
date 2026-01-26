# 码支付集成说明

## 概述

本项目已集成码支付（Mazfu）作为支付方式之一，支持支付宝扫码支付。参考实现来源：https://github.com/zhihao93li/bazi/tree/dev

## 功能特性

1. **支付宝扫码支付** - 通过二维码进行支付
2. **异步通知** - 支持 POST/GET 方式的支付回调
3. **同步跳转** - 支付完成后跳转回前端页面
4. **签名验证** - MD5 签名确保通信安全
5. **幂等性处理** - 防止重复支付和积分充值

## 架构设计

### 核心组件

1. **MazfuConfig** (`config/MazfuConfig.java`)
   - 读取环境变量配置
   - 验证配置完整性
   - 提供配置访问接口

2. **MazfuService** (`service/MazfuService.java`)
   - 创建支付请求
   - 验证回调签名
   - 处理支付通知

3. **PaymentController** (`controller/PaymentController.java`)
   - `/payment/create-mazfu` - 创建码支付订单
   - `/payment/mazfu-notify` - 处理异步通知（POST/GET）
   - `/payment/mazfu-return` - 处理同步跳转

### DTO 类

- `MazfuCreatePaymentRequest` - 创建支付请求参数
- `MazfuCreatePaymentResult` - 创建支付返回结果
- `MazfuNotifyParams` - 支付回调参数

## 配置说明

### 环境变量

在 `.env` 文件或环境变量中配置以下参数：

```bash
# 码支付商户 ID
MAZFU_PID=your_mazfu_pid

# 码支付签名密钥
MAZFU_KEY=your_mazfu_key

# 异步通知 URL（必须是公网可访问的 HTTPS 地址）
MAZFU_NOTIFY_URL=https://your-domain.com/api/payment/mazfu-notify

# 同步跳转 URL（支付完成后跳转地址）
MAZFU_RETURN_URL=https://your-domain.com/api/payment/mazfu-return

# 码支付 API 地址（可选，默认为官方地址）
MAZFU_API_BASE_URL=https://www.mazfu.com
```

### application.yml 配置

```yaml
mazfu:
  pid: ${MAZFU_PID:}
  key: ${MAZFU_KEY:}
  api:
    base-url: ${MAZFU_API_BASE_URL:https://www.mazfu.com}
  notify-url: ${MAZFU_NOTIFY_URL:}
  return-url: ${MAZFU_RETURN_URL:}

app:
  frontend-url: ${FRONTEND_URL:http://localhost:5173}
```

## API 接口

### 1. 创建码支付订单

**请求**
```http
POST /api/payment/create-mazfu
Authorization: Bearer <token>
Content-Type: application/json

{
  "packageId": "package_uuid",
  "device": "pc"  // 可选：pc 或 mobile，默认 pc
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "orderNo": "20260126123456ABCD1234",
    "amount": 1000,
    "points": 100,
    "qrcode": "https://mazfu.com/qrcode/xxx",  // 二维码链接
    "money": "9.50"  // 实际支付金额（元）
  }
}
```

### 2. 异步通知回调

码支付会在支付完成后向 `MAZFU_NOTIFY_URL` 发送通知。

**支持方式**
- POST：`Content-Type: application/x-www-form-urlencoded` 或 `application/json`
- GET：URL 参数

**参数**
```
pid=商户ID
trade_no=码支付订单号
out_trade_no=商户订单号
type=支付方式
name=商品名称
money=金额
trade_status=TRADE_SUCCESS
sign=签名
sign_type=MD5
```

**响应**
- 成功：返回 `success`
- 失败：返回 `fail`

### 3. 同步跳转回调

支付完成后，用户会被重定向到 `MAZFU_RETURN_URL`，系统会再次验证签名并跳转到前端结果页面。

**跳转 URL**
```
成功：{FRONTEND_URL}/payment/result?order_no={订单号}&status=success
失败：{FRONTEND_URL}/payment/result?order_no={订单号}&status=failed&error=invalid_signature
```

## 签名机制

### 签名生成算法

1. 将所有参数按键名 ASCII 升序排序
2. 排除 `sign` 和 `sign_type` 参数
3. 排除空值参数
4. 拼接为 `key1=value1&key2=value2` 格式
5. 在末尾追加商户密钥 `KEY`
6. 计算 MD5 并转小写

### 示例代码

```java
// 参数示例
Map<String, String> params = new TreeMap<>();
params.put("pid", "12345");
params.put("out_trade_no", "ORDER123");
params.put("money", "9.50");

// 拼接字符串
String queryString = "money=9.50&out_trade_no=ORDER123&pid=12345";
String signString = queryString + "your_mazfu_key";

// 计算 MD5
String sign = md5(signString).toLowerCase();
```

## 业务流程

### 支付流程

```
用户 -> 选择套餐 -> 创建订单
     -> 调用码支付 API -> 获取二维码
     -> 展示二维码给用户 -> 用户扫码支付
     -> 码支付异步通知后端 -> 验证签名 -> 更新订单状态 -> 充值积分
     -> 同步跳转前端结果页
```

### 安全措施

1. **签名验证** - 所有回调必须验证签名
2. **幂等性处理** - 检查订单状态，避免重复处理
3. **HTTPS** - 生产环境必须使用 HTTPS
4. **IP 白名单** - 建议在服务器配置码支付 IP 白名单

## 特殊说明

### 二维码优惠

代码中设置了 50 分（0.5 元）的二维码支付优惠：

```java
int qrcodeDiscount = 50; // 分
int actualPayAmount = Math.max(pkg.getPrice() - qrcodeDiscount, 1);
```

可根据实际业务需求调整此值。

### 金额单位

- 数据库存储：**分**（整数）
- 码支付 API：**元**（小数，保留 2 位）

系统自动进行单位转换：

```java
String moneyInYuan = String.format("%.2f", request.getAmount() / 100.0);
```

## 测试

### 本地测试

由于码支付需要公网 URL 接收回调，本地测试需要使用内网穿透工具（如 ngrok）：

```bash
# 启动内网穿透
ngrok http 3000

# 获得公网地址后，设置环境变量
MAZFU_NOTIFY_URL=https://your-ngrok-domain.ngrok.io/api/payment/mazfu-notify
MAZFU_RETURN_URL=https://your-ngrok-domain.ngrok.io/api/payment/mazfu-return
```

### Mock 测试

使用 Mock 接口测试支付流程（无需真实支付）：

```http
POST /api/payment/mock-callback
Content-Type: application/json

{
  "orderNo": "20260126123456ABCD1234",
  "transactionId": "MOCK_txn_123456"
}
```

## 故障排查

### 常见问题

1. **回调未收到**
   - 检查 `MAZFU_NOTIFY_URL` 是否可公网访问
   - 查看服务器日志是否有错误
   - 确认码支付后台是否配置正确的回调地址

2. **签名验证失败**
   - 确认 `MAZFU_KEY` 配置正确
   - 检查参数是否完整
   - 查看日志中的预期签名和实际签名

3. **订单状态未更新**
   - 检查数据库连接
   - 查看事务是否正常提交
   - 确认积分充值逻辑是否执行

### 日志关键字

```
[Mazfu] Requesting payment       - 发起支付请求
[Mazfu] Response:                - 码支付响应
[Mazfu] Notify signature         - 签名验证
[Mazfu] Order payment completed  - 支付完成
```

## 参考资料

- 参考实现：https://github.com/zhihao93li/bazi/tree/dev
- 码支付官方文档：请联系码支付获取
- Stripe 支付：已集成，作为国际支付方式

## 维护记录

- 2026-01-26：初始集成，参考 bazi/dev 仓库实现
