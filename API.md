# API 接口文档

## 基础信息

- 基础路径: `/api`
- 响应格式: `application/json`
- 鉴权方式: `Authorization: Bearer <JWT>`
- 免鉴权路径: `/api/auth/**`

## 统一响应结构

所有接口返回 `ApiResponse` 结构:

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

失败示例:

```json
{
  "success": false,
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

## 错误码

- 200: 操作成功
- 400: 参数错误
- 401: 请先登录
- 403: 无权访问
- 404: 资源不存在
- 500: 系统繁忙，请稍后重试
- 20001: 用户名或密码错误
- 20002: 手机号格式错误
- 20003: 验证码错误或已失效
- 30001: 余额不足

## 认证模块

### 手机号登录

- `POST /auth/login/phone`
- 请求体:
  - phone: string
  - code: string

响应 `data`:

```json
{
  "token": "jwt-token",
  "user": {
    "id": "user-id",
    "username": "username",
    "phone": "13800000000",
    "isNewUser": true
  }
}
```

### 密码登录

- `POST /auth/login/password`
- 请求体:
  - username: string
  - password: string

响应结构同上。

### 注册

- `POST /auth/register`
- 请求体:
  - username: string
  - password: string

响应结构同上。

### 发送验证码

- `POST /auth/send-code`
- 请求体:
  - phone: string

响应 `data`: `null`

## 八字排盘

### 八字计算

- `POST /bazi/calculate`
- 请求体:
  - year: number
  - month: number
  - day: number
  - hour: number
  - minute: number
  - calendarType: string (`solar` | `lunar`)
  - gender: string (`male` | `female`)
  - isLeapMonth: boolean
  - location: string

响应 `data`: `Map<String, Object>` 排盘结果

## 测算对象

### 列表

- `GET /subjects`
- Query:
  - page: number
  - size: number
  - sort: string

响应 `data`:
`Page<SubjectResponse>`

`SubjectResponse` 字段:
- id, name, gender, calendarType
- birthYear, birthMonth, birthDay, birthHour, birthMinute
- isLeapMonth, location, relationship, note
- createdAt, baziBrief

### 详情

- `GET /subjects/{id}`

响应 `data`: `SubjectResponse`

### 创建

- `POST /subjects`
- 请求体:
  - name: string
  - gender: string
  - calendarType: string
  - birthYear: number
  - birthMonth: number
  - birthDay: number
  - birthHour: number
  - birthMinute: number
  - isLeapMonth: boolean
  - location: string
  - relationship: string
  - note: string

响应 `data`: `SubjectResponse`

### 更新

- `PUT /subjects/{id}`
- 请求体同创建

响应 `data`: `SubjectResponse`

### 删除

- `DELETE /subjects/{id}`

响应 `data`: `null`

### 关联报告

- `GET /subjects/{id}/reports`

响应 `data`: `List<FortuneReport>`

## 运势分析

### 初步分析

- `POST /fortune/analyze`
- 请求体:
  - subjectId: string

响应 `data`: `Map<String, Object>`

## 主题内容

### 解锁主题

- `POST /themes/unlock`
- 请求体:
  - subjectId: string
  - theme: string

响应 `data`: string

### 获取主题内容

- `GET /themes/{subjectId}/{theme}`

响应 `data`: `Map<String, Object>`

## 报告记录

### 报告列表

- `GET /reports`

响应 `data`: `List<FortuneReport>`

### 报告详情

- `GET /reports/{id}`

响应 `data`: `FortuneReport`

### 删除报告

- `DELETE /reports/{id}`

响应 `data`: `null`

`FortuneReport` 字段:
- id, userId, subjectId
- birthInfo, baziChart, analysis
- pointsCost, createdAt, deletedAt

## 积分

### 我的积分

- `GET /points`

响应 `data`:

```json
{
  "balance": 0,
  "transactions": [],
  "total": 0
}
```

### 积分套餐

- `GET /points/packages`

响应 `data`: `List<PointsPackage>`

`PointsPackage` 字段:
- id, name, points, price, isActive, sortOrder

## 支付

### 套餐列表

- `GET /payment/packages`

响应 `data`: `List<PointsPackage>`

### 创建订单

- `POST /payment/create`
- 请求体:
  - packageId: string
  - paymentMethod: string (默认 `manual`)

响应 `data`: `PaymentOrder`

### 模拟回调

- `POST /payment/mock-callback`
- 请求体:
  - orderNo: string

响应 `data`: `null`

`PaymentOrder` 字段:
- id, userId, orderNo
- amount, points, paymentMethod, status
- transactionId, stripeSessionId
- createdAt, paidAt

## 异步任务

### 查询任务

- `GET /tasks/{id}`

响应 `data`:

```json
{
  "taskId": "task-id",
  "status": "pending",
  "content": "string",
  "error": "string"
}
```

## 管理后台

### 登录

- `POST /admin/login`
- 请求体:
  - phone: string
  - code: string

响应 `data`:

```json
{
  "token": "jwt-token"
}
```

### 控制台统计

- `GET /admin/dashboard`

响应 `data`: `Map<String, Object>`

### 用户列表

- `GET /admin/users`
- Query:
  - page: number
  - limit: number

响应 `data`: `Page<AdminUserResponse>`

### 用户详情

- `GET /admin/users/{id}`

响应 `data`: `AdminUserResponse`

`AdminUserResponse` 字段:
- id, phone, username, balance, createdAt, updatedAt

### 任务统计

- `GET /admin/tasks/stats`

响应 `data`: `Map<String, Object>`

### 任务列表

- `GET /admin/tasks`
- Query:
  - page: number
  - limit: number
  - status: string
  - type: string
  - userId: string

响应 `data`: `Page<Task>`

### 任务详情

- `GET /admin/tasks/{id}`

响应 `data`: `Task`

### 重试任务

- `POST /admin/tasks/{id}/retry`

响应 `data`: `Task`

### 取消任务

- `POST /admin/tasks/{id}/cancel`

响应 `data`: `Task`

### 重试所有失败任务

- `POST /admin/tasks/retry-all-failed`

响应 `data`:

```json
{
  "message": "已重试 0 个失败任务",
  "count": 0
}
```

### 清理过期任务

- `POST /admin/tasks/cleanup`
- 请求体:
  - days: number

响应 `data`:

```json
{
  "message": "已清理 0 个过期任务",
  "count": 0
}
```

### 支付订单列表

- `GET /admin/payment-orders`
- Query:
  - page: number
  - limit: number
  - status: string
  - paymentMethod: string
  - userId: string
  - orderNo: string

响应 `data`: `Page<PaymentOrder>`

### 支付订单详情

- `GET /admin/payment-orders/{id}`

响应 `data`: `PaymentOrder`

### 积分流水

- `GET /admin/points-transactions`
- Query:
  - page: number
  - limit: number
  - userId: string
  - type: string

响应 `data`: `Page<PointsTransaction>`

### 命盘列表

- `GET /admin/subjects`
- Query:
  - page: number
  - limit: number
  - userId: string
  - search: string

响应 `data`: `Page<Subject>`

### 命盘详情

- `GET /admin/subjects/{id}`

响应 `data`: `Subject`

### 主题分析记录

- `GET /admin/theme-analyses`
- Query:
  - page: number
  - limit: number
  - userId: string
  - theme: string

响应 `data`: `Page<ThemeAnalysis>`

## 附录: 关键实体字段

`Task` 字段:
- id, userId, type, status
- payload, result, error
- createdAt, startedAt, completedAt

`ThemeAnalysis` 字段:
- id, userId, subjectId, theme
- content, pointsCost, createdAt
