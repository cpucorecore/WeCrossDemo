sleep 5
mkdir -p /data/router/cert
rm -rf /data/router/cert/*
cp ~/wecross-networks/routers-payment/cert/sdk/* /data/router/cert

cd ~/wecross-networks/routers-payment/${GM_BCOS_ROUTER_IP}-8250-25500
bash add_chain.sh -t GM_BCOS2.0 -n gm_bcos
cp -r /data/gm_bcos/cert/* ~/wecross-networks/routers-payment/${GM_BCOS_ROUTER_IP}-8250-25500/conf/chains/gm_bcos/

cd ~/wecross-networks/routers-payment/${BCOS_ROUTER_IP}-8251-25501
bash add_chain.sh -t BCOS2.0 -n bcos
cp -r /data/bcos/cert/* ~/wecross-networks/routers-payment/${BCOS_ROUTER_IP}-8251-25501/conf/chains/bcos/

tail -f /dev/null
