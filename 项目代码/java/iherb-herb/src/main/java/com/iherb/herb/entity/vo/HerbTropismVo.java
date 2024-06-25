package com.iherb.herb.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HerbTropismVo {

    private Long tropismId;

    private String tropismName;

    private Long herbId;

    private String herbName;

    private String image;
}
