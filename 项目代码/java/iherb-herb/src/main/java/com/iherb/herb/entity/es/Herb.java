package com.iherb.herb.entity.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Herb implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String[] alias;

    private String image;

    private String text;

}
