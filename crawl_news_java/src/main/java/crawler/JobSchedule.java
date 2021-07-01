package crawler;

import crawler.model.LoggerTool;
import crawler.model.crontabCrawlNews;
import org.quartz.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//TODO 待優化
public class JobSchedule implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowTime = sdf.format(new Date());

        // 從context中獲取屬性
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        JobKey key = jobDetail.getKey();
        String description = jobDetail.getDescription();

        String excuteDate = jobDataMap.getString("excuteDate");
        String excuteTime = jobDataMap.getString("excuteTime");
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
        System.out.println("key:"+key+",description:"+description+",excuteDate:"+ excuteDate+",excuteTime:"+ excuteTime);
    }
}
