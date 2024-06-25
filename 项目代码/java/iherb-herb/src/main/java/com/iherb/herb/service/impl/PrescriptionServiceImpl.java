package com.iherb.herb.service.impl;

import com.alibaba.fastjson.JSON;
import com.iherb.common.constant.EsConstant;
import com.iherb.herb.entity.es.Prescription;
import com.iherb.herb.entity.vo.PrescriptionVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.PrescriptionDao;
import com.iherb.herb.entity.PrescriptionEntity;
import com.iherb.herb.service.PrescriptionService;


@Service("prescriptionService")
@Slf4j
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionDao, PrescriptionEntity> implements PrescriptionService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<PrescriptionEntity> page = this.page(
                new Query<PrescriptionEntity>().getPage(curPage, limit),
                new QueryWrapper<PrescriptionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<PrescriptionVo> getPrescriptions() {
        List<PrescriptionEntity> list = list();
        List<PrescriptionVo> result = new ArrayList<>();
        list.forEach(p -> {
            PrescriptionVo vo = new PrescriptionVo();
            String[] arr = p.getIngredient().replaceAll("_;", ";").split(";");
            List<PrescriptionVo.Ingredient> ingredients = new ArrayList<>();
            for (String ingredient : arr) {
                if (!ingredient.contains("_") && ingredient.length() > 7) {
                    continue;
                }
                String[] source = ingredient.split("_");
                ingredients.add(new PrescriptionVo.Ingredient(source[0], source.length > 1 ? source[1] : ""));
            }
            if (!ingredients.isEmpty()) {
                vo.setIngredients(ingredients);
                BeanUtils.copyProperties(p, vo);
                result.add(vo);
            }
        });
        result.forEach(System.out::println);
        return result;
    }

    @Override
    public void addPrescriptions() {
        List<PrescriptionEntity> list = list();
        BulkRequest bulkRequest = new BulkRequest(EsConstant.PRESCRIPTION_INDEX);
        list.forEach(p -> {
            IndexRequest request = new IndexRequest();
            Prescription prescription = new Prescription();
            BeanUtils.copyProperties(p, prescription);
            Pattern pattern = Pattern.compile("#\\{(.*?)\\}");
            Matcher matcher = pattern.matcher(p.getIngredient());
            List<String> ingredients = new ArrayList<>();
            while (matcher.find()) {
                ingredients.add(matcher.group(1));
            }
            prescription.setIngredients(ingredients);
            request.source(JSON.toJSONString(prescription), XContentType.JSON);
            bulkRequest.add(request);
        });
        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                throw new RuntimeException("有错误");
            }
            log.info("成功添加{}条记录", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Prescription> searchPrescriptions(List<String> herbNames) {
        SearchRequest request = new SearchRequest(EsConstant.PRESCRIPTION_INDEX);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<Prescription> list = new ArrayList<>();
        for (String herbName : herbNames) {
            boolQuery.should(QueryBuilders.termQuery("ingredients", herbName));
        }
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                .from(0)
                .size(10)
                .query(boolQuery);
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            response.getHits().forEach(hit -> {
                System.out.println(hit.getSourceAsString());
                Prescription p = JSON.parseObject(hit.getSourceAsString(), Prescription.class);
                p.setIngredient(p.getIngredient().replaceAll("#\\{", "")
                        .replaceAll("}", ""));
                list.add(p);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
