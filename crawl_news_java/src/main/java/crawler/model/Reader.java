package crawler.model;

import org.json.JSONArray;
import org.jsoup.select.Elements;
import crawler.config.*;

import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import org.json.JSONObject;
//TODO 待優化
public class Reader {
    private OkHttpClient okHttpClient;
    private final Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private final CookieJar cookieJar;
    private String contnetText;
    private String urlName;
    public Reader() throws IOException {
        /* 初始化 */
        this.cookieStore = new HashMap<>();
        this.cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                List<Cookie> cookies = cookieStore.getOrDefault(httpUrl.host(), new ArrayList<>());
                cookies.addAll(list);
                cookieStore.put(httpUrl.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookieStore.getOrDefault(httpUrl.host(), new ArrayList<>());
            }
        };
        this.contnetText = "";
        this.urlName = "";
    }
//    List<Content>
    public List<Map<String, String>> getList(String urlName,String date) throws IOException, ParseException ,Exception{
        this.urlName = urlName;
        crawlConfig configList = Config.CONFIG_LIST.get(urlName);

        if (configList == null) {
            return null;
        }

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS).cookieJar(this.cookieJar)
                .connectionPool(new ConnectionPool(32,15,TimeUnit.MINUTES)).build();

        /* 獲得網站的初始 Cookie */
        Response response = this.getHttpRequest(configList.getUrl());
        if (response == null) {
            return null;
        }
        String body = response.body().string();

        /* 轉換 HTML 到 Article */
        List<Map<String, String>> parseResult = parseBody(body,configList,date);

        return parseResult;
    }

    private Response getHttpRequest(String requestUrl) throws IOException {
        Request request = new Request.Builder().url(requestUrl).get().build();
        Response response = null;
        Integer errorTime = 0;
        while (response == null && errorTime < 5) {
            try {
                response = this.okHttpClient.newCall(request).execute();
            }catch (SocketTimeoutException te){
                response = this.okHttpClient.newCall(request).execute();
            }
            errorTime++;
        }
        return response;
    }
    /* 解析標題文章列表 */
    private List<Map<String, String>> parseBody(String body,crawlConfig configList,String date) throws ParseException,Exception{
        List<Map<String, String>> result = new ArrayList<>();
        String TITSelector = configList.getTitleItem();
        String TISelector = configList.getTitleImgSelector();
        String TNSelector= configList.getTitleNameSelector();
        String TDSelector = configList.getTitleDateSelector();
        String CLSelector= configList.getContentLinkSelector();
        String CISelector= configList.getContentImgSelector();
        if(TITSelector.contains("[jsonOb]")){
            if(this.urlName=="xinhuanet")
                body = body.replace("(", "").replace(")", "");

            JSONArray jsonTitleList = this.getJsonArr(body,TITSelector);
            for (Object jsonObj:jsonTitleList){
                String titleItemStr = jsonObj.toString();
                String titleName = this.getJsonString(titleItemStr,TNSelector);
                String titleImg = TISelector.contains("(Arr)")?this.getJsonArr(titleItemStr,TISelector).get(0).toString():this.getJsonString(titleItemStr,TISelector);
                String contentLink = this.getJsonString(titleItemStr,CLSelector);
                String DateLimit = this.getJsonString(titleItemStr,TDSelector);
                if(contentLink!="" && DateLimit.contains(date)){
                    result = this.getCrawlList(contentLink,configList,titleImg,CISelector,titleName);
                }
            }
        }else{
            Document titleDoc = Jsoup.parse(body);
            Elements titleList = titleDoc.select(TITSelector);
            for (Element tiElement: titleList) {
                String titleImg = TISelector==""?"":tiElement.select(TISelector).attr("src");
                String titleName = TNSelector==""?"":tiElement.select(TNSelector).text();
                String contentLink = CLSelector==""?"":tiElement.select(CLSelector).attr("href");
                String DateLimit = tiElement.select(TDSelector).text();
                if(contentLink!="" && DateLimit.contains(date)){
                    result = this.getCrawlList(contentLink,configList,titleImg,CISelector,titleName);
                }
            }
        }

        return result;
    }

    private List<Map<String, String>> getCrawlList(String contentLink,crawlConfig configList,String titleImg,String CISelector,String titleName) throws IOException,Exception{
        List<Map<String, String>> result = new ArrayList<>();
        Response contentResponse = getHttpRequest(contentLink);
        String contentBody = contentResponse.body().string();
        Document contentDoc = Jsoup.parse(contentBody);
        Elements contentList = contentDoc.select(configList.getContentItem());
        String titleImgBase64 = titleImg==""?"":downloadImage(titleImg);
        MySQL MySQL = new MySQL();
        Integer titleImgId = MySQL.update("insert into news_title_image(image_blob) values('"+titleImgBase64+"')");
        String titleImgRoute = titleImgId==-1?"":Integer.toString(titleImgId);
        this.contnetText = this.htmlFilter(contentList.html());
        String contentImgRoute = getContentImgUrls(contentList,CISelector,contentLink);
        result.add(new HashMap<>(){{
            put("titleImg", titleImgRoute);
            put("titleName", titleName);
            put("contentLink", contentLink);
            put("contentText", contnetText);
            put("contentImg", contentImgRoute);
        }});
        return result;
    }

    private String htmlFilter(String html){
        html = html.replaceAll("/<script(.*?)</script>/i", "");
        html = html.replaceAll("/<style(.*?)/style>/i", "");
        html = html.replaceAll("/<frame(.*?)>/i", "");
        html = html.replaceAll("/<iframe(.*?)</iframe>/i", "");
        html = html.replaceAll("/<link(.*?)>/i", "");
        return html;
    }

    private JSONArray getJsonArr(String body, String selector){
        selector = selector.replace("(Arr)", "");
        JSONObject jsonObject = new JSONObject(body);
        JSONArray result = null;
        while (selector.contains("[jsonOb]")||selector.contains("[jsonArr]")){
            if(selector.contains("[jsonOb]")){
                String[] temArr = selector.split("\\[jsonOb\\]");
                jsonObject = jsonObject.getJSONObject(temArr[0]);
                selector = selector.replaceFirst(temArr[0]+"\\[jsonOb\\]", "");
            }else if(selector.contains("[jsonArr]")){
                String[] temArr2 = selector.split("\\[jsonArr\\]");
                result = jsonObject.getJSONArray(temArr2[0]);
                selector = selector.replaceFirst(temArr2[0]+"\\[jsonArr\\]", "");
            }
        }
        return result;
    }

    private String getJsonString(String body,String selector){
        selector = selector.replace("(Str)", "");
        JSONObject jsonObject = new JSONObject(body);
        String result = null;
        while (selector.contains("[jsonOb]")||selector.contains("[jsonStr]")){
            if(selector.contains("[jsonOb]")){
                String[] temArr = selector.split("\\[jsonOb\\]");
                jsonObject = jsonObject.getJSONObject(temArr[0]);
                selector = selector.replaceFirst(temArr[0]+"\\[jsonOb\\]", "");
            }else if(selector.contains("[jsonStr]")){
                String[] temArr2 = selector.split("\\[jsonStr\\]");
                result = jsonObject.getString(temArr2[0]);
                selector = selector.replaceFirst(temArr2[0]+"\\[jsonStr\\]", "");
            }
        }
        return result;
    }

    private String getContentImgUrls(Elements contentList,String CISelector,String contentUrl) throws Exception{
        String contentImgRouteT = "";
        Integer cieindex = 0;
        for (Element ciElement: contentList.select(CISelector)){
            String imgSrc = ciElement.attr("src");
            if(this.urlName=="tags"){
                imgSrc = "http:"+imgSrc;
            }else if(this.urlName=="xinhuanet"){
                String[] urlA = contentUrl.split("/");
                urlA = delete(urlA, urlA.length-1);
                String urlAStr = "";
                for (int i = 0; i < urlA.length; i++) {
                    if(i!=0)
                        urlAStr = urlAStr + "/";
                    urlAStr = urlAStr + urlA[i];
                }
                imgSrc = urlAStr+"/"+imgSrc;
            }
            String contentImgBase64 = downloadImage(imgSrc);
            MySQL MySQL = new MySQL();
            Integer contentId = MySQL.update("insert into news_content_image(image_blob) values('"+contentImgBase64+"')");
            if(contentId!=-1){
                this.contnetText = this.contnetText.replaceAll("<img src=\""+ciElement.attr("src")+"\"(.*?)>", "^[%"+contentId+"%]^");
                if(cieindex!=0)
                    contentImgRouteT+=",";
                contentImgRouteT+=Integer.toString(contentId);
                cieindex++;
            }
        }
        return contentImgRouteT;
    }

    private String downloadImage(String url) throws IOException {
        int randomNo=(int)(Math.random()*1000000);
        String filename=url.substring(url.lastIndexOf("/")+1,url.length());//獲取服務器上圖片的名稱
        filename = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+randomNo+filename;//時間+隨機數防止重復
        Response response = this.getHttpRequest(url);
        byte[] imgBytes = response.body().bytes();
        String imgBase64 = Base64.getEncoder().encodeToString(imgBytes);
        return imgBase64;
    }

    private void byte2image(byte[] data,String path) {
        if (data.length < 3 || path.equals("")) return;//判断输入的byte是否为空
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));//打开输入流
            imageOutput.write(data, 0, data.length);//将byte写入硬盘
            imageOutput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private String[] delete(String []n,Integer index) {
        Integer j=0;
        if(index<0||index>=n.length) {
            System.out.println("沒有對應的元素可刪除");
            return n;
        }
        String []b= new String[n.length - 1];
        for(int i=0;i<n.length;i++) {
            if(i==index)continue;
            b[j++]=n[i];
        }
        return b;
    }
}

