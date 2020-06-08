package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload()  {
        try {
            ossClient.putObject("gulimall-cy", "ha.jpg", new FileInputStream("/Users/cuiyue/Desktop/abc.jpg"));
            System.out.println("上传完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
