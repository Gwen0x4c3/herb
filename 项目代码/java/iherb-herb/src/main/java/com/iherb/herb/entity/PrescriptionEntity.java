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
 * @date 2022-06-18 12:11:29
 */
@Data
@TableName("t_prescription")
public class PrescriptionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 药方id
	 */
	@TableId
	private Long id;
	/**
	 * 药方名
	 */
	private String name;
	/**
	 * 药方来源
	 */
	private String source;
	/**
	 * 药方配料
	 */
	private String ingredient;
	/**
	 * 药方主治
	 */
	private String symptom;
	/**
	 * 药方功效
	 */
	private String functions;
	/**
	 * 服用方法
	 */
	private String method;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date modifyTime;

}
