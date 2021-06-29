package crawler;

import org.jsoup.select.Elements;
import crawler.model.*;
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

public class Reader {
    private OkHttpClient okHttpClient;
    private final Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private final CookieJar cookieJar;
    private String contnetText;
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
    }
//    List<Content>
    public List<Map<String, String>> getList(String urlName,String date) throws IOException, ParseException ,Exception{
        crawlConfig configList = Config.CONFIG_LIST.get(urlName);

        if (configList == null) {
            return null;
        }

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).cookieJar(this.cookieJar)
                .connectionPool(new ConnectionPool(32,5,TimeUnit.MINUTES)).build();

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
    private List<Map<String, String>> parseBody(String body,crawlConfig configList,String date) throws IOException, ParseException,Exception{
        List<Map<String, String>> result = new ArrayList<>();
        Document titleDoc = Jsoup.parse(body);
        Elements titleList = titleDoc.select(configList.getTitleItem());
        String TISelector = configList.getTitleImgSelector();
        String TNSelector= configList.getTitleNameSelector();
        String TDSelector = configList.getTitleDateSelector();
        String CLSelector= configList.getContentLinkSelector();
        String CISelector= configList.getContentImgSelector();

        for (Element tiElement: titleList) {
            String titleImg = TISelector==""?"":tiElement.select(TISelector).attr("src");
            String titleName = TNSelector==""?"":tiElement.select(TNSelector).text();
            String contentLink = CLSelector==""?"":tiElement.select(CLSelector).attr("href");
            String DateLimit = tiElement.select(TDSelector).text();
            if(contentLink!="" && DateLimit.contains(date)){
                Response contentResponse = this.getHttpRequest(contentLink);
                String contentBody = contentResponse.body().string();
                Document contentDoc = Jsoup.parse(contentBody);
                Elements contentList = contentDoc.select(configList.getContentItem());
                String titleImgBase64 = downloadImage(titleImg);
                MySQL MySQL = new MySQL();
                Integer titleImgId = MySQL.update("insert into news_title_image(image_blob) values('"+titleImgBase64+"')");
                String titleImgRoute = titleImgId==-1?"":Integer.toString(titleImgId);
                this.contnetText = contentList.html();
                String contentImgRoute = getContentImgUrls(contentList,CISelector);
                result.add(new HashMap<>(){{
                    put("titleImg", titleImgRoute);
                    put("titleName", titleName);
                    put("contentLink", contentLink);
                    put("contentText", contnetText);
                    put("contentImg", contentImgRoute);
                }});
            }
        }

        return result;
    }

    private String getContentImgUrls(Elements contentList,String CISelector) throws Exception{
        String contentImgRouteT = "";
        Integer cieindex = 0;
        for (Element ciElement: contentList.select(CISelector)){
            String imgSrc = ciElement.attr("src");
            String contentImgBase64 = downloadImage(imgSrc);
            MySQL MySQL = new MySQL();
            Integer contentId = MySQL.update("insert into news_content_image(image_blob) values('"+contentImgBase64+"')");
            if(contentId!=-1)
                this.contnetText = this.contnetText.replaceAll("<img src=\""+imgSrc+"\"(.*?)>", "^[%"+contentId+"%]^");
            contentImgRouteT+=Integer.toString(contentId);
            if(cieindex!=0)
                contentImgRouteT+=",";
            cieindex++;
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
}

