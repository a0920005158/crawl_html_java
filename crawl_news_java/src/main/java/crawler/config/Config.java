package crawler.config;

import crawler.model.*;
import java.util.*;

public class Config {
    //咸陽新聞
    public static final Map<String,crawlConfig> CONFIG_LIST = new HashMap(){{
        put("sxxynews",new crawlConfig(
                "http://www.sxxynews.com/jrlc/",
                "div.article-list ul[data-cid=47] .article-picture-item",
                "a",
                ".m-imagetext img",
                ".time",
                "a",
                ".article-detail-inner",
                "div p",
                "img",
                new HashMap(){{
                    put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
                }},
                new HashMap(){{
                    put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                }}
            )
        );
        //新浪
        put("tags",new crawlConfig(
                        "http://tags.news.sina.com.cn/%E7%A6%8F%E5%BD%A9",
                        "ul.feeds_list li",
                        "h3.feeds_li_title a",
                        "div.feeds_li_p img",
                        "div.feeds_li_meta .fl a",
                        "h3.feeds_li_title a",
                        "div.article,div.article-box",
                        "p",
                        "img",
                        new HashMap(){{
                            put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                            put("Accept-Language","zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
                            put("Upgrade-Insecure-Requests","1");
                            put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                        }},
                        new HashMap(){{
                            put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                            put("Accept-Language","zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
                            put("Upgrade-Insecure-Requests","1");
                            put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                        }}
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
                        "img",
                        new HashMap(){{
                            put("Accept-Language","zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
                            put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                        }},
                        new HashMap(){{
//                            put("Accept-Language","zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
//                            put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                        }}
                )
        );
    }};
}
