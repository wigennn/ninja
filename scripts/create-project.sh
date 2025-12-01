#!/bin/bash

# DDD项目创建脚本
# 使用方法: ./scripts/create-project.sh <新项目名称> <新包名>
# 示例: ./scripts/create-project.sh my-project com.mycompany.myproject

set -e

if [ $# -lt 2 ]; then
    echo "使用方法: $0 <新项目名称> <新包名>"
    echo "示例: $0 my-project com.mycompany.myproject"
    exit 1
fi

NEW_PROJECT_NAME=$1
NEW_PACKAGE=$2
OLD_PACKAGE="com.example"
CURRENT_DIR=$(pwd)
PARENT_DIR=$(dirname "$CURRENT_DIR")
NEW_PROJECT_DIR="$PARENT_DIR/$NEW_PROJECT_NAME"

echo "=========================================="
echo "创建新的DDD项目"
echo "=========================================="
echo "项目名称: $NEW_PROJECT_NAME"
echo "新包名: $NEW_PACKAGE"
echo "目标目录: $NEW_PROJECT_DIR"
echo "=========================================="

# 检查目标目录是否存在
if [ -d "$NEW_PROJECT_DIR" ]; then
    echo "错误: 目录 $NEW_PROJECT_DIR 已存在"
    exit 1
fi

# 复制项目
echo "步骤1: 复制项目文件..."
cp -r "$CURRENT_DIR" "$NEW_PROJECT_DIR"
cd "$NEW_PROJECT_DIR"

# 删除.git目录（如果存在）
if [ -d ".git" ]; then
    echo "步骤2: 删除Git历史..."
    rm -rf .git
fi

# 替换包名
echo "步骤3: 替换包名 ($OLD_PACKAGE -> $NEW_PACKAGE)..."
find . -type f \( -name "*.java" -o -name "*.xml" -o -name "*.yml" -o -name "*.properties" \) \
    -not -path "*/target/*" \
    -not -path "*/.git/*" \
    -not -path "*/node_modules/*" \
    -exec sed -i '' "s/$OLD_PACKAGE/$NEW_PACKAGE/g" {} +

# 替换项目名称
echo "步骤4: 替换项目名称..."
OLD_PROJECT_NAME="ddd-scaffold"
find . -type f \( -name "*.xml" -o -name "*.yml" -o -name "*.md" \) \
    -not -path "*/target/*" \
    -not -path "*/.git/*" \
    -exec sed -i '' "s/$OLD_PROJECT_NAME/$NEW_PROJECT_NAME/g" {} +

# 替换模块名称（可选，保持ddd-前缀或使用新前缀）
echo "步骤5: 更新模块名称..."
# 这里可以选择保持ddd-前缀或使用新前缀
# 为了简化，我们保持ddd-前缀，只更新artifactId

# 更新根POM
echo "步骤6: 更新根POM..."
sed -i '' "s/<artifactId>ddd-scaffold<\/artifactId>/<artifactId>$NEW_PROJECT_NAME<\/artifactId>/g" pom.xml
sed -i '' "s/<groupId>com.example<\/groupId>/<groupId>$(echo $NEW_PACKAGE | cut -d'.' -f1-2)<\/groupId>/g" pom.xml

# 更新应用主类名称
echo "步骤7: 更新应用主类..."
MAIN_CLASS_NAME=$(echo "$NEW_PROJECT_NAME" | sed 's/-//g' | sed 's/\([a-z]\)\([A-Z]\)/\1\2/g' | sed 's/^./\U&/')
MAIN_CLASS_DIR="$NEW_PROJECT_DIR/ddd-bootstrap/src/main/java/$(echo $NEW_PACKAGE | tr '.' '/')/bootstrap"
mkdir -p "$MAIN_CLASS_DIR"
if [ -f "ddd-bootstrap/src/main/java/com/example/bootstrap/DddApplication.java" ]; then
    mv "ddd-bootstrap/src/main/java/com/example/bootstrap/DddApplication.java" \
       "$MAIN_CLASS_DIR/${MAIN_CLASS_NAME}Application.java"
    sed -i '' "s/class DddApplication/class ${MAIN_CLASS_NAME}Application/g" \
       "$MAIN_CLASS_DIR/${MAIN_CLASS_NAME}Application.java"
fi

# 移动包目录结构
echo "步骤8: 重组包目录结构..."
OLD_PACKAGE_DIR=$(echo "$OLD_PACKAGE" | tr '.' '/')
NEW_PACKAGE_DIR=$(echo "$NEW_PACKAGE" | tr '.' '/')

for module in ddd-domain ddd-application ddd-infrastructure ddd-interfaces ddd-bootstrap; do
    if [ -d "$module/src/main/java/$OLD_PACKAGE_DIR" ]; then
        mkdir -p "$module/src/main/java/$NEW_PACKAGE_DIR"
        mv "$module/src/main/java/$OLD_PACKAGE_DIR"/* "$module/src/main/java/$NEW_PACKAGE_DIR/" 2>/dev/null || true
        rm -rf "$module/src/main/java/$OLD_PACKAGE_DIR"
    fi
done

# 清理示例代码（可选，注释掉以保留示例）
# echo "步骤9: 清理示例代码..."
# rm -rf ddd-domain/src/main/java/*/domain/model/user
# rm -rf ddd-domain/src/main/java/*/domain/repository/UserRepository.java
# rm -rf ddd-domain/src/main/java/*/domain/service/UserDomainService.java

echo "=========================================="
echo "项目创建完成！"
echo "=========================================="
echo "新项目位置: $NEW_PROJECT_DIR"
echo ""
echo "下一步:"
echo "1. cd $NEW_PROJECT_DIR"
echo "2. mvn clean install"
echo "3. cd ddd-bootstrap && mvn spring-boot:run"
echo "=========================================="

