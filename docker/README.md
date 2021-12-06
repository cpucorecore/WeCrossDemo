# docker 安装跨链路由
宿主机需要安装docker，docker-compose
本文档所采用的版本号：
- docker: 20.10.10
- docker-compose: 1.29.2

## 机器配置
- 跨链路由有1个Account Manager进程和多个router进程构成，router的数量由跨链的目标数决定，需要跨几条链就需要启动几个router
- Account Manager占用内存600M左右，每个router占用内存1G左右；cpu核心数由路由个数决定，2个路由2核，4个路由4核，依此类推；

## 收集跨链配置信息
- Account Manager依赖mysql server才能启动；
- 1个跨链分区由1个Account Manager和多个router构成，不同的跨链分区不能共用同一个mysql server数据库
- 多个跨链分区共用一个mysql server时可以为不同的跨链分区分配不同的数据库
- 默认使用wecross_account_manager数据库，可以在Account Manager的配置项里修改，配置项位于Account Manager安装目录下conf/application.toml文件中的db.url

router依赖Account Manager和目标链的ip，port，chainID，groupID，sdk cert等信息才能启动，在启动前必须先部署系统合约
- mysql server的ip和port
- 跨链路由要链接的各目标链的ip和port，sdk证书，chainID，groupID

## 创建路由
1. 在routers目录下创建ipfile文件：
每个路由对应一行，每行分别为目标链ip地址:router的rpc端口:router的p2p端口，其中ip地址也可以全部用127.0.0.1，但是要自己维护每个目录所链接的区块链信息
```
127.0.0.1:8250:25500
127.0.0.1:8251:25501
```

2. 配置安装环境变量：
修改配置文件routers/env.sh
```sh
# 跨链分区名字
export ROUTERS_ZONE_NAME=payment

# db配置
export MYSQL_IP=docker.for.mac.host.internal
export MYSQL_PORT=3306
export MYSQL_USER=root
export MYSQL_PASSWORD=MyNewPass5ABC
```

## 启动路由容器
3. 启动docker compose启动
```sh
docker-compose up -d
```

# 配置路由
进入docker容器:
```sh
# 查看容器id
docker ps -a
# 进入容器
docker exec -it 容器id /bin/bash
```

- 添加目标链
- 配置目标链证书
- 配置目标链stub

# 启动路由
进入docker容器:
```sh
# 查看容器id
docker ps -a
# 进入容器
docker exec -it 容器id /bin/bash
```

cd ~ && bash start_all.sh

确认路由启动成功：
```sh
ps -ef|grep java
```


# 提交镜像
1. 在容器中修改启动脚本：~/start.sh
```shell
start_all.sh
tail -f /dev/null
```

2. 退出容器

3. 停止容器
docker-compose stop

4. 提交容器
docker commit 容器id 新的镜像id

5. 启动跨链路由
6. docker-compose up -d

7. 新的镜像可以保存起来，以备后续更换机器时一键部署；已经启动的容器在docker服务启动的时候会自动启动相关的服务