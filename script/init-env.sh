#!/bin/bash
# 确保脚本以root权限运行
if [ "$(id -u)" != "0" ]; then
  echo "该脚本必须以root权限运行" 1>&2
  exit 1
fi
# 安装EPEL仓库（如果需要）
yum install -y epel-release
# 更新系统包索引
yum update -y
# 安装 Nginx
yum install -y nginx
# 启动 Nginx 服务
systemctl start nginx
# 设置 Nginx 开机自启
systemctl enable nginx
# 验证 Nginx 安装
if systemctl is-active --quiet nginx; then
  echo "Nginx 安装并启动成功。"
else
  echo "Nginx 安装失败。"
  exit 1
fi
# 下载并安装 JDK 17
# JDK 17 需要从Oracle官网下载，这里使用curl命令直接下载并安装
# 请替换以下URL为Oracle JDK 17的最新rpm包链接
JDK_URL="https://download.oracle.com/otn-pub/java/jdk/17/latest/jdk-17_linux-x64_bin.rpm"
# 使用 curl 下载 JDK
curl -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" $JDK_URL -o jdk-17_linux-x64_bin.rpm
# 安装 JDK
rpm -ivh jdk-17_linux-x64_bin.rpm
# 检查 JDK 是否安装成功
if [ $? -eq 0 ]; then
  echo "JDK 17 安装成功。"
else
  echo "JDK 17 安装失败。"
  exit 1
fi
# 设置 JAVA_HOME 环境变量
JAVA_HOME=$(readlink -f /etc/alternatives/java)
echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile
echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> /etc/profile
# 应用 JAVA_HOME 环境变量更改
source /etc/profile
# 验证 JDK 安装
java -version
echo "安装完成。"