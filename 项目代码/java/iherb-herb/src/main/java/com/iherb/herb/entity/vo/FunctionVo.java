package com.iherb.herb.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iherb.herb.entity.FunctionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionVo extends FunctionEntity implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FunctionVo> children;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Herb {
        private Long id;
        private String name;
        private String image;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Herb> herbs;
}
