package crawler;

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

            // 觸發Job執行
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "crawl-news")
                    .withSchedule(cronSchedule("0 15 18 ? * *"))
                    .build();


            // 告訴 quartz 使用trigger來排程job
            scheduler.scheduleJob(job, trigger);
            // 關閉，執行緒終止
            //scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}
