source env.sh

apt-get update && apt-get install -y curl wget vim net-tools telnet ssh mysql-client default-jdk

mkdir -p /root/wecross-networks && cd /root/wecross-networks

# install wecross
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_wecross.sh)

echo -n "127.0.0.1:8250:25500
127.0.0.1:8251:25501" > ipfile

bash ./WeCross/build_wecross.sh -n payment -o routers-payment -f ipfile

# install account manager
cd ~/wecross-networks
echo -e "${MYSQL_IP}\n3306\nroot\nMyNewPass5ABC\n" | bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_account_manager.sh)

cd ~/wecross-networks/WeCross-Account-Manager/
cp ~/wecross-networks/routers-payment/cert/sdk/* conf/

bash create_rsa_keypair.sh -d conf/

cp conf/application-sample.toml conf/application.toml

# install console
cd /root/wecross-networks
bash <(curl -sL https://gitee.com/WeBank/WeCross/raw/master/scripts/download_console.sh)

cd /root/wecross-networks/WeCross-Console
cp /root/wecross-networks/routers-payment/cert/sdk/* conf/
cp conf/rootlication-sample.toml conf/rootlication.toml


# generate chain accounts
cd ~/wecross-networks/WeCross-Console/conf/accounts
bash get_account.sh
bash get_gm_account.sh
