package com.iherb.herb;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iherb.common.constant.EsConstant;
import com.iherb.herb.entity.*;
import com.iherb.herb.entity.es.ImageSearchResult;
import com.iherb.herb.entity.vo.AnalyzeVo;
import com.iherb.herb.entity.vo.FunctionVo;
import com.iherb.herb.entity.vo.TropismVo;
import com.iherb.herb.service.*;
import com.iherb.herb.utils.FeatureExtractUtils;
import com.iherb.herb.utils.HtmlParseUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringEscapeUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest
class IherbHerbApplicationTests {

    @Autowired
    private HerbService herbService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private FunctionHerbService functionHerbService;
    @Autowired
    private TropismService tropismService;
    @Autowired
    private TropismHerbService tropismHerbService;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
    }

    @Test
    public void addHerb() {
        HerbEntity herb = new HerbEntity();
        String json = HtmlParseUtil.parseHerbPage("http://www.zhongyoo.com/name/boluohui_533.html", herb);
        herbService.saveHerb(json, herb);
    }

    @Test
    public void addHerbsByName() {
        String letter = "d";
        String letters = "iouv";
        for (int i = 0; i < letters.length(); i++) {
            letter = String.valueOf(letters.charAt(i));
            System.out.println();
            System.out.println();
            System.out.println("***************正在爬取拼音为" + letter + "中药***************");
            List<String> urls = HtmlParseUtil.parseCategoryPage(letter);
            for (String url : urls) {
                HerbEntity herb = new HerbEntity();
                String json = HtmlParseUtil.parseHerbPage(url, herb);
                herbService.saveHerb(json, herb);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void functionHerb() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.zhongyoo.com/gx/huashiyao/");
        urls.add("http://www.zhongyoo.com/gx/wenliyao/");
        urls.add("http://www.zhongyoo.com/gx/liqiyao/");
        urls.add("http://www.zhongyoo.com/gx/quchongyao/");
        urls.add("http://www.zhongyoo.com/gx/xiaoshiyao/");
        urls.add("http://www.zhongyoo.com/gx/kaiqiaoyao/");
        urls.add("http://www.zhongyoo.com/gx/yongtuyao/");
        urls.add("http://www.zhongyoo.com/gx/shachongzhiyangyao/");
        urls.add("http://www.zhongyoo.com/gx/badushengjiyao/");
        for (String url : urls) {
            Map<String, List<String>> map = HtmlParseUtil.parseFunctionPage(url);
            map.forEach((functionName, herbNames) -> {
                FunctionEntity function = functionService.getOne(new QueryWrapper<FunctionEntity>()
                        .eq("name", functionName));
                List<HerbEntity> herbs = herbService.list(new QueryWrapper<HerbEntity>()
                        .select("id", "name")
                        .in("name", herbNames));
                System.out.println("功效：" + functionName + " " + function);
                herbs.forEach(System.out::println);
                List<FunctionHerbEntity> saveList = herbs.stream().map(herb -> {
                    FunctionHerbEntity functionHerb = new FunctionHerbEntity();
                    functionHerb.setFunctionId(function.getId());
                    functionHerb.setHerbId(herb.getId());
                    return functionHerb;
                }).collect(Collectors.toList());
                functionHerbService.saveBatch(saveList);
            });
        }

    }

    @Test
    public void tropismHerb() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.zhongyoo.com/guijing/weijing/");
        urls.add("http://www.zhongyoo.com/guijing/pijing/");
        urls.add("http://www.zhongyoo.com/guijing/dachangjing/");
        urls.add("http://www.zhongyoo.com/guijing/shenjing/");
        urls.add("http://www.zhongyoo.com/guijing/feijing/");
        urls.add("http://www.zhongyoo.com/guijing/xinjing/");
        urls.add("http://www.zhongyoo.com/guijing/ganjing/");
        urls.add("http://www.zhongyoo.com/guijing/bangguangjing/");
        urls.add("http://www.zhongyoo.com/guijing/danjing/" );
        urls.add("http://www.zhongyoo.com/guijing/sanjiaojing/");
        urls.add("http://www.zhongyoo.com/guijing/xiaochangjing/" );
        urls.add("http://www.zhongyoo.com/guijing/xinbaojing/");
        for (String url : urls) {
            Map<String, List<String>> map = HtmlParseUtil.parseTropismPage(url);
            map.forEach((tropismName, herbNames) -> {
                TropismEntity tropism = new TropismEntity();
                tropism.setName(tropismName);
                tropismService.save(tropism);
                List<HerbEntity> herbs = herbService.list(new QueryWrapper<HerbEntity>()
                        .select("id", "name")
                        .in("name", herbNames));
                List<TropismHerbEntity> saveList = new ArrayList<>();
                for (HerbEntity herb : herbs) {
                    TropismHerbEntity entity = new TropismHerbEntity();
                    entity.setHerbId(herb.getId());
                    entity.setTropismId(tropism.getId());
                    saveList.add(entity);
                }
                System.out.println("归经：" + tropism);
                System.out.println("关联：" + saveList);
                tropismHerbService.saveBatch(saveList);
            });
        }
    }

    @Test
    public void prescription() {
        List<PrescriptionEntity> list = new ArrayList<>();
        for (int i = 2; i <= 20; i++) {
            System.out.println("第" + i + "页");
            String url = "http://www.zhongyoo.com/fangji/page_" + i + ".html";
            try {
                Document document = Jsoup.parse(new URL(url), 3000);
                Elements lis = document.getElementsByClass("listbox").get(0).getElementsByTag("li");
                lis.forEach(li -> {
                    String s = li.getElementsByTag("a").get(0).attr("href");
                    PrescriptionEntity prescription = HtmlParseUtil.parsePrescriptionPage(s);
                    list.add(prescription);
                        System.out.println(prescription);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        prescriptionService.saveBatch(list);
    }

    @Test
    public void getPrescriptions() {
        System.out.println(prescriptionService.getPrescriptions());
    }

    @Test
    public void updatePrescription() {
        List<PrescriptionEntity> list = (List<PrescriptionEntity>) prescriptionService.queryPage(1L, 50L, "").getList();
        for (int i = 0; i < 50; i++) {
            PrescriptionEntity prescription = list.get(i);
            System.out.println(prescription.getIngredient());
            System.out.println(prescription.getIngredient().replaceAll("_;", ";"));
            System.out.println();
        }
    }

    @Test
    public void replaceName() {
        List<HerbEntity> herbs = herbService.list(new QueryWrapper<HerbEntity>()
                .select("name"));
        List<String> herbNames = herbs.stream().map(HerbEntity::getName).collect(Collectors.toList());
        Configuration config = DefaultConfig.getInstance();
        config.setUseSmart(true);
        Dictionary.initial(config);
        Dictionary.getSingleton().addWords(herbNames);
        List<PrescriptionEntity> prescriptions = prescriptionService.list();
        for (int i = 0; i < prescriptions.size(); i++) {
            PrescriptionEntity prescription = prescriptions.get(i);
            String ingredient = prescription.getIngredient().replaceAll("_", "").replaceAll(";", "、");
            StringReader reader = new StringReader(ingredient);
            IKSegmenter ikSegmenter = new IKSegmenter(reader, config);
            Lexeme lex;
            try {
                while ((lex = ikSegmenter.next()) != null) {
                    String word = lex.getLexemeText();
                    if (herbNames.contains(word)) {
                        ingredient = ingredient.replaceAll(word, "#{" + word + "}");
                    }
                }
                System.out.println("Ingredient:" + ingredient);
                prescription.setIngredient(ingredient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        prescriptionService.updateBatchById(prescriptions);
    }

    @Test
    public void addPrescriptions() {
        prescriptionService.addPrescriptions();
    }

    @Test
    public void addHerbs() {
        herbService.addHerbs();
    }

    @Test
    public void searchPrescriptions() {
        List<String> list = new ArrayList<>();
        list.add("白芷");
        list.add("白术");
        list.add("甘草");
        list.add("人参");
        prescriptionService.searchPrescriptions(list);
    }

    @Test
    public void searchFunctions() {
        Long[] ids = new Long[5];
        ids[0] = 8l;
        ids[1] = 9l;
        ids[2] = 10l;
        ids[3] = 11l;
        List<FunctionEntity> list = functionService.searchFunctions(ids);
        System.out.println(list);
    }

    @Test
    public void testAnalyze() {
        Long[] ids = new Long[5];
        ids[0] = 8l;
        ids[1] = 9l;
        ids[2] = 10l;
        ids[3] = 11l;
        AnalyzeVo analyzeVo = herbService.analyzeHerbs(ids);
        System.out.println(analyzeVo);
    }

    @Test
    public void testSearchHerb() {
        searchService.search("bai石", 0, 1, "");
    }

    @Test
    public void testSearchHints() {
        System.out.println(searchService.searchForHints("白", 1));
    }

    @Test
    public void testHerbFunction() {
        List<FunctionVo> functionVos = herbService.listByFunction();
        functionVos.forEach(System.out::println);
    }

    @Test
    public void testHerbTropism() {
        List<TropismVo> list = herbService.listByTropism();
        list.forEach(System.out::println);
    }

    @Test
    public void searchHerb() throws IOException {
        SearchRequest request = new SearchRequest("herb");
        SearchSourceBuilder.searchSource();
        request.source(SearchSourceBuilder.searchSource());
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

    }

    @Test
    public void addImageToIsi() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        //
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        //
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        String url = "https://image.so.com/i?q=川贝母 中药&src=tab_www&inact=0";
        BulkRequest bulkRequest = new BulkRequest(EsConstant.IMAGE_SEARCH_IMAGE_INDEX);
        try {
            Document doc = Jsoup.parse(new URL(url), 6000);
            Element script = doc.getElementById("initData");
            System.out.println(script);
            Pattern pattern = Pattern.compile("\\{.*?title\":\"([^\"]*?)\".*?thumb\":\"([^\"]*?)\".*?\\}");
            Matcher matcher = pattern.matcher(script.toString());
            int count = 0;
            while (matcher.find() && count++ <= 15) {
                String title = StringEscapeUtils.unescapeJava(matcher.group(1));
                String src = matcher.group(2).replace("\\", "");
                URL u = new URL(src); // 替换为实际的图片 URL
                InputStream inputStream = u.openStream();
                float[] vector = FeatureExtractUtils.getFeature(inputStream);
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("url", src);
                map.put("vector", vector);
                IndexRequest request = new IndexRequest(EsConstant.IMAGE_SEARCH_IMAGE_INDEX).source(map, XContentType.JSON);
                bulkRequest.add(request);
            }
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @SneakyThrows
    @Test
    public void searchImage() throws MalformedURLException {
        URL url = new URL("https://p2.ssl.qhimgs1.com/t0136447ec6984c9dd8.jpg");
        List<ImageSearchResult> imageSearchResults = herbService.imageSearch(url.openStream());
        imageSearchResults.forEach(System.out::println);
    }
}
