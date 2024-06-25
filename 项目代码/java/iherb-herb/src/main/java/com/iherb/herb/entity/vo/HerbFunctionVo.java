package com.iherb.herb.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HerbFunctionVo implements Serializable {

    private Long herbId;

    private Long functionId;

    private String herbName;

    private String functionName;

    private String image;
}
