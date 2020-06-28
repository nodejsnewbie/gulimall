package com.atguigu.gulimall.lsearch;

import com.atguigu.gulimall.search.GulimallSearchApplication;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
     * 检索
     */
    @Test
    void search() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        //按照年龄值分布聚合
        TermsAggregationBuilder aegAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(aegAgg);

        //计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        System.out.println("检索条件:");
        System.out.println(searchSourceBuilder.toString());

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            System.out.println("检索所有结果:");
            System.out.println(searchResponse.toString());

            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] searchHitsHits = searchHits.getHits();
            System.out.println("检索普通结果:");
            for (SearchHit hit : searchHitsHits) {
                System.out.println(hit.toString());
            }
            System.out.println("检索聚合结果:");
            Aggregations aggregations = searchResponse.getAggregations();
            Terms ageAgg = aggregations.get("ageAgg");
            for (Terms.Bucket bucket : ageAgg.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                System.out.println("年龄:" + keyAsString + "有几个:" + bucket.getDocCount());
            }

            Avg avg = aggregations.get("balanceAvg");
            System.out.println("平均薪资:" + avg.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
