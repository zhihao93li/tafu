# DTO 目录 (dto)

## 目录用途
存放数据传输对象 (Data Transfer Object)，用于 API 的请求参数 (request) 和响应数据 (response) 封装。

## 内容清单
| 文件/目录 | 类型 | 用途说明 |
|:--- |:--- |:--- |
| `request/AuthRequest.java` | Class | 认证相关请求参数 |
| `request/BaziCalculateRequest.java` | Class | 八字排盘计算请求参数 |
| `request/SubjectRequest.java` | Class | 测算对象创建/更新请求参数 |
| `response/ApiResponse.java` | Class | 统一 API 响应包装 |
| `response/AuthResponse.java` | Class | 认证成功响应数据 |
| `response/PointsResponse.java` | Class | 积分与流水响应数据 |
| `response/SubjectResponse.java` | Class | 测算对象详情响应数据 |

## 维护说明
当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
