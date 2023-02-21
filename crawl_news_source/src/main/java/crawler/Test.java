package crawler;

import crawler.model.crontabCrawlNews;
public class Test {
    public static void main(String[] args) throws Exception {
       crontabCrawlNews crontabCrawlNews = new crontabCrawlNews();
       crontabCrawlNews.excute();
//        clearOverCountNews clearOverCountNews = new clearOverCountNews(20);
//        String result = clearOverCountNews.excute();
//        new LoggerTool("log");
//        LoggerTool.deleteExpiredLog();
    }
}
