sleep 3 # wait data server start
bash /root/fisco/nodes/127.0.0.1/start_all.sh

mkdir -p /data/bcos/cert
rm -rf /data/bcos/cert/*
cp -r /root/fisco/nodes/127.0.0.1/sdk/* /data/bcos/cert

tail -f /dev/null
