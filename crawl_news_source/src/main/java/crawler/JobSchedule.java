package crawler;

import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import crawler.model.LoggerTool;
import crawler.model.crontabCrawlNews;

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
            LoggerTool.infoMsg("excutError","Handle IOException Error: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerTool.infoMsg("excutError","Handle Exception Error: " + e);
        }
        System.out.println("key:"+key+",description:"+description+",excuteDate:"+ excuteDate+",excuteTime:"+ excuteTime);
    }
}
