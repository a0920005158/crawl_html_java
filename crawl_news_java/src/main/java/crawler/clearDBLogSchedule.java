package crawler;

import crawler.model.LoggerTool;
import crawler.model.clearOverCountNews;
import org.quartz.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//TODO 待優化
public class clearDBLogSchedule implements Job {
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
        new LoggerTool("log");
        try {
            LoggerTool.deleteExpiredLog();
            clearOverCountNews clearOverCountNews = new clearOverCountNews(20);
            String result = clearOverCountNews.excute();
            LoggerTool.infoMsg(nowTime+" excutClearDBLogSuccess",result);
        } catch (ParseException e) {
            e.printStackTrace();
            LoggerTool.infoMsg(nowTime+" excutClearDBLogError","處理 ParseException 錯誤: " + e);
        }
        System.out.println("key:"+key+",description:"+description+",excuteDate:"+ excuteDate+",excuteTime:"+ excuteTime);
    }
}
