package crawler;

import org.jsoup.select.Elements;
import crawler.model.*;
import crawler.config.*;

import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.net.*;

public class Reader {
    private OkHttpClient okHttpClient;
    private final Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private final CookieJar cookieJar;

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
    }
//    List<Content>
    public List<Map<String, String>> getList(String urlName) throws IOException, ParseException ,Exception{
        Title Title = Config.TITLE_LIST.get(urlName);
        Content Content = Config.CONTENT_LIST.get(urlName);

        if (Title == null) {
            return null;
        }

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).cookieJar(this.cookieJar).build();

        /* 獲得網站的初始 Cookie */
        Request request = new Request.Builder().url(Title.getUrl()).get().build();
        Response response = null;
        Integer errorTime = 0;
        while (response == null && errorTime < 5) {
            response = this.okHttpClient.newCall(request).execute();
            errorTime++;
        }
        if (response == null) {
            return null;
        }
        String body = response.body().string();

        /* 轉換 HTML 到 Article */
        List<Map<String, String>> titles = parseTitle(body,Title,Content);
        List<Content> result = new ArrayList<>();

        return titles;
    }

    /* 解析標題文章列表 */
    private List<Map<String, String>> parseTitle(String body,Title select,Content contentSelect) throws IOException, ParseException,Exception{
        List<Map<String, String>> result = new ArrayList<>();
        Document titleDoc = Jsoup.parse(body);
        Elements titleList = titleDoc.select(select.getTitleItem());
        String TISelector = select.getTitleImgSelector();
        String TNSelector= select.getTitleNameSelector();
        String CLSelector= select.getContentLinkSelector();
        String CISelector= contentSelect.getContentImgSelector();

        for (Element tielement: titleList) {
            String titleImg = TISelector==""?"":tielement.select(TISelector).attr("src");
            String titleName = TNSelector==""?"":tielement.select(TNSelector).text();
            String contentLink = CLSelector==""?"":tielement.select(CLSelector).attr("href");

            if(contentLink!=""){
                Request contentRequest = new Request.Builder().url(contentLink).get().build();
                Response contentResponse = this.okHttpClient.newCall(contentRequest).execute();
                String contentBody = contentResponse.body().string();
                Document contentDoc = Jsoup.parse(contentBody);
                Elements contentList = contentDoc.select(contentSelect.getContentItem());
                String titleImgRoute = titleImg==""?"":downloadImage(titleImg,"imgDownload");

                String contentImgRoute = getContentImgUrls(contentList,CISelector);
                result.add(new HashMap<>(){{
                    put("titleImg", titleImgRoute);
                    put("titleName", titleName);
                    put("contentLink", contentLink);
                    put("contentText", contentList.text());
                    put("contentImg", contentImgRoute);
                }});
            }
        }

        return result;
    }

    private String getContentImgUrls(Elements contentList,String CISelector) throws Exception{
        String contentImgRouteT = "";
        Integer cieindex = 0;
        for (Element cielement: contentList.select(CISelector)){
            contentImgRouteT+=downloadImage(cielement.attr("src"),"imgDownload");
            if(cieindex!=0)
                contentImgRouteT+=",";
            cieindex++;
        }
        return contentImgRouteT;
    }

    private String downloadImage(String url, String imagePath) throws IOException {
        int randomNo=(int)(Math.random()*1000000);
        String filename=url.substring(url.lastIndexOf("/")+1,url.length());//獲取服務器上圖片的名稱
        filename = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+randomNo+filename;//時間+隨機數防止重復
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = this.okHttpClient.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        byte2image(bytes,imagePath+"\\"+filename);
        return filename;
    }

    private void byte2image(byte[] data,String path) {
        if (data.length < 3 || path.equals("")) return;//判断输入的byte是否为空
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));//打开输入流
            imageOutput.write(data, 0, data.length);//将byte写入硬盘
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }
}

