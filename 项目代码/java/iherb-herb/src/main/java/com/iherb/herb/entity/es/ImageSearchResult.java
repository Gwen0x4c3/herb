package com.iherb.herb.entity.es;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageSearchResult {

    private String title;

    private String url;

    private Float score;
}
