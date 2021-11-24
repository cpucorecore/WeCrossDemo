package org.ac;

import com.webank.wecrosssdk.exception.WeCrossSDKException;
import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.RemoteCall;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.WeCrossRPCFactory;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import com.webank.wecrosssdk.rpc.methods.response.XAResponse;
import com.webank.wecrosssdk.rpc.service.WeCrossRPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class App {

    public static final Logger logger = LoggerFactory.getLogger(App.class);

    static final String bcosHelloWorldPath = "payment.bcos.HelloWorld";
    static final String gmBcosHelloWorldPath = "payment.gm_bcos.HelloWorld";
    static String[] paths = new String[]{bcosHelloWorldPath, gmBcosHelloWorldPath};

    static Resource resource;
    static Resource gmResource;

    private static String getTransactionID() {
        int tid = Util.getTransactionIDFromFile();
        return Integer.toString(tid);
    }

    private static void updateTransactionID() throws IOException {
        Util.updateTransactionIDToFile();
    }

    private static void queryResources() {
        try {
            String[] callRet = resource.call("get");
            logger.info(resource.getPath() + " get info: " + Arrays.toString(callRet));

            callRet = gmResource.call("get");
            logger.info(gmResource.getPath() + " get info: " + Arrays.toString(callRet));
        } catch (WeCrossSDKException e) {
            logger.error("query resources failed: " + e.toString());
        }
    }

    public static void main(String[] args) {
        WeCrossRPCService weCrossRPCService = new WeCrossRPCService();

        WeCrossRPC weCrossRPC;
        try {
            weCrossRPC = WeCrossRPCFactory.build(weCrossRPCService);
        } catch (WeCrossSDKException e) {
            logger.error("RPC build failed: " + e.toString());
            return;
        }

        try {
            weCrossRPC.login("org1-admin", "123456").send(); // 需要有登录态才能进一步操作
        } catch (Exception e) {
            logger.error("RPC login failed: " + e.toString() + ", check your account");
            weCrossRPC.logout();
            return;
        }

        try {
            resource = ResourceFactory.build(weCrossRPC, bcosHelloWorldPath);
        } catch (WeCrossSDKException e) {
            logger.error("resource[" + bcosHelloWorldPath + "] build failed: " + e.toString());
            weCrossRPC.logout();
            return;
        }

        try {
            gmResource = ResourceFactory.build(weCrossRPC, gmBcosHelloWorldPath);
        } catch (WeCrossSDKException e) {
            logger.error("resource[" + gmBcosHelloWorldPath + "] build failed: " + e.toString());
            weCrossRPC.logout();
            return;
        }

        // 执行事务前查询合约内容
        queryResources();

        // 自己维护事务id
        String tid = getTransactionID();
        logger.info("get a tid: " + tid);

        // 开始事务，分别锁定bcos链的HelloWorld合约和gm_bcos链的HelloWorld合约
        try {
            XAResponse xaResponse = weCrossRPC.startXATransaction(tid, paths).send();
            logger.info("start Transaction response: " + xaResponse.toString());
        } catch (Exception e) {
            logger.error("start Transaction failed: " + e.toString());
            weCrossRPC.logout();
            return;
        }

        // 执行事务，在bcos链执行交易
        try {
            TransactionResponse response = weCrossRPC.sendXATransaction(tid, bcosHelloWorldPath, "set", "修改合约内容:" + tid).send();
            logger.info("send bcos Transaction response: " + response.toString());
        } catch (Exception e) {
            logger.error("send bcos Transaction failed: " + e.toString());
            try {
                weCrossRPC.rollbackXATransaction(tid, paths).send();
            } catch (Exception exception) {
                logger.error("rollback Transaction failed: " + e.toString());
            }
            weCrossRPC.logout();
            return;
        }

        // 执行事务，在gm_bcos链执行交易
        try {
            TransactionResponse response = weCrossRPC.sendXATransaction(tid, gmBcosHelloWorldPath, "set", "修改合约内容:" + tid).send();
            logger.info("send gm_bcos Transaction response: " + response.toString());
        } catch (Exception e) {
            logger.error("send gm_bcos Transaction failed: " + e.toString());
            try {
                weCrossRPC.rollbackXATransaction(tid, paths).send();
            } catch (Exception exception) {
                logger.error("rollback Transaction failed: " + e.toString());
            }
            weCrossRPC.logout();
            return;
        }

        // 提交事务，释放开始事务时锁定的资源
        try {
            XAResponse response = weCrossRPC.commitXATransaction(tid, paths).send();
            logger.info("commit Transaction response: " + response.toString());
        } catch (Exception e) {
            logger.error("send bcos Transaction failed: " + e.toString());
            try {
                weCrossRPC.rollbackXATransaction(tid, paths).send();
            } catch (Exception exception) {
                logger.error("rollback Transaction failed: " + e.toString());
            }
            weCrossRPC.logout();
            return;
        }

        // 执行事务后查询内容
        queryResources();

        // 更新本地事务id
        try {
            updateTransactionID();
        } catch (IOException e) {
            e.printStackTrace();
        }

        weCrossRPC.logout();
    }
}
