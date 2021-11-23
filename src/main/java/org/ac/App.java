package org.ac;

import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.RemoteCall;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.WeCrossRPCFactory;
import com.webank.wecrosssdk.rpc.methods.response.XAResponse;
import com.webank.wecrosssdk.rpc.service.WeCrossRPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class App {

    public static final Logger logger = LoggerFactory.getLogger(App.class);

    static String bcosHelloWorldPath = "payment.bcos.HelloWorld";
    static String gmBcosHelloWorldPath = "payment.gm_bcos.HelloWorld";
    static String[] paths = new String[]{bcosHelloWorldPath, gmBcosHelloWorldPath};

    private static String getTransactionID() {
        int tid = Util.getTransactionIDFromFile();
        return Integer.toString(tid);
    }

    private static void updateTransactionID() throws IOException {
        Util.updateTransactionIDToFile();
    }

    public static void main(String[] args) {
        try {
            WeCrossRPCService weCrossRPCService = new WeCrossRPCService();
            WeCrossRPC weCrossRPC = WeCrossRPCFactory.build(weCrossRPCService);
            weCrossRPC.login("org1-admin", "123456").send(); // 需要有登录态才能进一步操作

            Resource resource = ResourceFactory.build(weCrossRPC, bcosHelloWorldPath);
            Resource gmResource = ResourceFactory.build(weCrossRPC, gmBcosHelloWorldPath);

            String[] callRet = resource.call("get");
            System.out.println((Arrays.toString(callRet)));
            callRet = gmResource.call("get");
            System.out.println((Arrays.toString(callRet)));

            RemoteCall<XAResponse> xaResponse = weCrossRPC.startXATransaction(getTransactionID(), paths);

            callRet = resource.call("get");
            System.out.println((Arrays.toString(callRet)));
            callRet = gmResource.call("get");
            System.out.println((Arrays.toString(callRet)));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
