# 快速开始 - 使用模板创建新项目

## 方法一：使用脚本（最简单）⭐

```bash
# 在模板项目根目录执行
./scripts/create-project.sh my-new-project com.mycompany.myproject
```

脚本会自动完成：
- ✅ 复制项目文件
- ✅ 替换包名
- ✅ 更新项目名称
- ✅ 重组目录结构

## 方法二：手动创建

### 步骤1：复制项目

```bash
cp -r ddd-scaffold my-new-project
cd my-new-project
```

### 步骤2：修改项目信息

#### 修改根POM (`pom.xml`)

```xml
<groupId>com.yourcompany</groupId>  <!-- 改为你的组织ID -->
<artifactId>my-new-project</artifactId>  <!-- 改为你的项目名 -->
```

#### 替换包名

使用IDE的全局替换功能：
- 查找：`com.example`
- 替换为：`com.yourcompany.yourproject`

或者在命令行执行：

```bash
# macOS/Linux
find . -type f \( -name "*.java" -o -name "*.xml" -o -name "*.yml" \) \
    -not -path "*/target/*" \
    -exec sed -i '' 's/com\.example/com.yourcompany.yourproject/g' {} +

# Windows (Git Bash)
find . -type f \( -name "*.java" -o -name "*.xml" -o -name "*.yml" \) \
    -not -path "*/target/*" \
    -exec sed -i 's/com\.example/com.yourcompany.yourproject/g' {} +
```

#### 更新应用主类

编辑 `ddd-bootstrap/src/main/java/com/yourcompany/yourproject/bootstrap/DddApplication.java`：

```java
@ComponentScan(basePackages = {
    "com.yourcompany.yourproject.domain",
    "com.yourcompany.yourproject.application",
    // ...
})
```

### 步骤3：验证

```bash
mvn clean install
```

如果编译成功，说明配置正确！

### 步骤4：运行

```bash
cd ddd-bootstrap
mvn spring-boot:run
```

## 方法三：使用Maven Archetype

```bash
# 1. 安装Archetype
cd ddd-scaffold
mvn clean install
mvn archetype:create-from-project

# 2. 安装到本地
cd target/generated-sources/archetype
mvn install

# 3. 使用Archetype创建项目
mvn archetype:generate \
  -DarchetypeGroupId=com.example \
  -DarchetypeArtifactId=ddd-scaffold-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.yourcompany \
  -DartifactId=my-new-project
```

## 清理示例代码（可选）

如果不需要用户管理示例，可以删除：

```bash
# 删除用户相关代码
find . -type f -path "*/user/*" -name "*.java" -delete
find . -type f -name "*User*.java" -delete
```

## 检查清单

创建项目后，确认：

- [ ] 所有POM中的groupId、artifactId已更新
- [ ] 所有Java文件的包名已更新  
- [ ] 应用主类的包扫描路径已更新
- [ ] `application.yml`中的应用名称已更新
- [ ] 项目可以编译成功
- [ ] 项目可以运行成功

## 详细文档

更多详细信息请查看：
- [完整使用指南](./docs/guide/create-new-project.md)
- [快速开始](./docs/guide/getting-started.md)
- [开发规范](./docs/guide/development.md)

## 常见问题

**Q: 脚本在Windows上无法运行？**  
A: 使用Git Bash或WSL，或者参考方法二手动创建。

**Q: 如何修改数据库配置？**  
A: 编辑 `ddd-bootstrap/src/main/resources/application.yml`

**Q: 如何添加新的聚合？**  
A: 参考[用户管理示例](./docs/examples/user-management.md)，按照相同的模式创建。

