package crawler.config;

import crawler.model.*;
import java.util.*;

public class Config {
    public static final Map<String,Title> TITLE_LIST = new HashMap<>(){{
        put("sxxynews",new Title(
                "http://www.sxxynews.com/jrlc/",
                "div.article-list ul[data-cid=47] .article-picture-item",
                "a",
                ".m-imagetext img",
                "a"
            )
        );
    }};
    public static final Map<String,Content>CONTENT_LIST = new HashMap<>(){{
        put("sxxynews",new Content(
                        ".article-detail-inner",
                        "div p",
                        "img"
                )
        );
    }};
}
