package com.atguigu.guilimall.product;

//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.gulimall.product.GulimallProductApplication;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallProductApplication.class)
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

//    @Autowired
//    private OSS ossClient;

    @Test
    void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("haha");
//        brandService.save(brandEntity);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println(item);
        });

    }

    @Test
    void testUpload() {
////         Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI4G7XQFJNbw2FWDh8Koca";
//        String accessKeySecret = "9wERyuZHSPRylwDsa63R6MIg0KxKAP";
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        // 上传文件流。
//        InputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream("/Users/cuiyue/Desktop/abc.jpg");
//            ossClient.putObject("gulimall-cy", "abc.jpg", inputStream);
//            // 关闭OSSClient。
//            ossClient.shutdown();
//            System.out.println("上传完成");
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }


    @Test
    void testUpload2() {
//        try {
//            ossClient.putObject("gulimall-cy", "abc.jpg", new FileInputStream("/Users/cuiyue/Desktop/abc.jpg"));
//            System.out.println("上传完成");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }

}
