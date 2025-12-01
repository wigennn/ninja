#!/bin/bash

# GitBook文档本地服务器脚本
# 使用方法: ./scripts/serve-docs.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
DOCS_DIR="$PROJECT_ROOT/docs"

cd "$DOCS_DIR"

echo "=========================================="
echo "启动GitBook文档服务器"
echo "=========================================="
echo "文档目录: $DOCS_DIR"
echo ""

# 检查HonKit
if command -v honkit &> /dev/null; then
    echo "使用HonKit启动服务器..."
    echo "访问地址: http://localhost:4000"
    echo ""
    honkit serve
    exit 0
fi

# 检查GitBook CLI
if command -v gitbook &> /dev/null; then
    echo "使用GitBook CLI启动服务器..."
    echo "访问地址: http://localhost:4000"
    echo ""
    gitbook serve
    exit 0
fi

# 如果都没有安装，提供安装指南
echo "错误: 未找到HonKit或GitBook CLI"
echo ""
echo "请安装其中一个工具:"
echo ""
echo "1. 安装HonKit (推荐):"
echo "   npm install -g honkit"
echo ""
echo "2. 或安装GitBook CLI:"
echo "   npm install -g gitbook-cli"
echo "   gitbook install"
echo ""
echo "安装后重新运行此脚本即可。"
echo "=========================================="
exit 1

