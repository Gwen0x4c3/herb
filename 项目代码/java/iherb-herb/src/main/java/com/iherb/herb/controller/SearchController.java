package com.iherb.herb.controller;

import com.iherb.common.utils.R;
import com.iherb.herb.entity.es.SearchResult;
import com.iherb.herb.service.HerbService;
import com.iherb.herb.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SearchController {

    @Autowired
    private HerbService herbService;
    @Autowired
    private SearchService searchService;

    @GetMapping
    public R search(String keyword, Integer type, Integer page, HttpServletRequest request) {
        SearchResult result = searchService.search(keyword, type, page, getTicket(request));
        return R.ok().data("page", result);
    }

    @GetMapping("searchForHints")
    public R searchForHints(String keyword, Integer type) {
        List<String> list = searchService.searchForHints(keyword, type);

        return R.ok().data("list", list);
    }

    @GetMapping("hotwords")
    public R getHotWords(Integer type) {
        List<String> list = searchService.getHotWords(type);
        return R.ok().data("list", list);
    }

    @GetMapping("history")
    public R getHistory(Integer type, HttpServletRequest request) {
        String ticket = getTicket(request);
        List<String> list = searchService.getSearchHistory(type, ticket);
        return R.ok().data("list", list);
    }

    @DeleteMapping("history")
    public R clearHistory(HttpServletRequest request) {
        String ticket = getTicket(request);
        searchService.clearSearchHistory(ticket);
        return R.ok();
    }

    private String getTicket(HttpServletRequest request) {
        return request.getHeader("TICKET");
    }
}
