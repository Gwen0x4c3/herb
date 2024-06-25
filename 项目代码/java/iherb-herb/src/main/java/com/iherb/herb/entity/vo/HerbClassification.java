package com.iherb.herb.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HerbClassification {

    private Long id;

    private String name;

    private String alias;

    private String image;

    private Double probability;
}
