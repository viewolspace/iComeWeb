package com.com.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youguu.core.logging.Log;
import com.youguu.core.logging.LogFactory;
import com.youguu.core.pojo.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2018/8/14.
 */
public class QingSmsServiceImpl implements ISmsService {

    private static Log log = LogFactory.getLog(QingSmsServiceImpl.class);

    public static Response<String> sendGet(String url, Map<String, String> head,Map<String,String> params, String charset) {
        Response res = new Response();
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;

        try {

            StringBuilder sb = new StringBuilder(url);

            sb.append("?");

            for(Map.Entry<String,String> entry:params.entrySet()){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }


            String path = sb.toString();
            path = path.substring(0,path.length()-1);

            URL e2 = new URL(path);
            HttpURLConnection connection = (HttpURLConnection)e2.openConnection();
            connection.setRequestMethod("GET");
            for(Map.Entry<String,String> entry:head.entrySet()){
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.connect();

            int code1 = connection.getResponseCode();
            System.out.println(code1);
            if(code1 == 200) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

                String line2;
                while((line2 = in.readLine()) != null) {
                    result.append(line2);
                }

                res.setCode("0000");
                res.setMsg("ok");
                res.setT(result.toString());
            } else {
                res.setCode("1001");
                res.setMsg("返回码异常:" + code1);
            }
        } catch (SocketTimeoutException var22) {
            res.setCode("1001");
            res.setMsg("连接超时");
            var22.printStackTrace();
        } catch (Exception var23) {
            res.setCode("1001");
            res.setMsg("连接异常");
            var23.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (Exception var21) {
                res.setCode("1002");
                res.setMsg("关闭连接异常");
                var21.printStackTrace();
            }

        }

        return res;
    }

    @Override
    public int sendRand(String phone,String rand) {

        String host = "https://feginesms.market.alicloudapi.com/codeNotice";
        String path = "/codeNotice";
        String method = "GET";
        String appcode = "b1569a310a8c42afb4a62fc71f8341e9";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("param", rand);
        querys.put("phone", phone);
        querys.put("sign", "51806");
        querys.put("skin", "100001");
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 或者直接下载：
             * http://code.fegine.com/HttpUtils.zip
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             * 相关jar包（非pom）直接下载：
             * http://code.fegine.com/aliyun-jar.zip
             *Response [code=0000, msg=ok, t={"Message":"手机号码错误！","Code":"101","RequestId":"","BizId":""}]
             * Response [code=0000, msg=ok, t={"Message":"OK","RequestId":"20764A63-8012-4DEB-8F1C-0F9BAB58D502","BizId":"576505734212865015^0","Code":"OK"}]
             */

            Response<String> response = sendGet(host, headers, querys, "UTF-8");
            System.out.println(response.toString());

            if("0000".equals(response.getCode())){
                JSONObject json = JSON.parseObject(response.getT()) ;
                String code = json.getString("Code");
                if("OK".equals(code)){
                    return 1;
                }else{
                    log.error("sms_err:" + code);
                }
            }
            //获取response的body
//            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String[] args) {
        QingSmsServiceImpl q = new QingSmsServiceImpl();
        q.sendRand("13810436365","1234");
    }
}
