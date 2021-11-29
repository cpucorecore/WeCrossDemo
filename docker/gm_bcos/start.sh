sleep 3 # wait data server start
bash /root/fisco/nodes/127.0.0.1/start_all.sh

mkdir -p /data/gm_bcos/cert
rm -rf /data/gm_bcos/cert/*
cp -r /root/fisco/nodes/127.0.0.1/sdk/* /data/gm_bcos/cert

tail -f /dev/null
