package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000118656175";
    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDUe6VcBMyBeKBZJ73BDjVpomSlgrG2dYlB2h+7y08xvrtKjhSKLsDgoag6o7lfYrd/u7mu1CpTGxqDuCTMxo1rqaPs0BH6qnGIyfnpPecL/l1CYj2WrYDw/2ngQG/vFrmP3Op9LAds5dRnZ9VVoQXA6a8L489/NoepvRg0N1ffWz58MgjoO1mhTPnshvT+Fo1Mev3ox/imnG2yc7ljS0j44n8zdfLUBh2cQ7CwfJb419NJrx8Gr6NCybfbofniaiexhERsEaHiv2pvtPH/vMGr47zDPCoa2MEoOJkiV8fB475LdqtFQXcH8W/ujvr0Zy9+Lz3qvzIJu3WUli93qSINAgMBAAECggEAGE2sNEI4QfbSIGBzS8mcQmDYsjWorf3znRyd1JUEkeQG+BgYMVX3qbnTzCE96rMX2fxW2LBbxvWqqrY9P07WjiHxJf0GkW09Xy/67bqu2qhVcmpaMQnXXblZGtVD+EzZHKNgDsEieEoxaUJvHEQ2rEIOS4XbDenmyntecVqDKoDHz5TqxrCYo1riTDiq1MtQOlyScEEwcSGm8oxY0d0R+RDKxK0nsAR4gkc6F8YRLqAzRkRz9MBVnAZBRW2puRqVqdmbSoKfRIIo0Honsp2GpGjekpuOxwm3VUoaYJTFU2Lj6rGpapucwOVvdfca29Mt/NVWwQAKFuKkKJC2O6LgAQKBgQD1zHiI4C8Npt7y3XzxmvdPOet7lwGczT/P25b5w6LariyMkCaFWAfq63TcTXqFsXIJuzWuvSDNilg3aynnXjbsGiCUpb3OoEuWXf+Ny2V8SICsXc670Qx4GcApRrs/vvviEu1hDAJu56ncp1IobI+28b/S5XcrdWk25Hk+n69tAQKBgQDdTTTpXkSZ+2cBFCD+1EuaxVnLphLVn16Gvvr1x+RoElWlZ8CpaheWuqcQhxEPwKgc8EswlnUSf3uz81/JEHvzCePrvafp90UzVItPjPTfJAS0NHMe99FCQprfgpLEYsu+P/7ncrjoAZrklFUcMqqaJzuojNX9mZ+7ta8mhJuZDQKBgE98LKpOnMi0Yrp053eV3k2vdDgGMA5NzUnhP+RK4HoJAvz9jIdoXT8ZnzOylmFvbWo6MPTt0hLFnxtkuytHq0kUdd677jJIYckhYoeu84WKSJn0x+qygm7AHQT8C4dksj18U2kjohHNNn/VV9scqCjH6nk/nRbfpAONRKv8pZUBAoGBANtOUasawj0eSHtnKTOfyrS78jrAf1W3s4Mw65550z3XPYzG/sRk4K98lWbpktzbfT4AW2LxF1+Xek1gV4H3lf3JxVavAiH361ns5ImXFGu1U/wJJjSAPZ2+R6BL04pOpb4JyWAv7lHslUUEQjvJcguF6bckqkPA4+yvJb20GEZZAoGARhYEPSfTUHqkcKzjZgq3b7P+n0wZ8YWN41UV4Og3KDBkPSJcA8TYSmsRvNi46mNAQjJB5wzfZbeG/r9BernZ9sw3F5yfvCIjNp+y9y2ugWaIDOBQBAfRjgLaamgR0R/59qPqAlY2rds0sQGFKcBikwb+HUWyeSR/4ONPh8D48zQ=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwgs8ULGib/BBR6BBbxCSm9f8+qptm5qPwV9cFa9qHvF1A06QCyT/F6Lst4enUWY0tsXW5R4GBEr/WZrPL3qBygU2iHTQfKNVt3+/mLV1YTBtMzenEmwheFMkQrync4n+QYpMX5zUVPBkLSjOMTLSQ8hwhIE8hNEOieevmGNo1z5irvvN5V8uzhrElHJT2T1PrS1lRpACvUgLwUuM1YJ4D+IjDA6juMkTfpL5NI+7MkqMt92sHPXg2hH2S05z5KxYZq4VGqmWlacV7cIx+gS9LXnHOAkRTKzywHeD1EQBQ4E0A9OjghrfpKjCam1IbNr82oGDFD6av8SFXthhx821/wIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://oq1xij58s4.52http.net/alipay.trade.wap.pay-java-utf-8/notify_url.jsp";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://oq1xij58s4.52http.net/alipay.trade.wap.pay-java-utf-8/return_url.jsp";
    // 签名方式
    private String sign_type = "RSA2";
    // 字符编码格式
    private String charset = "utf-8";
    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
