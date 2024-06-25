package com.iherb.user.entity.vo;

import com.iherb.user.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo implements Serializable {

    private Long id;

    private String userId;

    private String nickname;

    private String avatar;

    private Integer floor;

    private String content;

    private Date createTime;

}
