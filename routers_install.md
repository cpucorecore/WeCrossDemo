# WeCross安装
用于部署FISCO BCOS各联盟链之间的跨链路由，不支持Fabric联盟链

## 准备工作
1. 依赖
- mysql server，WeCross依赖mysql server才能启动

2. 机器配置
- 操作系统：ubuntu 20.04 LTS
- 一个跨链分区由1个Account Manager和多个router组成
- router的数量由跨链分区跨几条链决定
- Account Manager需要占用内存600M，每个router需要占用内存1G；对cpu的要求是每个router1个核心
- 根据上述要求选择合理的服务器配置

3. 信息收集
- 按照需求方确定跨链分区名字，本安装文档以payment作为跨链分区
- 各条链的sdk证书，用于配置跨链路由以接入相应的联盟链

## 安装
基础安装
----
```shell
sudo apt-get update && sudo apt-get install -y ssh htop default-jdk vim curl wget openssl tree net-tools expect mysql-client iputils-ping iproute2
```

安装WeCross
----
- 下载
```shell
mkdir -p ~/wecross-networks && cd ~/wecross-networks && bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_wecross.sh)
```

- 配置跨链路由列表
```shell
cd ~/wecross-networks
vim ipfile

# 在文件中键入以下内容，确保8250，8251，25500，25501端口没有被占用，防火墙开放端口
127.0.0.1:8250:25500
127.0.0.1:8251:25501

# 每一行代表一个跨链路由(router)，分别表示:路由的ip:rpc端口:p2p端口，需要跨几条链就配置几行，端口依次增加
```

- 创建跨链路由
```shell
bash ./WeCross/build_wecross.sh -n payment -o routers-payment -f ipfile
```

部署账户服务(Account Manager)
----
- 下载，执行过程中需要输入mysql的信息，ip，port，user，password，根据命令行依次输入
```shell
cd ~/wecross-networks && bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_account_manager.sh)
```

- 配置证书
```shell
cd ~/wecross-networks/WeCross-Account-Manager/
cp ~/wecross-networks/routers-payment/cert/sdk/* conf/
```

- 生成私钥
```shell
bash create_rsa_keypair.sh -d conf/
```

- 配置
```shell
cp conf/application-sample.toml conf/application.toml
vim conf/application.toml
```

*admin：配置admin账户，此处可默认，router中的admin账户需与此处对应，用于登录账户服务
*db：配置自己的数据库账号密码

- 启动
```shell
bash start.sh
```

区块链接入
----
- 生成配置框架
```shell
cd ~/wecross-networks/routers-payment/127.0.0.1-8250-25500

 # -t 链类型，-n 指定链名字，可根据-h查看使用说明
bash add_chain.sh -t BCOS2.0 -n bcos
```
执行成功，输出如下。如果执行出错，请查看屏幕打印提示。
```text
Chain "bcos" config framework has been generated to "conf/chains/bcos"
```

生成的目录结构如下：
```text
tree conf/chains/bcos/
conf/chains/bcos/
├── WeCrossHub
│   └── WeCrossHub.sol        # 桥接合约
├── WeCrossProxy              # 代理合约
│   └── WeCrossProxy.sol
├── admin                     # stub内部内置账户，部署代理合约和桥接合约的默认账户
│   ├── xxxxx_secp256k1.key
│   └── account.toml
└── stub.toml                 # 插件配置文件
```

- 拷贝证书
```shell
cp -r xxxxxx/nodes/127.0.0.1/sdk/* ~/wecross-networks/routers-payment/127.0.0.1-8250-25500/conf/chains/bcos/
```

- 编辑配置文件stub.toml：
  根据实际情况配置: chain.groupId, chain.chainId, channelService.connectionsStr

- 编辑路由配置文件conf/wecross.toml
  如果需要为本机以外提供rpc服务，需要修改wecross.toml中的rpc.address为'0.0.0.0'

- 部署系统合约

非国密链
```shell
# 部署代理合约
bash deploy_system_contract.sh -t BCOS2.0 -c chains/bcos -P

# 部署桥接合约
bash deploy_system_contract.sh -t BCOS2.0 -c chains/bcos -H
```

国密链
```shell
# 部署代理合约
bash deploy_system_contract.sh -t GM_BCOS2.0 -c chains/bcos -P

# 部署桥接合约
bash deploy_system_contract.sh -t GM_BCOS2.0 -c chains/bcos -H
```

部署成功，则输出如下内容。若失败可查看提示信息和错误日志。
```text
SUCCESS: WeCrossProxy:xxxxxxxx has been deployed! chain: chains/bcos
SUCCESS: WeCrossHub:xxxxxxxx has been deployed! chain: chains/bcos
```

- 启动路由
```shell
bash start
```

按照4.1-4.4依次配置并启动其他路由

部署控制台
----
- 下载
```shell
cd ~/wecross-networks && bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_console.sh)
```

- 配置
```shell
cd ~/wecross-networks/WeCross-Console
cp ~/wecross-networks/routers-payment/cert/sdk/* conf/
cp conf/application-sample.toml conf/application.toml
```

- 启动
```shell
bash start.sh
```

- 帮助
```shell
help
```

- 退出
```shell
quit
```
