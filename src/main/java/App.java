import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.WeCrossRPCFactory;
import com.webank.wecrosssdk.rpc.service.WeCrossRPCService;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        try {
            WeCrossRPCService weCrossRPCService = new WeCrossRPCService();
            WeCrossRPC weCrossRPC = WeCrossRPCFactory.build(weCrossRPCService);
            weCrossRPC.login("org1-admin", "123456").send(); // 需要有登录态才能进一步操作

            Resource resource = ResourceFactory.build(weCrossRPC, "payment.bcos.HelloWorld");
            Resource gmResource = ResourceFactory.build(weCrossRPC, "payment.gm_bcos.HelloWorld");

            String[] callRet = resource.call("get");
            System.out.println((Arrays.toString(callRet)));
            callRet = gmResource.call("get");
            System.out.println((Arrays.toString(callRet)));

            String[] sendTransactionRet = resource.sendTransaction("set", "Tom1"); // sendTransaction 接口函数名 参数列表
            System.out.println((Arrays.toString(sendTransactionRet)));
            sendTransactionRet = gmResource.sendTransaction("set", "gm_Tom1"); // sendTransaction 接口函数名 参数列表
            System.out.println((Arrays.toString(sendTransactionRet)));

            callRet = resource.call("get");
            System.out.println((Arrays.toString(callRet)));
            callRet = gmResource.call("get");
            System.out.println((Arrays.toString(callRet)));


        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
