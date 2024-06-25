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
 * @date 2022-08-20 17:51:57
 */
@Data
@TableName("t_discussion")
public class DiscussionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 讨论id
	 */
	@TableId
	private Long id;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 讨论正文
	 */
	private String content;
	/**
	 * 关键词
	 */
	private String keywords;
	/**
	 * 讨论发起者id
	 */
	private String userId;
	/**
	 * 浏览量
	 */
	private Integer viewCount;
	/**
	 * 评论数
	 */
	private Integer commentCount;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date modifyTime;

}
