package crawler;

import java.io.IOException;
import java.text.ParseException;

import crawler.model.LoggerTool;
import crawler.model.clearOverCountNews;
import crawler.model.crontabCrawlNews;
//TODO 待優化
public class Quartz {
    public static void main(String[] args) {
        //args[0] == "crawl-news" 執行爬新聞
        //args[0] == "clear-news" 清除新聞
        if(args[0].equals("crawl-news")){
            try {
                crontabCrawlNews crontabCrawlNews = new crontabCrawlNews();
                crontabCrawlNews.excute();
            } catch (IOException e) {
                e.printStackTrace();
                LoggerTool.infoMsg("excutError","Handle IOException Error: " + e);
            } catch (Exception e) {
                e.printStackTrace();
                LoggerTool.infoMsg("excutError","Handle Exception Error: " + e);
            }
        }else if(args[0].equals("clear-news")){
            try {
                LoggerTool.deleteExpiredLog();
                clearOverCountNews clearOverCountNews = new clearOverCountNews(20);
                String result = clearOverCountNews.excute();
                LoggerTool.infoMsg("excutClearDBLogSuccess",result);
            } catch (ParseException e) {
                e.printStackTrace();
                LoggerTool.infoMsg("excutClearDBLogError","Handle ParseException Error: " + e);
            }
        }
    }
}
