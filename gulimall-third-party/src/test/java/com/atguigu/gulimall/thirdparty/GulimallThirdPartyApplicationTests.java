package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;


@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testUpload() {
        try {
            ossClient.putObject("gulimall-cy", "ha.jpg", new FileInputStream("/Users/cuiyue/Desktop/abc.jpg"));
            System.out.println("上传完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendSms() {
        smsComponent.sendSmsCode("18513027xxx", "135246");
    }


    @Test
    public void test() {
        String url = "https://xxx";
        HashMap<String, Object> data = new HashMap<>();
        data.put("phone", "xxxx");
        data.put("validate_code", "1234");
        String result = doPost(url, data);
        System.out.println(result);
    }

    public static String doPost(String url, Map<String, Object> paramMap) {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SSL套接字连接工厂,NoopHostnameVerifier为信任所有服务器
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        /**
         * 通过setSSLSocketFactory(sslsf)保证httpclient实例能发送Https请求
         */
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setMaxConnTotal(50)
                .setMaxConnPerRoute(50).setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(60000).setConnectTimeout(60000).setSocketTimeout(60000).build())
                .build();

        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "multipart/form-data");
        httpPost.addHeader("CLIENT", "android");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry
                        .getValue().toString()));
            }
            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpclient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpclient) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static X509TrustManager tm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };


}
