package crawler;

import java.text.ParseException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import crawler.model.LoggerTool;
import crawler.model.clearOverCountNews;

//TODO 待優化
public class clearDBLogSchedule implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        // 從context中獲取屬性
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        JobKey key = jobDetail.getKey();
        String description = jobDetail.getDescription();

        String excuteDate = jobDataMap.getString("excuteDate");
        String excuteTime = jobDataMap.getString("excuteTime");
        new LoggerTool("log");
        try {
            LoggerTool.deleteExpiredLog();
            clearOverCountNews clearOverCountNews = new clearOverCountNews(20);
            String result = clearOverCountNews.excute();
            LoggerTool.infoMsg("excutClearDBLogSuccess",result);
        } catch (ParseException e) {
            e.printStackTrace();
            LoggerTool.infoMsg("excutClearDBLogError","Handle ParseException Error: " + e);
        }
        System.out.println("key:"+key+",description:"+description+",excuteDate:"+ excuteDate+",excuteTime:"+ excuteTime);
    }
}
