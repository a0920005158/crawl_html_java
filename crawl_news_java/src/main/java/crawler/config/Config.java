package crawler.config;

import crawler.model.Title;
import java.util.*;

public class Config {
    public static final Map<String,Title> TITLE_LIST = new HashMap<>(){{
        put("ppt",new Title(
                "https://www.ptt.cc/bbs/PC_Shopping/index.html",
                ".title",
                "a",
                "",
                "a"
            )
        );
    }};
}
