package crawler;

import org.jsoup.select.Elements;
import crawler.model.*;
import crawler.config.*;

import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public List<Map<String, String>> getList(String urlName) throws IOException, ParseException {
        Title Title = Config.TITLE_LIST.get(urlName);

        if (Title == null) {
            return null;
        }

        this.okHttpClient = new OkHttpClient.Builder().cookieJar(this.cookieJar).build();

        /* 獲得網站的初始 Cookie */
        Request request = new Request.Builder().url(Title.getUrl()).get().build();
        Response response = this.okHttpClient.newCall(request).execute();
        String body = response.body().string();

        /* 轉換 HTML 到 Article */
        List<Map<String, String>> titles = parseTitle(body,Title);
        List<Content> result = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");

        return titles;
    }

    /* 解析標題文章列表 */
    private List<Map<String, String>> parseTitle(String body,Title select) {
        List<Map<String, String>> result = new ArrayList<>();
        Document doc = Jsoup.parse(body);
        Elements selectList = doc.select(select.getTitleItem());
        String test = select.getTitleImgSelector();
        for (Element element: selectList) {
            String titleImg = select.getTitleImgSelector()==""?"":element.select(select.getTitleImgSelector()).attr("src");
            String titleName = element.select(select.getTitleNameSelector()).text();
            String contentLink = element.select(select.getContentLinkSelector()).attr("href");

            result.add(new HashMap<>(){{
                put("titleImg", titleImg);
                put("titleName", titleName);
                put("contentLink", contentLink);
            }});
        }

        return result;
    }
}

