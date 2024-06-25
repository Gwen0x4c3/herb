package com.iherb.user.entity.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {

    private Long id;

    private String title;

    private List<String> keywords;

    private String cover;

    private Integer viewCount;

    private Date createTime;

}
