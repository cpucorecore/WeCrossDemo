https://app.yinxiang.com/fx/92fa97c4-c0b0-4f6b-afd4-679e58329057
# 步骤
- 环境要求
- 安装依赖
- 联盟链搭建(如果已有可跳过)
- 跨链路由安装
- 区块链接入
- 配置链账号
- 网页管理平台
- 资源部署
- 跨链调用演示

## 环境要求
2台ubuntu20.04 LTS机器（虚拟机即可，实体机更佳）分别运行独立的联盟链；
其中1台用于运行跨链路由，对配置要求偏高，用机器A表示，另一台仅运行联盟链，用机器B表示：
| 配置 | A | B |
| :--: | :--: | :--: |
| CPU | 2核 | 1核 |
| 内存 | 4G | 1G |

## 安装依赖
- 安装基础工具
``` sh
sudo apt-get update;
sudo apt-get install -y ssh htop default-jdk vim curl wget openssl tree net-tools expect
```

- 安装mysql-server(B机器不需要安装)

``` sh
sudo apt-get install mysql-server
```
- 安装完毕后进行数据库的初始化，**保证能用非root权限通过命令行登录数据库**：
``` sh
mysql -uroot -p
```

ubuntu20.04 LTS下安装mysql-server常见问题
``` 
1. 安装完后用``` mysql -uroot -p ```登录不了，参考：http://www.zzvips.com/article/52104.html
2. 必须使用sudo才能登录，参考：https://blog.csdn.net/NepalTrip/article/details/82116607
3. 当密码设置过于简单时无论用户的登录方式怎样都必须用root权限登录，建议密码至少按以下格式：MyNewPass5!
```

- openjdk java环境变量设置
``` sh
sky@sun:~$ javac -version
javac 11.0.11
```
如果命令找不到，设置Java的PATH和CLASSPATH环境变量

## 联盟链搭建(如果已安装可跳过)
如果在执行以下安装步骤时报错请参考：
``` https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html ```
文中控制台的安装部分可跳过

``` sh
cd ~ && mkdir -p fisco && cd fisco;

# 下载脚本
 wget https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.8.0/build_chain.sh && chmod u+x build_chain.sh;

# 以下国密和非国密版本运行的命令不同，请根据需要选择运行
# 非国密版本创建单群组联盟链
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545

# 国密版本创建单群组联盟链
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545 -g -G
## 其中-g表示生成国密配置，-G表示使用国密SSL连接

# 启动所有节点
bash nodes/127.0.0.1/start_all.sh

# 检查节点状态
ps -ef | grep -v grep | grep fisco-bcos
```

## 跨链路由部署
参考``` https://wecross.readthedocs.io/zh_CN/latest/docs/tutorial/deploy/basic_env.html ```，如果在安装过程中遇到问题请参考该文档，正常情况按照以下安装步骤执行即可

### 跨链路由安装
- 下载
``` sh
cd ~ && mkdir -p ~/wecross-networks && cd ~/wecross-networks;
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_wecross.sh)
```

- 创建ipfile文件，内容如下：
```
127.0.0.1:8250:25500
127.0.0.1:8251:25501
```

- 安装
``` sh
bash ./WeCross/build_wecross.sh -n payment -o routers-payment -f ipfile
```

成功输出如下信息
```
[INFO] Create routers-payment/127.0.0.1-8250-25500 successfully
[INFO] Create routers-payment/127.0.0.1-8251-25501 successfully
[INFO] All completed. WeCross routers are generated in: routers-payment/
```

### 账户服务安装部署
路由的启动依赖账户服务，启动路由前先部署账户服务

- 下载
``` sh
cd ~/wecross-networks;
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_account_manager.sh);
```

- 证书拷贝
``` sh
cd ~/wecross-networks/WeCross-Account-Manager;
cp ~/wecross-networks/routers-payment/cert/sdk/* conf/
```

- 生成私钥
``` sh
bash create_rsa_keypair.sh -d conf/
```

- 配置
``` sh
cp conf/application-sample.toml conf/application.toml
```

- 检查，修改conf/application.toml中的db配置项

- 启动
``` sh
bash start.sh
```

### 启动跨链路由
``` sh
cd ~/wecross-networks/routers-payment && bash start_all.sh
```

启动成功，输出如下：
```
WeCross booting up .........
WeCross start successfully
WeCross booting up .........
WeCross start successfully
```

### 部署控制台
- 下载
``` sh
cd ~/wecross-networks;
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_console.sh)
```

- 配置
``` sh
cd ~/wecross-networks/WeCross-Console;
cp ~/wecross-networks/routers-payment/cert/sdk/* conf/ ;
cp conf/application-sample.toml conf/application.toml
```

- 启动
``` sh
bash start.sh
```

## 区块链接入
跨链分区为payment，分别接入以下区块链

路由 | 区块链 | 资源路径
-- | -- | --
8251 | FISCO BCOS | payment.bcos 
8250 | FISCO BCOS 国密版本 | payment.gm_bcos

参考``` https://wecross.readthedocs.io/zh_CN/latest/docs/stubs/bcos.html ```，如果在安装过程中遇到问题请参考该文档，正常情况按照以下安装步骤执行即可



``` sh
cd ~/wecross-networks/routers-payment/127.0.0.1-8250-25500;
bash add_chain.sh -t GM_BCOS2.0 -n gm_bcos; 

cd ~/wecross-networks/routers-payment/127.0.0.1-8251-25501;
bash add_chain.sh -t BCOS2.0 -n bcos;
# -t 链类型，-n 指定链名字，可根据-h查看使用说明

# 拷贝节点的sdk证书，证书位于节点的nodes/127.0.0.1/sdk/目录
# 这里假设本地节点为国密版本链
cp -r ~/fisco/nodes/127.0.0.1/sdk/* ~/wecross-networks/routers-payment/127.0.0.1-8250-25500/conf/chains/gm_bcos/ ;

# 192.168.0.107(需开启sshd服务)节点的为非国密版本链，xxx根据实际的用户名修改
scp -r xxx@192.168.0.107:/home/xxx/fisco/nodes/127.0.0.1/sdk/* ~/wecross-networks/routers-payment/127.0.0.1-8251-25501/conf/chains/bcos/ ;
```

- 修改配置
根据实际情况修改chainId，groupId，channelService.connectionsStr
```
~/wecross-networks/routers-payment/127.0.0.1-8250-25500/conf/chains/gm_bcos/stub.toml
~/wecross-networks/routers-payment/127.0.0.1-8251-25501/conf/chains/bcos/stub.toml
```

- 部署系统合约
``` 
cd ~/wecross-networks/routers-payment/127.0.0.1-8251-25501;
bash deploy_system_contract.sh -t BCOS2.0 -c chains/bcos -P ;
bash deploy_system_contract.sh -t BCOS2.0 -c chains/bcos -H ;

cd ~/wecross-networks/routers-payment/127.0.0.1-8251-25501;
bash deploy_system_contract.sh -t GM_BCOS2.0 -c chains/bcos -P ;
bash deploy_system_contract.sh -t GM_BCOS2.0 -c chains/bcos -H ;
```

## 配置链账号
跨链路由账号会分别关联链接的链账号

- 生成公私钥
``` sh
cd ~/wecross-networks/WeCross-Console/conf/accounts;

# 生成非国密公私钥，生成accounts目录
bash get_account.sh

# 生成国密公私钥，生成accounts_gm目录
bash get_gm_account.sh
```

- 启动控制台
``` sh
cd ~/wecross-networks/WeCross-Console/
bash start.sh
```

- 登录
``` sh
[WeCross]> login org1-admin 123456
Result: success
=============================================================================================
Universal Account:
username: org1-admin
pubKey  : 3059301306...
uaID    : 3059301306...
```

-- 添加链账号
添加
``` sh
# 参数： addChainAccount BCOS2.0 私钥位置 公钥位置 账户地址(address) 是否设置为发交易的默认链账户，可以运行addChainAccount help查看命令帮助

# 添加非国密链账号
[WeCross.org1-admin]> addChainAccount BCOS2.0 conf/accounts/accounts/0x4e89af80184147fcddc391c64ad673512236af67.public.pem conf/accounts/accounts/0x4e89af80184147fcddc391c64ad673512236af67.pem 0x4e89af80184147fcddc391c64ad673512236af67 true

# 添加过面链账号
[WeCross.org1-admin]> addChainAccount GM_BCOS2.0 conf/accounts/accounts_gm/0x4e89af80184147fcddc391c64ad673512236af67.public.pem conf/accounts/accounts_gm/0x4e89af80184147fcddc391c64ad673512236af67.pem 0x4e89af80184147fcddc391c64ad673512236af67 true
```


## 网页管理平台
http://localhost:8250/s/index.html#/login
默认用户名密码：org1-admin/123456

## 资源部署

- 启动跨链路由控制台
``` sh
cd ~/wecross-networks/WeCross-Console;
bash start.sh
```

- 在控制台部署
``` sh
# 登录
[WeCross]> login org1-admin 123456

# 部署BCOS链示例合约
[WeCross.org1-admin]> bcosDeploy payment.bcos.interchain contracts/solidity/InterchainSample.sol InterchainSample 1.0

# 部署BCOS国密链示例合约
[WeCross.org1-admin]> bcosDeploy payment.gm_bcos.interchain contracts/solidity/InterchainSample.sol InterchainSample 1.0
```

## 跨链调用演示
- 下载代码：
``` https://github.com/cpucorecore/WeCrossDemo ```

- WeCrossDemo/src/main/resources目录存放证书，密钥和配置
- 将跨链路由router-${zone}/cert/sdk/目录下的证书和密钥拷贝到本地项目的相应目录中，并根据实际跨链路由的ip地址和端口修改application.toml的server配置项
- 运行WeCrossDemo/src/main/java/org/ac/App.java的main函数即可演示跨链调用
