package com.iherb.herb.entity.vo;

import com.iherb.herb.entity.HerbEntity;
import com.iherb.herb.entity.PrescriptionEntity;
import com.iherb.herb.entity.es.Prescription;
import lombok.Data;

import java.util.List;

@Data
public class AnalyzeVo {

    private List<HerbEntity> herbs;

    private List<Prescription> prescriptions;

    private List<String> functions;

    private List<String> symptoms;

}
