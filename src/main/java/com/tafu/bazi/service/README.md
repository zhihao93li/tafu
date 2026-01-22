# 业务接口目录 (service)

## 目录用途
存放业务逻辑接口 (Interface)，定义系统核心功能契约。

## 内容清单
| 文件/目录 | 类型 | 用途说明 |
|:--- |:--- |:--- |
| `AuthService.java` | Interface | 认证服务 (登录/注册/验证码) |
| `BaziService.java` | Interface | 八字排盘核心计算服务 |
| `FortuneService.java` | Interface | 运势分析与 AI 生成服务 |
| `PaymentService.java` | Interface | 支付与订单服务 |
| `PointsService.java` | Interface | 积分账户与流水服务 |
| `SubjectService.java` | Interface | 测算对象管理服务 |
| `ThemeService.java` | Interface | 主题内容与解锁服务 |
| `impl/` | Dir | 接口的具体实现类 |

## 维护说明
当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
