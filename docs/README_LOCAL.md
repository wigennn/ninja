# 本地查看GitBook文档

本文档介绍如何在本地查看GitBook文档的几种方法。

## 方法一：使用HonKit（推荐）⭐

HonKit是GitBook的现代替代品，完全兼容GitBook格式。

### 安装HonKit

```bash
# 使用npm安装
npm install -g honkit

# 或使用yarn
yarn global add honkit
```

### 构建和预览

```bash
# 进入docs目录
cd docs

# 安装插件（首次运行）
honkit install

# 启动本地服务器（默认端口4000）
honkit serve
```

访问：http://localhost:4000

### 常见问题解决

**问题：Maximum call stack size exceeded**

这是search插件的已知问题，已在`book.json`中移除了search插件。如果仍有问题：

1. 删除`node_modules`和`_book`目录：
```bash
cd docs
rm -rf node_modules _book
```

2. 重新安装：
```bash
honkit install
honkit serve
```

### 常用命令

```bash
# 构建静态网站
honkit build

# 启动开发服务器（支持热重载）
honkit serve

# 构建并输出到指定目录
honkit build ./_book ./output

# 安装插件
honkit install
```

## 方法二：使用GitBook CLI（旧版，不推荐）

GitBook CLI已不再维护，但如果你需要：

```bash
# 安装（需要Node.js）
npm install -g gitbook-cli

# 安装GitBook
gitbook install

# 构建
cd docs
gitbook build

# 启动服务器
gitbook serve
```

访问：http://localhost:4000

## 方法三：使用VS Code插件

### Markdown Preview Enhanced

1. 在VS Code中安装插件：`Markdown Preview Enhanced`
2. 打开 `docs/README.md`
3. 右键选择 `Markdown Preview Enhanced: Open Preview to the Side`
4. 可以查看单个文件，但无法查看完整目录结构

### Markdown All in One

1. 安装插件：`Markdown All in One`
2. 打开任意Markdown文件
3. 使用 `Ctrl+Shift+V` (Windows/Linux) 或 `Cmd+Shift+V` (Mac) 预览

## 方法四：使用mdbook（Rust工具）

mdbook是Rust编写的类似工具，性能优秀。

### 安装

```bash
# macOS
brew install mdbook

# 或使用cargo
cargo install mdbook
```

### 配置

创建 `docs/book.toml`：

```toml
[book]
title = "DDD领域驱动设计脚手架文档"
description = "完整的DDD架构实现指南"
authors = ["DDD Scaffold Team"]
language = "zh-CN"

[build]
build-dir = "_book"
```

### 使用

```bash
cd docs
mdbook serve
```

访问：http://localhost:3000

## 方法五：使用简单的HTTP服务器

如果只需要查看Markdown文件，可以使用简单的HTTP服务器：

### Python HTTP服务器

```bash
cd docs
python3 -m http.server 8000
```

访问：http://localhost:8000

### Node.js http-server

```bash
# 安装
npm install -g http-server

# 启动
cd docs
http-server -p 8000
```

## 方法六：使用Docker（推荐用于CI/CD）

### 使用HonKit Docker镜像

```bash
# 构建
docker run --rm -v "$PWD/docs:/srv/gitbook" -p 4000:4000 fellah/gitbook gitbook serve

# 或使用honkit镜像
docker run --rm -v "$PWD/docs:/srv/gitbook" -p 4000:4000 fellah/gitbook honkit serve
```

## 推荐工作流程

### 开发文档时

```bash
# 使用HonKit，支持热重载
cd docs
honkit install  # 首次运行
honkit serve
```

### 构建生产版本

```bash
cd docs
honkit build
# 生成的静态文件在 _book 目录
```

### 部署到GitHub Pages

```bash
cd docs
honkit build
# 将 _book 目录的内容推送到 gh-pages 分支
```

## 故障排除

### 问题1：Maximum call stack size exceeded

**原因**：search插件与某些内容冲突

**解决**：
1. 已从`book.json`中移除search插件
2. 如果仍有问题，删除缓存：
```bash
cd docs
rm -rf node_modules _book .honkit
honkit install
honkit serve
```

### 问题2：插件安装失败

**解决**：
```bash
cd docs
rm -rf node_modules
honkit install
```

### 问题3：端口被占用

**解决**：指定其他端口
```bash
honkit serve --port 4001
```

## 快速开始脚本

使用提供的脚本：

```bash
./scripts/serve-docs.sh
```

## 常见问题

### Q: HonKit和GitBook有什么区别？

A: HonKit是GitBook的开源fork，完全兼容GitBook 3.x格式，但持续维护和更新。

### Q: 如何添加插件？

A: 在 `book.json` 中添加插件，然后运行 `honkit install`

### Q: 如何自定义主题？

A: 修改 `book.json` 中的 `pluginsConfig` 部分

### Q: 构建失败怎么办？

A: 
1. 检查Node.js版本（建议14+）
2. 删除 `node_modules` 和 `_book` 目录
3. 重新运行 `honkit install` 和 `honkit build`
