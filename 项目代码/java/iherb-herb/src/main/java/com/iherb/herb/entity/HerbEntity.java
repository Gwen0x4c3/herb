package com.iherb.herb.entity;

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
 * @date 2022-06-14 12:39:14
 */
@Data
@TableName("t_herb")
public class HerbEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 药草id
	 */
	@TableId
	private Long id;
	/**
	 * 药草名称
	 */
	private String name;
	/**
	 * 别名
	 */
	private String alias;
	/**
	 * 文本id
	 */
	private Long textId;
	/**
	 * 草药图片
	 */
	private String image;
	/**
	 * 添加时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date modifyTime;

}
