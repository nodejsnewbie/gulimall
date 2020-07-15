package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.vo.SearchParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model) {
        //1. 根据页面传递过来的查询参数，到ES中检索商品
//        SearchReult result = mallSearchService.search(param);
//        model.addAttribute("result", result);
        return "list";
    }
}
