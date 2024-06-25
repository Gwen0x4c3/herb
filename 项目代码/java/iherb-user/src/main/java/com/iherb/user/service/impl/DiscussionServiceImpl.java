package com.iherb.user.service.impl;

import com.iherb.common.constant.RedisKey;
import com.iherb.user.entity.vo.ArticleVo;
import com.iherb.user.entity.vo.DiscussionVo;
import com.iherb.user.util.KeywordUtil;
import joptsimple.internal.Strings;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.user.dao.DiscussionDao;
import com.iherb.user.entity.DiscussionEntity;
import com.iherb.user.service.DiscussionService;


@Service("discussionService")
public class DiscussionServiceImpl extends ServiceImpl<DiscussionDao, DiscussionEntity> implements DiscussionService {

    @Autowired
    private KeywordUtil keywordUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final int ROWS = 10;

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<DiscussionEntity> page = this.page(
                new Query<DiscussionEntity>().getPage(curPage, limit),
                new QueryWrapper<DiscussionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDiscussion(DiscussionEntity discussion, String userId) {
        discussion.setUserId(userId);
        discussion.setCommentCount(0);
        discussion.setViewCount(0);

        List<String> keywords = extractKeywords(discussion.getContent());
        String keywordStr = String.join(",", keywords);
        discussion.setKeywords(keywordStr);

        discussion.setCreateTime(new Date());
        discussion.setModifyTime(new Date());

        System.out.println("讨论：" + discussion);
        this.save(discussion);
    }

    @Override
    public PageUtils getDiscussions(Long page) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        IPage<DiscussionEntity> myPage = this.page(
                new Query<DiscussionEntity>().getPage(page, (long) ROWS),
                new QueryWrapper<DiscussionEntity>()
        );
        List<DiscussionEntity> records = myPage.getRecords();
        List<DiscussionVo> list = records.stream().map(discussion -> {
            DiscussionVo vo = new DiscussionVo();
            BeanUtils.copyProperties(discussion, vo);

            String viewKey = RedisKey.DISCUSSION_VIEW.replace("#1", vo.getId().toString());
            Integer viewCount = (Integer) valueOperations.get(viewKey);
            if (viewCount == null) {
                valueOperations.set(viewKey, vo.getViewCount());
            } else {
                vo.setViewCount(viewCount);
            }
            vo.setCover(extractImage(vo.getContent()));
            return vo;
        }).collect(Collectors.toList());
        return new PageUtils(list, (int) myPage.getTotal(), (int) myPage.getSize(), (int) myPage.getCurrent());
    }

    @Override
    public DiscussionVo getDiscussion(Long id, String userId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String discussionKey = RedisKey.DISCUSSION.replace("#1", String.valueOf(id));
        String viewKey = RedisKey.DISCUSSION_VIEW.replace("#1", String.valueOf(id));
        DiscussionVo discussion = (DiscussionVo) valueOperations.get(discussionKey);
        Integer viewCount = (Integer) valueOperations.get(viewKey);
        if (discussion != null) {
            discussion.setViewCount(viewCount + 1);
            valueOperations.increment(viewKey);
        } else {
            discussion = this.baseMapper.getDiscussion(id);
            valueOperations.set(discussionKey, discussion, 24, TimeUnit.HOURS);
            valueOperations.set(viewKey, discussion.getViewCount(), 24, TimeUnit.HOURS);
        }

        //分析keyword，设置用户interest
        String keywordsStr = discussion.getKeywords();
        if (StringUtils.isBlank(keywordsStr)) {
            return discussion;
        }
        String[] keywords = keywordsStr.split(",");
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String interestKey = RedisKey.USER_INTEREST.replace("#1", userId);
        long size = zSetOperations.size(interestKey);
        if (size > 15) {
            zSetOperations.removeRange(interestKey, 0, size - 15);
        }
        Random random = new Random();
        int length = keywords.length;
        int num = 1 + random.nextInt(length);
        for (int i = 0; i < length && i < num; i++) {
            zSetOperations.incrementScore(interestKey, keywords[i], 0.1 + (length - i) * random.nextDouble());
        }
        return discussion;
    }

    @Override
    public void resetKeyword(DiscussionEntity discussion) {
        List<String> keywords = extractKeywords(discussion.getContent());
        String keywordsStr = Strings.join(keywords, ",");
        this.baseMapper.updateKeywords(discussion.getId(), keywordsStr);
    }

    private List<String> extractKeywords(String content) {
        Pattern pattern = Pattern.compile("<p.*?>([^<>]*?)</p>");
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            if (matcher.group(1).length() > 0)
                sb.append(matcher.group(1))
                        .append("\n");
        }
        return keywordUtil.getKeywords(sb.toString(), 5);
    }

    private String extractImage(String content) {
        Pattern pattern = Pattern.compile("<img.*src=\"(.*?)\".*>");
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find())
            return "";
        return matcher.group(1);
    }
}
