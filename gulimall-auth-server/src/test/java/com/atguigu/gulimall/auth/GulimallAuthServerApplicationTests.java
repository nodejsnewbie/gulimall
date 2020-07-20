package com.atguigu.gulimall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

@SpringBootTest
class GulimallAuthServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testCode() {
        int radomInt = (int) ((Math.random() * 9 + 1) * 100000);
        String radomString = String.valueOf(radomInt);
        System.out.println(radomString);
    }

}
