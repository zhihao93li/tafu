# 源码根目录 (com.tafu.bazi)

## 目录用途
存放 Spring Boot 应用程序的所有后端源代码。

## 内容清单
| 文件/目录 | 类型 | 用途说明 |
|:--- |:--- |:--- |
| `BaziApplication.java` | Class | Spring Boot 启动入口 |
| `config/` | Dir | 全局配置类 (Security, Swagger, OpenAI 等) |
| `controller/` | Dir | REST 控制器 API 接口层 |
| `dto/` | Dir | 数据传输对象 (Request/Response) |
| `entity/` | Dir | 数据库实体类 (JPA Entity) |
| `exception/` | Dir | 全局异常处理 |
| `repository/` | Dir | 数据库访问层 (Spring Data Repository) |
| `service/` | Dir | 业务逻辑层接口与实现 |
| `utils/` | Dir | 通用工具类 |

## 维护说明
当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
