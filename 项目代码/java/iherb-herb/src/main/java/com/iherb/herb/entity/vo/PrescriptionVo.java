package com.iherb.herb.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PrescriptionVo implements Serializable {

    private Long id;

    private String name;

    private String source;

    private List<Ingredient> ingredients;

    private String symptom;

    private String functions;

    private String method;

    private Date createTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredient {
        private String herb;
        private String detail;
    }
}
