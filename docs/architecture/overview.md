# 架构概览

## 分层架构

本项目采用经典的四层DDD架构，每层职责清晰，依赖关系明确。

```
┌─────────────────────────────────────┐
│         Interfaces Layer            │  ← 接口层（REST API、DTO）
├─────────────────────────────────────┤
│       Application Layer             │  ← 应用层（应用服务、用例编排）
├─────────────────────────────────────┤
│         Domain Layer                │  ← 领域层（实体、值对象、领域服务）
├─────────────────────────────────────┤
│      Infrastructure Layer           │  ← 基础设施层（持久化、外部服务）
└─────────────────────────────────────┘
```

## 依赖关系

- **接口层** → 应用层
- **应用层** → 领域层
- **基础设施层** → 领域层 + 应用层
- **领域层** → 无依赖（核心层）

## 各层职责

### 领域层（Domain Layer）

领域层是系统的核心，包含：

- **实体（Entity）**：具有唯一标识的对象
- **值对象（Value Object）**：通过值相等性比较的对象
- **领域服务（Domain Service）**：跨实体的业务逻辑
- **仓储接口（Repository Interface）**：数据访问抽象

**特点**：
- 不依赖任何外部框架
- 包含核心业务逻辑
- 定义仓储接口，不包含实现

### 应用层（Application Layer）

应用层协调领域对象完成用例：

- **应用服务（Application Service）**：编排领域对象完成业务用例
- **DTO（Data Transfer Object）**：数据传输对象
- **命令（Command）**：写操作命令
- **查询（Query）**：读操作查询

**特点**：
- 薄层，不包含业务逻辑
- 负责事务管理
- 协调领域对象和基础设施

### 基础设施层（Infrastructure Layer）

基础设施层提供技术实现：

- **仓储实现（Repository Implementation）**：实现领域层的仓储接口
- **持久化实体（Persistence Entity）**：JPA实体
- **外部服务适配器（External Service Adapter）**：第三方服务集成
- **配置（Configuration）**：技术配置

**特点**：
- 实现技术细节
- 可替换的实现
- 依赖外部框架

### 接口层（Interfaces Layer）

接口层提供外部访问入口：

- **REST控制器（REST Controller）**：HTTP API端点
- **请求/响应DTO**：API数据传输对象
- **异常处理（Exception Handler）**：统一异常处理
- **参数验证（Validation）**：输入验证

**特点**：
- 处理HTTP请求
- 参数验证和转换
- 统一异常处理

## 数据流向

```
HTTP Request
    ↓
Interfaces Layer (Controller)
    ↓
Application Layer (Application Service)
    ↓
Domain Layer (Entity/Domain Service)
    ↓
Infrastructure Layer (Repository Implementation)
    ↓
Database
```

## 设计原则

1. **依赖倒置**：高层模块不依赖低层模块，都依赖抽象
2. **单一职责**：每层、每个类职责单一
3. **开闭原则**：对扩展开放，对修改关闭
4. **领域驱动**：以领域模型为核心，技术为领域服务

## 模块划分

项目采用Maven多模块结构：

- `ddd-domain`：领域层模块
- `ddd-application`：应用层模块
- `ddd-infrastructure`：基础设施层模块
- `ddd-interfaces`：接口层模块
- `ddd-bootstrap`：启动模块（包含Spring Boot配置）

## 优势

1. **清晰的边界**：每层职责明确，易于理解
2. **易于测试**：领域层可独立测试
3. **易于维护**：修改技术实现不影响领域层
4. **易于扩展**：新功能按层添加，结构清晰

