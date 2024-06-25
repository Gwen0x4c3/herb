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
@TableName("t_tropism")
public class TropismEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 归经id
	 */
	@TableId
	private Long id;
	/**
	 * 归经名称
	 */
	private String name;

}
