package crawler.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.imageio.stream.FileImageOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawler.components.filterHtml;
import crawler.config.Config;
import okhttp3.ConnectionPool;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public List<Map<String, String>> getList(String urlName,String date) throws ParseException ,Exception{
        this.urlName = urlName;
        crawlConfig configList = Config.CONFIG_LIST.get(urlName);

        if (configList == null) {
            return null;
        }

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).cookieJar(this.cookieJar)
                .connectionPool(new ConnectionPool(32,10,TimeUnit.MINUTES)).build();

        String body = PostData_Request(configList.getUrl(),"GET",configList.getTitleHeader(),new HashMap<>());
        String response = this.getPostData_Response_String(body);
        if(response != null && !response.isEmpty()){
            List<Map<String, String>> parseResult = parseBody(response,configList,date);
            return parseResult;
        }else{
            LoggerTool.infoMsg("excutError","url:"+configList.getUrl()+" getPostData_Response_String_getList: " + body);
            return new ArrayList<>();
        }
    }

    private Response postHttpRequest(String requestUrl, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(requestUrl).post(requestBody).build();
        Response response = null;
        Integer errorTime = 0;
        while (response == null && errorTime <= 7) {
            try {
                response = this.okHttpClient.newCall(request).execute();
            }catch (SocketTimeoutException te){
                response = null;
                if(errorTime==7)
                    LoggerTool.infoMsg("excutError","Handle connectTimeout Error: " + te);
            }
            errorTime++;
        }
        return response;
    }

    private String PostData_Request(String url, String method, Map<String,String> headers, Map<String,String> body) throws IOException {
        String serverURL = get_PostData_Request_URL();
        Map<String,String> array_jumppost= new HashMap(){{
            put("uri",url);
            put("method",method);
            put("headers", new JSONObject(headers));
            put("body", new JSONObject(body));
        }};
        Response response = postHttpRequest(serverURL, new JSONObject(array_jumppost).toString());
        return response.body().string();
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
        
        body = filterHtml.Html2Text(body);
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
                    Map<String, String> CrawlList = getCrawlList(contentLink,configList,titleImg,CISelector,titleName);
                    if(!CrawlList.isEmpty()){
                        result.add(
                            CrawlList
                        );
                    }
                }
            }
        }else{
            Document titleDoc = Jsoup.parse(body);
            Elements titleList = titleDoc.select(TITSelector);
            for (Element tiElement: titleList) {
                String titleImg = (TISelector==null||TISelector.isEmpty())?"":tiElement.select(TISelector).attr("src");
                String titleName = (TNSelector==null||TNSelector.isEmpty())?"":tiElement.select(TNSelector).text();
                String contentLink = (CLSelector==null||CLSelector.isEmpty())?"":tiElement.select(CLSelector).attr("href");
                String DateLimit = tiElement.select(TDSelector).text();
                
                // https://sports.sina.com.cn/ 過濾新浪開獎新聞
                // if(contentLink!="" && DateLimit.contains(date)&&!contentLink.contains("sports.sina.com.cn")){
                if(contentLink!="" && DateLimit.contains(date)){
                    Map<String, String> CrawlList = getCrawlList(contentLink,configList,titleImg,CISelector,titleName);
                    if(!CrawlList.isEmpty()){
                        result.add(
                            CrawlList
                        );
                    }
                }
            }
        }
        return result;
    }

    private Map<String, String> getCrawlList(String contentLink,crawlConfig configList,String titleImg,String CISelector,String titleName) throws IOException,Exception{
        Map<String, String> result = new HashMap<>();
        String response = PostData_Request(contentLink,"GET",configList.getContentHeader(),new HashMap<>());
        String contentBody = this.getPostData_Response_String(response);
        if(contentBody != null && !contentBody.isEmpty()){
            Document contentDoc = Jsoup.parse(contentBody);
            Elements contentList = contentDoc.select(configList.getContentItem());
            // String titleImgBase64 = (titleImg==""||titleImg.contains("n.sinaimg.cn"))?"":downloadImage(titleImg);
            String titleImgBase64 = (titleImg==null||titleImg.isEmpty())?"":downloadImage(titleImg);
            MySQL MySQL = new MySQL();
            Integer titleImgId = -1;
            if(titleImgBase64!=null&&!titleImgBase64.isEmpty()){
                titleImgId = MySQL.update("insert into news_title_image(image_blob) values('"+titleImgBase64+"')");
            }
            String titleImgRoute = titleImgId==-1?"":Integer.toString(titleImgId);
            this.contnetText = this.contentFilter(contentList.html());
            String contentImgRoute = getContentImgUrls(contentList,CISelector,contentLink);
            result = new HashMap(){{
                put("titleImg", titleImgRoute);
                put("titleName", titleName);
                put("contentLink", contentLink);
                put("contentText", contnetText);
                put("contentImg", contentImgRoute);
            }};
            return result;
        }else{
            LoggerTool.infoMsg("excutError","url:"+contentLink+" getPostData_Response_String_getCrawlList: " + response);
            return result;
        }
    }

    private String contentFilter(String html){
        html = filterHtml.Html2Text(html);
        Document contentDoc = Jsoup.parse(html);
        switch (this.urlName){
            case "tags":
                contentDoc.select(".article-notice").remove();
                contentDoc.select(".otherContent_01").remove();
                contentDoc.select(".article-video").remove();
                contentDoc.select(".news_weixin_ercode").remove();
                contentDoc.select(".appendQr_wrap").remove();
                html = contentDoc.html();
                break;
        }
        return html;
    }

    private JSONArray getJsonArr(String body, String selector){
        selector = selector.replace("(Arr)", "");
        if(!isJson(body))
            return new JSONArray();
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
        try{
            selector = selector.replace("(Str)", "");
            if(!isJson(body))
                return "";
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
        }catch(JSONException e){
            System.out.println(e);
            return "";
        }
    }

    private String getContentImgUrls(Elements contentList,String CISelector,String contentUrl) throws Exception{
        String contentImgRouteT = "";
        Integer cieindex = 0;
        for (Element ciElement: contentList.select(CISelector)){
            String imgSrc = ciElement.attr("src");
            // if (imgSrc==""||imgSrc.contains("gif")||imgSrc.contains("n.sinaimg.cn")){
            if (imgSrc==null||imgSrc.isEmpty()||imgSrc.contains("gif")){
                this.contnetText = this.contnetText.replaceAll("<img (.*?)src=\""+ciElement.attr("src")+"\"(.*?)>", "");
                continue;
            }

            if(this.urlName=="tags"&&!imgSrc.contains("http")){
                imgSrc = "https:"+imgSrc;
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
            
            String contentImgBase64 = (imgSrc==null||imgSrc.isEmpty())?"":downloadImage(imgSrc);
            MySQL MySQL = new MySQL();
            Integer contentId = -1;
            if(contentImgBase64!=null&&!contentImgBase64.isEmpty()){
                contentId = MySQL.update("insert into news_content_image(image_blob) values('"+contentImgBase64+"')");
            }
            if(contentId!=-1){
                this.contnetText = this.contnetText.replaceAll("<img (.*?)src=\""+ciElement.attr("src")+"\"(.*?)>", "^[%"+contentId+"%]^");
                if(cieindex!=0)
                    contentImgRouteT+=",";
                contentImgRouteT+=Integer.toString(contentId);
                cieindex++;
            }else{
                this.contnetText = this.contnetText.replaceAll("<img (.*?)src=\""+ciElement.attr("src")+"\"(.*?)>", "");
            }
        }
        return contentImgRouteT;
    }

    private String downloadImage(String url) throws IOException {
        if(url.contains("bmp"))
            return "";
        int randomNo=(int)(Math.random()*1000000);
        String filename=url.substring(url.lastIndexOf("/")+1,url.length());//獲取服務器上圖片的名稱
        filename = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+randomNo+filename;//時間+隨機數防止重復
        String response = PostData_Request(url,"GET",new HashMap<>(),new HashMap<>());
        String imgBase64 = this.getPostData_Response_String(response);
        if(imgBase64 != null && !imgBase64.isEmpty()){
            return imgBase64;
        }else{
            LoggerTool.infoMsg("excutError","url:"+url+" getPostData_Response_String_downloadImage: " + response);
            return "";
        }
//        byte[] imgBytes = response.body().bytes();
//        String imgBase64 = Base64.getEncoder().encodeToString(imgBytes);
//        return imgBase64;
    }

    private void byte2image(byte[] data,String path) {
        if (data.length < 3 || path.equals("")) return;//判断输入的byte是否为空
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));//打开输入流
            imageOutput.write(data, 0, data.length);//将byte写入硬盘
            imageOutput.close();
        } catch (Exception ex) {
            LoggerTool.infoMsg("excutError","Handle Exception Error: " + ex);
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

    
    private boolean isJson(String value) {
		try {
			new JSONObject(value);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}


    private String get_PostData_Request_URL() {
        Properties properties = new Properties();
        String configFile = "Config.properties";
        try {
            properties.load(new FileInputStream(configFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            LoggerTool.infoMsg("excutError","Handle FileNotFoundException Error: " + ex);
            return null;
        } catch (IOException ex) {
            LoggerTool.infoMsg("excutError","Handle IOException Error: " + ex);
            ex.printStackTrace();
            return null;
        }

        return properties.getProperty("postdata_request_url", "");
    }

    private String getPostData_Response_String(String body){
        try {
            String response = new JSONObject(body).getString("response");
            if (body == "" && response==null) {
                return "";
            }
            return response;
        }catch(JSONException e){
            System.out.println(e);
            return "";
        }
    }
}

