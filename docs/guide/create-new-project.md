# 使用模板创建新项目

本文档详细说明如何使用此DDD脚手架模板创建新的Maven DDD项目。

## 方法一：直接复制模板（推荐）

### 步骤1：复制项目

```bash
# 复制整个项目目录
cp -r ddd-scaffold my-new-project
cd my-new-project
```

或者使用Git：

```bash
# 克隆模板项目
git clone <template-repository-url> my-new-project
cd my-new-project
# 删除.git目录（如果不需要保留Git历史）
rm -rf .git
```

### 步骤2：修改项目基本信息

#### 2.1 修改父POM

编辑根目录的 `pom.xml`：

```xml
<groupId>com.yourcompany</groupId>  <!-- 修改为你的组织ID -->
<artifactId>your-project-name</artifactId>  <!-- 修改为你的项目名称 -->
<version>1.0.0-SNAPSHOT</version>
<name>Your Project Name</name>  <!-- 修改为你的项目名称 -->
```

#### 2.2 修改子模块POM

修改各个子模块的 `artifactId`：

- `ddd-domain` → `your-project-domain`
- `ddd-application` → `your-project-application`
- `ddd-infrastructure` → `your-project-infrastructure`
- `ddd-interfaces` → `your-project-interfaces`
- `ddd-bootstrap` → `your-project-bootstrap`

例如，编辑 `ddd-domain/pom.xml`：

```xml
<artifactId>your-project-domain</artifactId>
```

#### 2.3 修改包名

**方法A：使用IDE重构（推荐）**

1. 在IDE中打开项目
2. 右键点击 `com.example` 包
3. 选择 "Refactor" → "Rename"
4. 输入新包名，如 `com.yourcompany.yourproject`
5. 选择 "Rename package" 和 "Search in comments and strings"

**方法B：手动替换**

使用查找替换功能，将所有的 `com.example` 替换为 `com.yourcompany.yourproject`：

```bash
# 在项目根目录执行（注意备份）
find . -type f -name "*.java" -exec sed -i '' 's/com\.example/com.yourcompany.yourproject/g' {} +
find . -type f -name "*.xml" -exec sed -i '' 's/com\.example/com.yourcompany.yourproject/g' {} +
find . -type f -name "*.yml" -exec sed -i '' 's/com\.example/com.yourcompany.yourproject/g' {} +
```

#### 2.4 修改应用主类

编辑 `ddd-bootstrap/src/main/java/com/yourcompany/yourproject/bootstrap/DddApplication.java`：

```java
package com.yourcompany.yourproject.bootstrap;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.yourcompany.yourproject.domain",
    "com.yourcompany.yourproject.application",
    "com.yourcompany.yourproject.infrastructure",
    "com.yourcompany.yourproject.interfaces",
    "com.yourcompany.yourproject.bootstrap"
})
public class YourProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourProjectApplication.class, args);
    }
}
```

#### 2.5 修改应用配置

编辑 `ddd-bootstrap/src/main/resources/application.yml`：

```yaml
spring:
  application:
    name: your-project-name  # 修改为你的项目名称
```

### 步骤3：清理示例代码（可选）

如果你想从头开始，可以删除示例代码：

```bash
# 删除用户管理相关的示例代码
rm -rf ddd-domain/src/main/java/com/yourcompany/yourproject/domain/model/user
rm -rf ddd-domain/src/main/java/com/example/domain/repository/UserRepository.java
rm -rf ddd-domain/src/main/java/com/example/domain/service/UserDomainService.java
rm -rf ddd-application/src/main/java/com/example/application/dto/UserDTO.java
rm -rf ddd-application/src/main/java/com/example/application/command/*User*.java
rm -rf ddd-application/src/main/java/com/example/application/service/UserApplicationService.java
rm -rf ddd-application/src/main/java/com/example/application/mapper/UserMapper.java
rm -rf ddd-infrastructure/src/main/java/com/example/infrastructure/persistence/entity/UserEntity.java
rm -rf ddd-infrastructure/src/main/java/com/example/infrastructure/persistence/repository/User*Repository*.java
rm -rf ddd-infrastructure/src/main/java/com/example/infrastructure/persistence/mapper/UserEntityMapper.java
rm -rf ddd-interfaces/src/main/java/com/example/interfaces/rest/controller/UserController.java
rm -rf ddd-interfaces/src/main/java/com/example/interfaces/rest/dto/*User*.java
rm -rf ddd-interfaces/src/main/java/com/example/interfaces/rest/mapper/UserRestMapper.java
rm -rf ddd-application/src/main/java/com/example/application/config/DomainServiceConfig.java
```

### 步骤4：验证项目

```bash
# 清理并编译
mvn clean install

# 如果编译成功，说明项目配置正确
```

## 方法二：使用Maven Archetype（高级）

如果你想创建Maven Archetype，可以：

### 步骤1：安装Archetype

```bash
cd ddd-scaffold
mvn clean install
mvn archetype:create-from-project
```

### 步骤2：安装到本地仓库

```bash
cd target/generated-sources/archetype
mvn install
```

### 步骤3：使用Archetype创建新项目

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.example \
  -DarchetypeArtifactId=ddd-scaffold-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.yourcompany \
  -DartifactId=your-project-name \
  -Dversion=1.0.0-SNAPSHOT
```

## 创建新聚合的步骤

创建新聚合时，按照以下步骤：

### 1. 领域层 - 创建实体

在 `ddd-domain/src/main/java/com/yourcompany/yourproject/domain/model/` 下创建：

```java
package com.yourcompany.yourproject.domain.model.order;

import com.yourcompany.yourproject.domain.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order extends BaseEntity {
    private Long customerId;
    private OrderStatus status;
    // 其他字段...
    
    public static Order create(Long customerId) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.DRAFT);
        return order;
    }
}
```

### 2. 领域层 - 创建仓储接口

在 `ddd-domain/src/main/java/com/yourcompany/yourproject/domain/repository/` 下创建：

```java
package com.yourcompany.yourproject.domain.repository;

import com.yourcompany.yourproject.domain.model.order.Order;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    // 其他方法...
}
```

### 3. 应用层 - 创建DTO和命令

在 `ddd-application/src/main/java/com/yourcompany/yourproject/application/` 下创建：

- `dto/OrderDTO.java`
- `command/CreateOrderCommand.java`
- `service/OrderApplicationService.java`
- `mapper/OrderMapper.java`

### 4. 基础设施层 - 实现仓储

在 `ddd-infrastructure/src/main/java/com/yourcompany/yourproject/infrastructure/` 下创建：

- `persistence/entity/OrderEntity.java`
- `persistence/repository/OrderJpaRepository.java`
- `persistence/repository/OrderRepositoryImpl.java`
- `persistence/mapper/OrderEntityMapper.java`

### 5. 接口层 - 创建REST控制器

在 `ddd-interfaces/src/main/java/com/yourcompany/yourproject/interfaces/rest/` 下创建：

- `controller/OrderController.java`
- `dto/CreateOrderRequest.java`
- `dto/OrderResponse.java`
- `mapper/OrderRestMapper.java`

## 快速检查清单

创建新项目后，检查以下事项：

- [ ] 所有POM文件中的groupId、artifactId已更新
- [ ] 所有Java文件的包名已更新
- [ ] 应用主类的包扫描路径已更新
- [ ] application.yml中的应用名称已更新
- [ ] 项目可以成功编译（`mvn clean install`）
- [ ] 项目可以成功运行（`mvn spring-boot:run`）
- [ ] 示例代码已清理（如果不需要）

## 常见问题

### Q: 如何修改数据库？

**A**: 编辑 `ddd-bootstrap/src/main/resources/application.yml`，修改数据源配置。

### Q: 如何添加新的依赖？

**A**: 在对应模块的 `pom.xml` 中添加依赖，或在父POM的 `dependencyManagement` 中统一管理。

### Q: 如何修改端口？

**A**: 在 `application.yml` 中修改 `server.port`。

## 下一步

- 阅读[快速开始指南](./getting-started.md)了解如何运行项目
- 阅读[开发规范](./development.md)了解编码规范
- 阅读[最佳实践](./best-practices.md)学习DDD最佳实践
- 查看[用户管理示例](../examples/user-management.md)了解完整实现

