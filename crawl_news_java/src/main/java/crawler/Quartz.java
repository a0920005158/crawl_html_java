package crawler;

import crawler.model.LoggerTool;
import org.apache.log4j.BasicConfigurator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.CronScheduleBuilder.*;

import java.text.SimpleDateFormat;
import java.util.Date;
//TODO 待優化
public class Quartz {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowTime = sdf.format(new Date());
        BasicConfigurator.configure();
        try {
            // 從工廠中獲取Scheduler示例
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // 開始
            scheduler.start();

            // 定義Job，並將其繫結到HelloJob類中
            JobDetail job = JobBuilder.newJob(JobSchedule.class)
                    .withIdentity("job1", "crawl-news") // name 和 group
                    .usingJobData("excuteDate", nowTime) // 置入JobDataMap
                    .usingJobData("excuteTime", "1")
                    .withDescription("crawl-news")
                    .build();

            // 定義Job2，並將其繫結到HelloJob類中
            JobDetail job2 = JobBuilder.newJob(clearDBLogSchedule.class)
                    .withIdentity("job2", "clear-over-log-and-news") // name 和 group
                    .usingJobData("excuteDate", nowTime) // 置入JobDataMap
                    .usingJobData("excuteTime", "1")
                    .withDescription("clear-over-log-and-news")
                    .build();

            // 觸發Job執行
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "crawl-news")
                    .withSchedule(cronSchedule("0 0 7 ? * *"))
                    .build();

            // 觸發Job2執行
            Trigger trigger2 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger2", "clear-over-log-and-news")
                    .withSchedule(cronSchedule("0 30 7 ? * Thu"))
                    .build();

            // 告訴 quartz 使用trigger來排程job
            //爬取新聞排程
            scheduler.scheduleJob(job, trigger);
            //刪除過期log、db資料排程
            scheduler.scheduleJob(job2, trigger2);
            // 關閉，執行緒終止
            //scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
            LoggerTool.infoMsg(nowTime+" excutError","處理 SchedulerException 錯誤: " + se);
        }
    }
}
