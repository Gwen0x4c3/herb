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
@TableName("t_symptom")
public class SymptomEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 症状id
	 */
	@TableId
	private Long id;
	/**
	 * 症状名称
	 */
	private String name;
	/**
	 * 症状说明
	 */
	private String explain;

}
