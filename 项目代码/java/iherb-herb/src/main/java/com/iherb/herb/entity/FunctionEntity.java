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
@TableName("t_function")
public class FunctionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 药效id
	 */
	@TableId
	private Long id;
	/**
	 * 父类id
	 */
	private Long parentId;
	/**
	 * 药效名称
	 */
	private String name;
	/**
	 * 具体功效
	 */
	private String detail;
	/**
	 * 主治病症
	 */
	private String symptom;

}
