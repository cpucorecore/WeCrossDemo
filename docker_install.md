# 环境要求
要求安装docker，docker-compose
[docker-compose install](https://docs.docker.com/compose/install/)

# 修改环境变量
- 获取本机ip地址
- 修改docker/routers/env，将本机ip地址更新到3个环境变量中

# 执行docker安装
``` sh
cd routers
docker-compose up -d
```

# 执行完毕后
进入routers容器
``` sh
docker exec -it routers /bin/bash
```

