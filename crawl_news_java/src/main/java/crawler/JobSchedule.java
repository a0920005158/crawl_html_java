package crawler;

import crawler.model.crontabCrawlNews;
import org.quartz.*;

import java.io.IOException;

//TODO 待優化
public class JobSchedule implements Job {
    @Override
    public void execute(JobExecutionContext context) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("key:"+key+",description:"+description+",excuteDate:"+ excuteDate+",excuteTime:"+ excuteTime);
    }
}
