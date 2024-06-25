package com.iherb.user.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVo implements Serializable {

    private Long id;

    private String title;

    private String content;

    private String userId;

    private String nickname;

    private String avatar;

    private String keywords;

    private Integer viewCount;

    private Integer commentCount;

    private Date createTime;

}
