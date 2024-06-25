package com.iherb.herb.entity.es;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String source;

    private List<String> ingredients;

    private String ingredient;

    private String symptom;

    private String functions;

    private String method;

}
