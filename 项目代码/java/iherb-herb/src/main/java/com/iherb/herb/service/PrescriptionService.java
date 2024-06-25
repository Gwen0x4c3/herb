package com.iherb.herb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.herb.entity.PrescriptionEntity;
import com.iherb.herb.entity.es.Prescription;
import com.iherb.herb.entity.vo.PrescriptionVo;

import java.util.List;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-18 11:59:34
 */
public interface PrescriptionService extends IService<PrescriptionEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);

    List<PrescriptionVo> getPrescriptions();

    void addPrescriptions();

    List<Prescription> searchPrescriptions(List<String> herbNames);
}

