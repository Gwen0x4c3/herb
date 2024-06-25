package com.iherb.herb.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iherb.common.constant.EsConstant;
import com.iherb.herb.entity.FunctionEntity;
import com.iherb.herb.entity.TextEntity;
import com.iherb.herb.entity.es.Herb;
import com.iherb.herb.entity.es.ImageSearchResult;
import com.iherb.herb.entity.es.Prescription;
import com.iherb.herb.entity.vo.*;
import com.iherb.herb.service.FunctionService;
import com.iherb.herb.service.PrescriptionService;
import com.iherb.herb.service.TextService;
import com.iherb.herb.utils.FeatureExtractUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.HerbDao;
import com.iherb.herb.entity.HerbEntity;
import com.iherb.herb.service.HerbService;


@Service("herbService")
public class HerbServiceImpl extends ServiceImpl<HerbDao, HerbEntity> implements HerbService {

    @Autowired
    private TextService textService;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private FunctionService functionService;

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<HerbEntity> page = this.page(
                new Query<HerbEntity>().getPage(curPage, limit),
                new QueryWrapper<HerbEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByFunctionId(Map<String, Object> params) {
        Page<HerbFunctionVo> page = new Page<>(Long.parseLong(params.get("page").toString()),
                Long.parseLong(params.get("limit").toString()));
        String id = null;
        if (params.containsKey("id") && params.get("id") != null) {
            id = params.get("id").toString();
        } else {
            this.baseMapper.queryPageByFunctionId(null, page);
            return new PageUtils(page);
        }
        if (params.containsKey("isParent") && Boolean.parseBoolean(params.get("isParent").toString())) {
            this.baseMapper.queryPageByFunctionParentId(Long.parseLong(id), page);
        } else {
            this.baseMapper.queryPageByFunctionId(Long.parseLong(id), page);
        }
        return new PageUtils(page);
    }

    @Override
    public HerbVo getHerb(Long id) {
        HerbEntity herb = this.getById(id);
        HerbVo vo = new HerbVo();
        BeanUtils.copyProperties(herb, vo);
        String content = textService.getById(herb.getTextId()).getContent();
        vo.setText(content);
        return vo;
    }

    @Override
    public void saveHerb(HerbVo vo) {
        HerbEntity herb = new HerbEntity();
        BeanUtils.copyProperties(vo, herb);
        herb.setCreateTime(new Date());
        herb.setModifyTime(new Date());

        TextEntity text = new TextEntity();
        text.setContent(vo.getText());
        textService.save(text);

        herb.setTextId(text.getId());
        this.save(herb);
    }

    @Override
    public void saveHerb(String json, HerbEntity herb) {
        TextEntity text = new TextEntity();
        text.setContent(json);
        textService.save(text);

        herb.setTextId(text.getId());
        this.save(herb);
    }

    @Override
    public AnalyzeVo analyzeHerbs(Long[] ids) {
        AnalyzeVo analyze = new AnalyzeVo();

        List<HerbEntity> herbs = list(new QueryWrapper<HerbEntity>()
                .select("id", "name")
                .in("id", ids));
        List<String> herbNames = new ArrayList<>();
        for (HerbEntity herb : herbs) {
            herbNames.add(herb.getName());
        }

        //根据配料搜索药方
        List<Prescription> prescriptions;
        CompletableFuture<List<Prescription>> prescriptionFuture = CompletableFuture.supplyAsync(
                () -> prescriptionService.searchPrescriptions(herbNames), executor);

        //根据中药搜索功效、症状
        List<String> functions = new ArrayList<>();
        List<String> symptoms = new ArrayList<>();
        CompletableFuture<Void> functionFuture = CompletableFuture.supplyAsync(
                () -> functionService.searchFunctions(ids), executor
        ).thenAcceptAsync(functionEntities -> {
            functionEntities.forEach(f -> {
                String[] functionArr = f.getDetail().split("、");
                String[] symptomArr = f.getSymptom().split("、");
                for (String s : functionArr) {
                    if (s.length() == 0)
                        continue;
                    if (functions.contains(s)) {
                        functions.remove(s);
                        functions.add(0, s);
                    } else {
                        functions.add(s);
                    }
                }
                for (String s : symptomArr) {
                    if (s.length() == 0)
                        continue;
                    if (symptoms.contains(s)) {
                        symptoms.remove(s);
                        symptoms.add(0, s);
                    } else {
                        symptoms.add(s);
                    }
                }
            });
        }, executor);

        try {
            CompletableFuture.allOf(prescriptionFuture, functionFuture).get();
            prescriptions = prescriptionFuture.get();
            analyze.setHerbs(herbs);
            analyze.setPrescriptions(prescriptions);
            analyze.setFunctions(functions);
            analyze.setSymptoms(symptoms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return analyze;
    }

    @Override
    public void addHerbs() {
        BulkRequest bulkRequest = new BulkRequest(EsConstant.HERB_INDEX);
        List<HerbVo> list = this.baseMapper.queryAllWithText();
        list.forEach(herb -> {
            Herb esHerb = new Herb();
            BeanUtils.copyProperties(herb, esHerb);
            String[] alias = herb.getAlias().split(",");
            esHerb.setAlias(alias);
            System.out.println(esHerb);
            bulkRequest.add(new IndexRequest()
                    .source(JSON.toJSONString(esHerb), XContentType.JSON));
        });
        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                throw new RuntimeException("出错了我擦");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FunctionVo> listByFunction() {
        List<FunctionVo> functionTree = functionService.listWithTree();
        List<HerbFunctionVo> source = baseMapper.listWithFunction(null);
        functionTree.forEach(parent -> {
            parent.getChildren().forEach(function -> { //遍历父功效下的每一个功效
                List<FunctionVo.Herb> herbs = source.stream().filter(vo -> Objects.equals(vo.getFunctionId(), function.getId()))
                        .map(vo -> { //遍历中药匹配到list中
                            return FunctionVo.Herb.builder()
                                    .id(vo.getHerbId())
                                    .name(vo.getHerbName())
                                    .image(vo.getImage()).build();
                        }).collect(Collectors.toList());
                function.setHerbs(herbs);
            });
        });
        return functionTree;
    }

    @Override
    public FunctionVo listByFunction(Long functionId) {
        FunctionVo parent = functionService.listWithTree(functionId);
        List<HerbFunctionVo> source = baseMapper.listWithFunction(functionId);
        System.out.println("functionId: " + functionId + ", source: " + source.size());
        parent.getChildren().forEach(function -> { //遍历父功效下的每一个功效
            List<FunctionVo.Herb> herbs = source.stream().filter(vo -> Objects.equals(vo.getFunctionId(), function.getId()))
                    .map(vo -> { //遍历中药匹配到list中
                        return FunctionVo.Herb.builder()
                                .id(vo.getHerbId())
                                .name(vo.getHerbName())
                                .image(vo.getImage()).build();
                    }).collect(Collectors.toList());
            function.setHerbs(herbs);
        });
        System.out.println("parent:" + parent);
        return parent;
    }

    @Override
    public List<TropismVo> listByTropism() {
        List<HerbTropismVo> source = baseMapper.listWithTropism(null);
        List<TropismVo> list = new ArrayList<>();
        long tropismId = source.get(0).getTropismId();
        TropismVo tropism = new TropismVo();
        tropism.setId(tropismId);
        tropism.setName(source.get(0).getTropismName());
        List<TropismVo.Herb> herbs = new ArrayList<>();
        for (HerbTropismVo vo : source) {
            if (tropismId != vo.getTropismId()) {
                tropism.setHerbs(herbs);
                list.add(tropism);
                tropismId = vo.getTropismId();
                herbs = new ArrayList<>();
                tropism = new TropismVo();
                tropism.setId(tropismId);
                tropism.setName(vo.getTropismName());
            }
            herbs.add(new TropismVo.Herb(vo.getHerbId(), vo.getHerbName(), vo.getImage()));
        }
        tropism.setHerbs(herbs);
        list.add(tropism);
        return list;
    }

    @Override
    public TropismVo listByTropism(Long tropismId) {
        List<HerbTropismVo> source = baseMapper.listWithTropism(tropismId);
        TropismVo tropismVo = new TropismVo();
        tropismVo.setId(tropismId);
        tropismVo.setName(source.get(0).getTropismName());
        List<TropismVo.Herb> herbs = source.stream().map(vo -> {
            TropismVo.Herb herb = new TropismVo.Herb();
            herb.setId(vo.getHerbId());
            herb.setName(vo.getHerbName());
            herb.setImage(vo.getImage());
            return herb;
        }).collect(Collectors.toList());
        tropismVo.setHerbs(herbs);
        return tropismVo;
    }

    @Override
    public List<ImageSearchResult> imageSearch(InputStream inputStream) {
        float[] vector = FeatureExtractUtils.getFeature(inputStream);
        SearchRequest searchRequest = new SearchRequest(EsConstant.IMAGE_SEARCH_IMAGE_INDEX);
        Script script = new Script(
                ScriptType.INLINE,
                "painless",
                "cosineSimilarity(params.queryVector, doc['vector'])",
                Collections.singletonMap("queryVector", vector));

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(
                QueryBuilders.matchAllQuery(),
                ScoreFunctionBuilders.scriptFunction(script));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(functionScoreQueryBuilder)
                .fetchSource(null, "vector")
                .size(100);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        List<ImageSearchResult> list = new ArrayList<>();
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            for (SearchHit hit : hits) {
                // 处理搜索结果
                System.out.println(hit.toString());
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                ImageSearchResult result = new ImageSearchResult((String) sourceMap.get("title"),(String) sourceMap.get("url"), hit.getScore());
                list.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return list;
    }

}
