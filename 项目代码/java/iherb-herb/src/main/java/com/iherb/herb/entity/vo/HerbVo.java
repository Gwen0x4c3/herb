package com.iherb.herb.entity.vo;

import com.iherb.herb.entity.HerbEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class HerbVo extends HerbEntity implements Serializable {

    private String text;
}
