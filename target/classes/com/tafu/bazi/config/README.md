# 配置目录 (config)

## 目录用途
存放 Spring Boot 应用程序的所有全局配置类。

## 内容清单
| 文件/目录 | 类型 | 用途说明 |
|:--- |:--- |:--- |
| `AppConfig.java` | Class | 基础应用配置 (如 RestTemplate Bean) |
| `SecurityConfig.java` | Class | Spring Security 安全配置 (JWT, CORS, BCrypt) |
| `SwaggerConfig.java` | Class | OpenAPI/Swagger 文档配置 |
| `WebMvcConfig.java` | Class | Web MVC 配置 (如拦截器) |
| `AiConfig.java` | Class | Spring AI 基础配置 |
| `AiPromptsConfig.java` | Class | AI 提示词模板配置 (映射 ai-prompts.yaml) |
| `SecurityConfig.java` | Class | Spring Security 安全配置 |

## 维护说明
当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
