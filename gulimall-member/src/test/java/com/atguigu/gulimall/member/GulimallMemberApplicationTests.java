package com.atguigu.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GulimallMemberApplicationTests {

    @Test
    void contextLoads() {
        //md5
//        String str = DigestUtils.md5Hex("123456");
//        System.out.println(str);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // $2a$10$v.v/uIEhnVjTRgSkCn3PX..KpSMHQRbsUlgwkSCO31JvyZxVJir0G
        String encode = bCryptPasswordEncoder.encode("123456");

        boolean reuslt = bCryptPasswordEncoder.matches("123456", "$2a$10$v.v/uIEhnVjTRgSkCn3PX..KpSMHQRbsUlgwkSCO31JvyZxVJir0G");
        System.out.println(reuslt);

    }

}
