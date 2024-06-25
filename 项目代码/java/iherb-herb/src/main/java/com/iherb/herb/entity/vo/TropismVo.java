package com.iherb.herb.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iherb.herb.entity.TropismEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TropismVo implements Serializable {

    private Long id;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Herb> herbs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Herb {
        private Long id;
        private String name;
        private String image;
    }

}
