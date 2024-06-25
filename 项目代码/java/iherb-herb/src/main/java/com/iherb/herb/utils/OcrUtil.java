package com.iherb.herb.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OcrUtil {

    public static String API_KEY;
    public static String API_SECRET;
    public static String ACCESS_TOKEN;

    @Value("${baidu.api-key}")
    public void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }
    @Value("${baidu.api-secret}")
    public void setApiSecret(String apiSecret) {
        API_SECRET = apiSecret;
    }

    @PostConstruct
    public void init() {
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + API_KEY
                + "&client_secret=" + API_SECRET;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            JSONObject jsonObject = JSON.parseObject(result);
            ACCESS_TOKEN = jsonObject.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static String generalBasic(MultipartFile file) {
        try {
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
            byte[] bytes = file.getBytes();
            String imgStr = Base64Util.encode(bytes);
            String param = "image=" + URLEncoder.encode(imgStr, "UTF-8");
            String result = HttpUtil.post(url, ACCESS_TOKEN, param);
            System.out.println(result);
            Pattern pattern = Pattern.compile("\"words\":\"(.*?)\"");
            Matcher matcher = pattern.matcher(result);
            StringBuilder stringBuilder = new StringBuilder();
            while (matcher.find()) {
                System.out.println(matcher.group(1));
                stringBuilder.append(matcher.group(1));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
