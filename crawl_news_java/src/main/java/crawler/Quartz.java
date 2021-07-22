package crawler;

import crawler.model.LoggerTool;
import crawler.model.clearOverCountNews;
import crawler.model.crontabCrawlNews;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//TODO 待優化
public class Quartz {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = sdf.format(new Date());
        //args[0] == "crawl-news" 執行爬新聞
        //args[0] == "clear-news" 清除新聞
        if(args[0].equals("crawl-news")){
            try {
                crontabCrawlNews crontabCrawlNews = new crontabCrawlNews();
                crontabCrawlNews.excute();
            } catch (IOException e) {
                e.printStackTrace();
                LoggerTool.infoMsg(nowTime+" excutError","處理 IOException 錯誤: " + e);
            } catch (Exception e) {
                e.printStackTrace();
                LoggerTool.infoMsg(nowTime+" excutError","處理 Exception 錯誤: " + e);
            }
        }else if(args[0].equals("clear-news")){
            try {
                LoggerTool.deleteExpiredLog();
                clearOverCountNews clearOverCountNews = new clearOverCountNews(20);
                String result = clearOverCountNews.excute();
                LoggerTool.infoMsg(nowTime+" excutClearDBLogSuccess",result);
            } catch (ParseException e) {
                e.printStackTrace();
                LoggerTool.infoMsg(nowTime+" excutClearDBLogError","處理 ParseException 錯誤: " + e);
            }
        }
    }
}
