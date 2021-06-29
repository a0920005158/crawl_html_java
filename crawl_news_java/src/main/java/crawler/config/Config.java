package crawler.config;

import crawler.model.*;
import java.util.*;

public class Config {
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

    }};
}
