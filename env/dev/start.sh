#!/bin/bash
source /diy/saas/acre/dev/app/.env

#作者
GITLAB_USER_NAME=$1
#邮箱
CI_COMMIT_AUTHOR=$2
#project name
PROJECT_NAME=$3
#branch or tag name
CI_COMMIT_REF_NAME=$4
#commit id
CI_COMMIT_SHA=$5
#commit message
CI_COMMIT_MESSAGE=$6
#变更文件
CHANGED_FILES=$7
#状态 成功或失败
STATE=$8

if ! docker network ls | grep -q "$DOCKER_NETWORK"; then
    docker network create "$DOCKER_NETWORK"
fi
# todo 安装环境依赖paas，获取paas目录下的docker-compose.yml文件，并替换其中的环境变量，然后执行docker-compose up -d(先检测镜像是否存在，如果存在则跳过)
docker rm -f $CONTAINER_NAME
# 执行docker-compose文件
docker compose -f $DOCKER_COMPOSE_FILE_DIR/docker-compose.yml down
# 检查镜像是否存在
image_exists=$(docker images -q $REGISTRY_URL/$DOCKER_IMAGE:$TAG)
# 如果镜像存在，则删除
if [ -n "$image_exists" ]; then
    docker rmi -f $image_exists
    echo "已删除镜像：$REGISTRY_URL/$DOCKER_IMAGE:$TAG"
else
    echo "镜像不存在：$REGISTRY_URL/$DOCKER_IMAGE:$TAG"
fi
#删除dangling镜像
docker rmi $(docker images --filter "reference=*/$DOCKER_IMAGE" --filter "dangling=true" -q)
# 启动容器
docker compose -f $DOCKER_COMPOSE_FILE_DIR/docker-compose.yml up -d
#todo 触发成功通知


title='应用发布'
time="$(date "+%Y-%m-%d")"
times="$(date "+%H:%M:%S")"
xingqi="$(date "+%A")"
public_ip=$(curl -s https://api.ipify.org)
#runner 中需要apt-get install -y ifconfig
ip=$(ifconfig | grep inet | awk 'NR==3{print $2}')
lsblk=$(df -h / | awk '{print $4"/"$2 , $5}' | tail -n 1 )
mem_info=$(free -h) # 运行 free -h 命令并将结果保存到变量 mem_info 中
total_memory=$(echo "$mem_info" | grep "Mem:" | awk '{print $2}')
mem=$(free | grep Mem | awk '{print $3/$2 * 100.0}')
cpu=$(top -b -n1 | grep "Cpu(s)" | awk '{print $2}')

#feishu
curl -X POST -H "Content-Type: application/json" \
        -d '{
          "msg_type":"post",
          "content": {
            "post": {
              "zh_cn": {
                "title": "'"$CONTAINER_NAME/中冶焦耐"'-发布",
                "content": [
                  [
                    {"tag": "text", "text": "'"时间：$time $times $xingqi\n"'"},
                    {"tag": "text", "text": "'"HOST:$public_ip:$ip\n"'"},
                    {"tag": "text", "text": "'"DISK:$lsblk\n"'"},
                    {"tag": "text", "text": "'"MEM:$total_memory,$mem%\n"'"},
                    {"tag": "text", "text": "'"CPU:$cpu%\n"'"},
                    {"tag": "text", "text": "'"接口文档: "'"},
                    {"tag": "a", "href": "'"$API_FOX_HENGLI"'", "text": "'"$API_FOX_HENGLI\n"'"},
                    {"tag": "text", "text": "'"项目地址: "'"},
                    {"tag": "a", "href": "'"$APP_HOST:$EHP"'/doc", "text": "'"$APP_HOST:$EHP"'"}
                  ]
                ]
              }
            }
          }
        }' "${WEBHOOK_FEI_SHU}"