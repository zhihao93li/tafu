# Bazi Fortune Telling API (tafu)

> 本项目是将原 Node.js 版 Bazi 后端迁移至 Java Spring Boot 的重构版本。

## 项目架构

本项目采用 **Spring Boot 3 + Java 21** 架构，严格遵循**分形文档架构**标准。

- **文档中心**:
    - [架构文档 (architecture.md)](file:///Users/zhihaoli/.gemini/antigravity/brain/f9068c4c-74d5-43c0-98ce-df2c8757b776/architecture.md)
    - [工程化标准 (project_standards.md)](file:///Users/zhihaoli/.gemini/antigravity/brain/f9068c4c-74d5-43c0-98ce-df2c8757b776/project_standards.md)
    - [实施计划 (implementation_plan.md)](file:///Users/zhihaoli/.gemini/antigravity/brain/f9068c4c-74d5-43c0-98ce-df2c8757b776/implementation_plan.md)

## 快速开始

### 环境依赖
- Java 21+
- Maven 3.8+
- PostgreSQL 15+

### 运行
```bash
mvn spring-boot:run
```

### 代码规范
提交代码前请运行 Spotless 进行格式化：
```bash
mvn spotless:apply
```

## 维护说明
当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
