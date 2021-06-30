package crawler.config;

import crawler.model.*;
import java.util.*;

public class Config {
    //咸陽新聞
    public static final Map<String,crawlConfig> CONFIG_LIST = new HashMap<>(){{
        put("sxxynews",new crawlConfig(
                "http://www.sxxynews.com/jrlc/",
                "div.article-list ul[data-cid=47] .article-picture-item",
                "a",
                ".m-imagetext img",
                ".time",
                "a",
                ".article-detail-inner",
                "div p",
                "img"
            )
        );
        //新浪
        put("tags",new crawlConfig(
                        "http://tags.news.sina.com.cn/%E7%A6%8F%E5%BD%A9",
                        "ul.feeds_list li",
                        "h3.feeds_li_title a",
                        ".div.feeds_li_p img",
                        "div.feeds_li_meta .fl a",
                        "h3.feeds_li_title a",
                        "div.article",
                        "p",
                        "img"
                )
        );
        //新華網
        put("xinhuanet",new crawlConfig(
                        "http://qc.wa.news.cn/nodeart/list?nid=11166396&pgnum=1&cnt=16&attr=&tp=1&orderby=1",
                        "(Arr)data[jsonOb]list[jsonArr]",
                        "(Str)Title[jsonStr]",
                        "(Arr)allPics[jsonArr]",
                        "(Str)PubTime[jsonStr]",
                        "(Str)LinkUrl[jsonStr]",
                        "div#detail",
                        "p",
                        "img"
                )
        );
    }};
}
