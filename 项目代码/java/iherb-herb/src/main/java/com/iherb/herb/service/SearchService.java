package com.iherb.herb.service;

import com.iherb.herb.entity.es.SearchResult;

import java.util.List;

public interface SearchService {

    SearchResult search(String keyword, Integer type, Integer page, String ticket);

    /**
     * 获取搜索热词
     * @param type 搜索类型，0-中药，1-处方
     * @return 热词列表
     */
    List<String> getHotWords(int type);

    /**
     * 查询用户搜搜历史
     * @param ticket 用户ticket
     * @return 搜索历史列表
     */
    List<String> getSearchHistory(int type, String ticket);

    /**
     * 删除用户搜索历史
     * @param ticket 用户ticket
     */
    void clearSearchHistory(String ticket);

    /**
     * 获取推荐搜索，返回到提示框中
     * @param keyword 关键词
     * @param type 搜索类型，0-中药，1-处方
     * @return 推荐搜索的文本
     */
    List<String> searchForHints(String keyword, Integer type);
}
