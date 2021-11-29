# 环境要求
要求安装docker，docker-compose
[docker-compose install](https://docs.docker.com/compose/install/)

# 修改环境变量
不同的docker宿主机修改的方式不同
## mac
用默认配置即可
## window


# 启动docker mysql
``` sh
bash start_mysql.sh
```

# 启动跨链路由相关容器
``` sh
bash start.sh
```

# 开启相关服务
- 进入routers容器
``` sh
docker exec -it routers /bin/bash
```

## 在routers容器内执行：
### 启动Account Manager
``` sh
cd /root/wecross-networks/WeCross-Account-Manager
```
- 修改conf/application.toml
- - 修改db.url，将localhost替换为mysql的ip地址，mac宿主机配置为docker.for.mac.host.internal
- - 修改db.password='MyNewPass5ABC'

- 启动Account Manager
``` sh
bash start.sh
```

### 启动gm_bcos路由
``` sh
cd /root/wecross-networks/routers-payment/127.0.0.1-8250-25500
```

- 修改配置文件conf/chains/gm_bcos/stub.toml
  connectionsStr配置为gm_bcos链的ip地址
- 修改conf/wecross.toml
  rpc.address配置为'0.0.0.0'

- 部署系统合约
``` sh
bash deploy_system_contract.sh -t GM_BCOS2.0 -c chains/gm_bcos -H

bash deploy_system_contract.sh -t GM_BCOS2.0 -c chains/gm_bcos -P
```

- 启动路由
``` sh
bash start.sh
```

### 启动bcos路由
``` sh
cd /root/wecross-networks/routers-payment/127.0.0.1-8251-25501
```

- 修改配置文件conf/chains/gm_bcos/stub.toml
  connectionsStr配置为gm_bcos链的ip地址
- 修改conf/wecross.toml
  rpc.address配置为'0.0.0.0'

- 部署系统合约
``` sh
bash deploy_system_contract.sh -t BCOS2.0 -c chains/bcos -H

bash deploy_system_contract.sh -t BCOS2.0 -c chains/bcos -P
```

- 启动路由
``` sh
bash start.sh
```

### 添加链账号，部署跨链demo合约
- 启动WeCross终端
``` sh
cd /root/wecross-networks/WeCross-Console
bash start.sh

# 登陆
[WeCross]> login org1-admin 123456
Result: success
```

- 添加链账号
``` sh
# 添加非国密链账号
[WeCross.org1-admin]> addChainAccount BCOS2.0 conf/accounts/accounts/0x0ef0fb979948debde15a0d64795c18e6e22dbfb6.public.pem conf/accounts/accounts/0x0ef0fb979948debde15a0d64795c18e6e22dbfb6.pem 0x0ef0fb979948debde15a0d64795c18e6e22dbfb6 true

# 添加国密链账号
[WeCross.org1-admin]> addChainAccount GM_BCOS2.0 conf/accounts/accounts_gm/0x48853e1f42f582692b02ebd3d04987e28cffec28.public.pem conf/accounts/accounts_gm/0x48853e1f42f582692b02ebd3d04987e28cffec28.pem 0x48853e1f42f582692b02ebd3d04987e28cffec28 true

```


- 部署合约
``` sh
# 部署非国密链跨链demo合约
[WeCross.org1-admin]> bcosDeploy payment.bcos.interchain contracts/solidity/InterchainSample.sol InterchainSample 1.0
Result: 0x21c60948ac6ab03af4cd52e0a03fa8a8cb98480e

# 部署国密链跨链demo合约
[WeCross.org1-admin]> bcosDeploy payment.gm_bcos.interchain contracts/solidity/InterchainSample.sol InterchainSample 1.0
Result: 0xbe191f11175b6a27094947a27c53f3fe55140468
```

### 登陆跨链路由平台
[跨链路由平台](http://localhost:8250/s/index.html#/home)

