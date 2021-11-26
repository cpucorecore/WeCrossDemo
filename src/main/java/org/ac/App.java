/*
  本例使用WeCross Java SDK发起对链上合约的调用，该sdk对合约调用支持有限，当不能满足需求时使用FISCO BCOS的Java SDK发起链上合约调用，它支持通过abi来构造交易，能够构造复杂的合约参数；
  本例在本地搭建了两条FISCO BCOS联盟链，分别为国密(gm_bcos)和非国密(bcos)链，展示由bcos链发起的对gm_bcos链的跨链调用；
  1. java App -->调用 payment.bcos.WeCrossHub合约 interchainInvoke方法
  2. 跨链路由 -->轮询 payment.bcos.WeCrossHub --> 得到跨链请求
  3. 跨链路由 -->调用 payment.gm_bcos.interchain合约 set方法 --> 跨链路由得到本次调用返回的值
  4. 跨链路由 -->调用回调 payment.bcos.interchain合约 set方法 步骤3返回的值
  5. 跨链路由 --> 将步骤4的回调结果写入payment.bcos.WeCrossHub合约
  6. java App -->查询 payment.bcos.WeCrossHub合约得到回调结果
 */

package org.ac;

import com.alibaba.fastjson.JSONArray;
import com.webank.wecrosssdk.exception.WeCrossSDKException;
import com.webank.wecrosssdk.resource.Resource;
import com.webank.wecrosssdk.resource.ResourceFactory;
import com.webank.wecrosssdk.rpc.WeCrossRPC;
import com.webank.wecrosssdk.rpc.WeCrossRPCFactory;
import com.webank.wecrosssdk.rpc.methods.response.TransactionResponse;
import com.webank.wecrosssdk.rpc.service.WeCrossRPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

public class App {

    public static final Logger logger = LoggerFactory.getLogger(App.class);

    static final String WeCrossHubInterchainInvoke = "interchainInvoke";

    static final String bcosWeCrossHubPath = "payment.bcos.WeCrossHub";
    static final String gm_bcosContractPath = "payment.gm_bcos.interchain";
    static final String bcosContractPath = "payment.bcos.interchain";

    static final String gm_bcosContractMethod = "set";
    static final String bcosContractMethod = "callback";

    static Resource bcosWeCrossHubContract; // bcos链桥接合约
    static Resource bcosInterchainContract; // 回调合约(payment.bcos.interchain)，跨链调用成功后跨链路由会回调该合约的callback方法
    /* 跨链目标合约(payment.gm_bcos.interchain)，通过跨链设置其数据，
       设置成功后设置的数据又会通过回调写回发起跨链调用的链端的合约payment.bcos.interchain
     */
    static Resource gm_bcosInterchainContract;

    public static final Random r = new Random();

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

        // 创建相关合约的实例
        try {
            bcosWeCrossHubContract = ResourceFactory.build(weCrossRPC, bcosWeCrossHubPath);
            bcosInterchainContract = ResourceFactory.build(weCrossRPC, bcosContractPath);
            gm_bcosInterchainContract = ResourceFactory.build(weCrossRPC, gm_bcosContractPath);
        } catch (WeCrossSDKException e) {
            logger.error("resource build failed: " + e.toString());
            weCrossRPC.logout();
            return;
        }

        // 跨链调用前查询合约内容
        queryResources();

        // 创建调用目标合约方法的参数，如果参数列表复杂参考另一种调用合约的方式: https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/assemble_transaction.html
        String paramsListString = generateContractMethodParams();
        logger.info("paramsListString:{}", paramsListString);

        String uid;
        try {
            // 调用bcos链的桥接合约发送跨链请求
            TransactionResponse response = weCrossRPC.sendTransaction(
                    bcosWeCrossHubPath,
                    WeCrossHubInterchainInvoke,
                    gm_bcosContractPath,
                    gm_bcosContractMethod,
                    paramsListString,
                    bcosContractPath,
                    bcosContractMethod).send();
            // TODO check response
            logger.info("interchain Transaction response: " + response.toString());
            uid = response.getData().getResult()[0].trim();
        } catch (Exception e) {
            logger.error("interchain Transaction failed: " + e.toString());
            weCrossRPC.logout();
            return;
        }


        // 查询跨链结果
        try {
            Thread.sleep(3000);
            String[] callRet = bcosWeCrossHubContract.call("selectCallbackResult", uid);
            logger.info("interchain call[{}] result: {}", uid, Arrays.toString(callRet));
        } catch (WeCrossSDKException | InterruptedException e) {
            logger.error("selectCallbackResult[" + uid + "] failed:{}", e.toString());
        }

        // 执行跨链调用后查询相关合约内容
        queryResources();
        weCrossRPC.logout();
    }

    private static String generateContractMethodParams() {
        JSONArray json = new JSONArray();
        json.add(0, Integer.toString(Math.abs(r.nextInt())));
        return json.toString();
    }

    private static void queryResources() {
        try {
            String[] callRet = bcosInterchainContract.call("get");
            logger.info(bcosInterchainContract.getPath() + " get info: " + Arrays.toString(callRet));

            callRet = gm_bcosInterchainContract.call("get");
            logger.info(gm_bcosInterchainContract.getPath() + " get info: " + Arrays.toString(callRet));
        } catch (WeCrossSDKException e) {
            logger.error("query resources failed: " + e.toString());
        }
    }
}
