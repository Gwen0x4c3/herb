package com.iherb.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:56
 */
@Data
@TableName("t_comment")
public class CommentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 评论id
	 */
	@TableId
	private Long id;
	/**
	 * 评论者id
	 */
	private String userId;
	/**
	 * 0-文章 1-讨论
	 */
	private Integer type;
	/**
	 * 文章或讨论id
	 */
	private Long targetId;
	/**
	 * 评论内容
	 */
	private String content;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
