apt-get update && apt-get install -y openssl curl

mkdir -p fisco && cd fisco

curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.8.0/build_chain.sh && chmod u+x build_chain.sh

bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545

