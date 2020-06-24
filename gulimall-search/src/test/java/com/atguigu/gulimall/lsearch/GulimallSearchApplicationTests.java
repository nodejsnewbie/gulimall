package com.atguigu.gulimall.lsearch;

import com.atguigu.gulimall.search.GulimallSearchApplication;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallSearchApplication.class)
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 存储数据到es
     */
    @Test
    void index() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("users")
                .id("1").source(jsonMap);
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储数据到es
     */
    @Test
    void indexAsync() {
        System.out.println("1111");
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("users")
                .id("1").source(jsonMap);
        client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {

            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println("22");
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("33");
            }
        });
        System.out.println("444");
    }


}
