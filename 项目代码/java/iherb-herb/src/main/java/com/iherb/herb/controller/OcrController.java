package com.iherb.herb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iherb.common.constant.ResultConstant;
import com.iherb.common.utils.R;
import com.iherb.herb.entity.HerbEntity;
import com.iherb.herb.service.HerbService;
import com.iherb.herb.utils.Base64Util;
import com.iherb.herb.utils.HttpUtil;
import com.iherb.herb.utils.OcrUtil;
import com.iherb.herb.utils.WordSegmentUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.List;

@Api(tags = "图像识别接口")
@RequestMapping("ocr")
@RestController
@CrossOrigin
public class OcrController {

    @Autowired
    private HerbService herbService;
    @Autowired
    private WordSegmentUtil wordSegmentUtil;

    @PostMapping("recognize")
    @ApiOperation("图像识别文字，返回中药{编号，中药名}的列表")
    public R recognize(MultipartFile file) {
        if (file.isEmpty()) {
            return R.error(ResultConstant.ERROR, "请重新上传图片");
        }
        String text = OcrUtil.generalBasic(file);
        List<String> names = wordSegmentUtil.extractHerbNames(text);
        List<HerbEntity> herbs = herbService.list(new QueryWrapper<HerbEntity>()
                .in("name", names)
                .select("id", "name"));
        return R.ok().data("list", herbs);
    }
}
