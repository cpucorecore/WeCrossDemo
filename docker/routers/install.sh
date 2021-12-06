apt-get update && apt-get install -y curl wget vim net-tools telnet ssh mysql-client default-jdk htop iputils-ping iproute2

source install_env.sh

mkdir -p ~/wecross-networks && cd ~/wecross-networks

# install wecross
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_wecross.sh)

bash ./WeCross/build_wecross.sh -n ${ROUTERS_ZONE_NAME} -o routers-${ROUTERS_ZONE_NAME} -f ./ipfile

# install account manager
cd ~/wecross-networks
echo -e "${MYSQL_IP}\n${MYSQL_PORT}\n${MYSQL_USER}\n${MYSQL_PASSWORD}\n" | bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_account_manager.sh)

cd ~/wecross-networks/WeCross-Account-Manager/
cp ~/wecross-networks/routers-${ROUTERS_ZONE_NAME}/cert/sdk/* conf/

bash create_rsa_keypair.sh -d conf/

cp conf/application-sample.toml conf/application.toml

# install console
cd ~/wecross-networks
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_console.sh)

cd ~/wecross-networks/WeCross-Console
cp ~/wecross-networks/routers-${ROUTERS_ZONE_NAME}/cert/sdk/* conf/
cp conf/rootlication-sample.toml conf/rootlication.toml