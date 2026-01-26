# 新增 API 接口文档

本文档列出了根据 GitHub dev 分支补全的所有新增接口。

## 八字排盘相关接口

### 1. 获取年份闰月信息

**接口**: `GET /bazi/leap-month/{year}`

**描述**: 获取指定农历年份的闰月信息

**路径参数**:
- `year`: 农历年份 (例如: 2024)

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "leapMonth": 2
  }
}
```

**说明**:
- `leapMonth` 为 0 表示该年无闰月
- `leapMonth` 为 1-12 表示闰几月 (例如: 2 表示闰二月)

---

### 2. 获取地点经纬度信息

**接口**: `GET /bazi/coordinates`

**描述**: 根据地点字符串获取对应的经纬度信息

**查询参数**:
- `location`: 地点字符串,格式为 "省/市/区" (例如: "北京市/北京市/朝阳区")

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "coordinates": {
      "lng": 116.4,
      "lat": 39.9
    }
  }
}
```

**说明**:
- 支持省市区三级匹配
- 如果找不到精确匹配,会使用模糊匹配
- 默认返回北京经纬度 (116.4, 35.0)

---

## 支付相关接口

### 1. 创建 Stripe Checkout 会话

**接口**: `POST /api/payment/checkout`

**描述**: 创建 Stripe Checkout 支付会话,返回支付链接

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "packageId": "套餐ID",
  "successUrl": "http://localhost:5173/payment/success",
  "cancelUrl": "http://localhost:5173/payment/cancel"
}
```

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "sessionId": "cs_test_...",
    "url": "https://checkout.stripe.com/pay/...",
    "orderNo": "订单号"
  }
}
```

---

### 2. Stripe Webhook 回调处理

**接口**: `POST /api/payment/webhook`

**描述**: 处理 Stripe 支付成功的 Webhook 回调

**请求头**:
```
Stripe-Signature: {signature}
```

**请求体**: Stripe Event JSON Payload (Raw Body)

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功"
}
```

---

### 3. 查询订单状态

**接口**: `GET /api/payment/status/{sessionId}`

**描述**: 根据 Stripe Session ID 查询订单支付状态

**路径参数**:
- `sessionId`: Stripe Checkout Session ID

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": "订单ID",
    "orderNo": "订单号",
    "status": "pending/paid/failed",
    "amount": 1000,
    "points": 100,
    "stripeSessionId": "cs_test_...",
    "createdAt": "2026-01-26T10:00:00",
    "paidAt": "2026-01-26T10:05:00"
  }
}
```

---

## 运势分析接口

### 4. 流式初步分析

**接口**: `POST /api/fortune/analyze-stream`

**描述**: 实时流式输出初步八字分析结果 (Server-Sent Events)

**请求头**:
```
Authorization: Bearer {token}
Accept: text/event-stream
```

**请求体**:
```json
{
  "subjectId": "测算对象ID"
}
```

**响应**: Server-Sent Events 流

```
data: 您的
data: 八字
data: 为
data: 甲子年
...
```

**前端使用示例**:
```javascript
const response = await fetch('/api/fortune/analyze-stream', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ subjectId: 'xxx' })
});

const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
  const { done, value } = await reader.read();
  if (done) break;
  const chunk = decoder.decode(value);
  console.log(chunk);
}
```

---

## 管理后台接口

### 5. 更新用户信息

**接口**: `PUT /api/admin/users/{id}`

**描述**: 管理员更新用户信息

**路径参数**:
- `id`: 用户ID

**请求体**:
```json
{
  "username": "新用户名",
  "phone": "新手机号"
}
```

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": "用户ID",
    "username": "新用户名",
    "phone": "新手机号",
    "balance": 100,
    "createdAt": "2026-01-20T10:00:00",
    "updatedAt": "2026-01-26T10:00:00"
  }
}
```

---

### 6. 删除用户

**接口**: `DELETE /api/admin/users/{id}`

**描述**: 管理员删除用户

**路径参数**:
- `id`: 用户ID

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功"
}
```

---

### 7. 更新支付订单

**接口**: `PUT /api/admin/payment-orders/{id}`

**描述**: 管理员更新订单状态

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "status": "paid",
  "transactionId": "txn_xxx"
}
```

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": "订单ID",
    "orderNo": "订单号",
    "status": "paid",
    "transactionId": "txn_xxx",
    ...
  }
}
```

---

### 8. 删除支付订单

**接口**: `DELETE /api/admin/payment-orders/{id}`

**描述**: 管理员删除订单

**路径参数**:
- `id`: 订单ID

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功"
}
```

---

### 9. 更新命盘信息

**接口**: `PUT /api/admin/subjects/{id}`

**描述**: 管理员更新命盘信息

**路径参数**:
- `id`: 命盘ID

**请求体**:
```json
{
  "name": "新名称",
  "note": "备注",
  "relationship": "关系"
}
```

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": "命盘ID",
    "name": "新名称",
    ...
  }
}
```

---

### 10. 删除命盘

**接口**: `DELETE /api/admin/subjects/{id}`

**描述**: 管理员删除命盘

**路径参数**:
- `id`: 命盘ID

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功"
}
```

---

### 11. 获取主题分析详情

**接口**: `GET /api/admin/theme-analyses/{id}`

**描述**: 管理员查看主题分析详情

**路径参数**:
- `id`: 主题分析ID

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": "分析ID",
    "userId": "用户ID",
    "subjectId": "命盘ID",
    "theme": "career",
    "content": {...},
    "createdAt": "2026-01-26T10:00:00"
  }
}
```

---

### 12. 删除主题分析

**接口**: `DELETE /api/admin/theme-analyses/{id}`

**描述**: 管理员删除主题分析记录

**路径参数**:
- `id`: 主题分析ID

**响应**:
```json
{
  "success": true,
  "code": 200,
  "message": "操作成功"
}
```

---

## 环境变量配置

新增接口需要以下环境变量:

```bash
# Stripe 配置
STRIPE_API_KEY=sk_test_your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
```

### Stripe 测试说明

1. **本地测试 Webhook**:
   - 安装 Stripe CLI: `brew install stripe/stripe-cli/stripe`
   - 登录: `stripe login`
   - 转发事件到本地: `stripe listen --forward-to localhost:3000/api/payment/webhook`
   - 复制显示的 webhook secret 到环境变量

2. **Mock 支付测试**:
   - 仍可使用 `POST /api/payment/mock-callback` 进行无需 Stripe 的集成测试

---

## 接口统计

| 模块 | 新增接口数 |
|------|-----------|
| 支付模块 | 3 个 |
| 运势分析 | 1 个 |
| 管理后台 | 8 个 |
| **总计** | **12 个** |

---

## 注意事项

1. **Stripe 签名验证**: Webhook 接口会验证 Stripe 签名,确保请求来自 Stripe
2. **流式接口**: `/analyze-stream` 返回 SSE 流,前端需使用 EventSource 或 Fetch API 接收
3. **幂等性**: 支付成功回调会检查订单状态,避免重复充值
4. **管理权限**: 所有 `/admin/*` 接口需要管理员权限 (需在 SecurityConfig 中配置)
5. **事务保证**: 支付成功和积分充值在同一事务中,保证数据一致性
