package crawler.model;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class LoggerTool {
    private static Logger logger;
    private static String filepath = "log";
    public LoggerTool(String path){      //path:日誌儲存路徑
        LoggerTool.filepath=path; 
        new File(filepath).mkdir();  //建立資料夾
    }
    public static void infoMsg(String filename,String msg){       //filename:生成日誌的檔名  msg:日誌資訊
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String nowTime = sdf.format(new Date());
        filename = nowTime+"_"+filename;
        logger  =  Logger.getLogger("_"+filename);
        DailyRollingFileAppender appender=new DailyRollingFileAppender();
        appender.setFile(filepath+"/"+filename+".log");
        appender.setDatePattern("'.'yyyy-MM-dd");
        PatternLayout layout=new PatternLayout("%-4r %-5p %d{yyyy-MM-dd HH:mm:ssS}%c %m%n");
        appender.setLayout(layout);
        appender.setAppend(true);
        appender.activateOptions();
        logger.addAppender(appender);
        logger.setAdditivity(false);
        logger.setLevel((Level)Level.INFO);
        logger.info(msg);
        appender.close();
        System.out.println(msg);
    }
    public Logger getLogger() {
        return logger;
    }

    public static void deleteExpiredLog() throws ParseException {
        File folder1 = new File("log");
        String[] list1 = folder1.list();

        for (int i = 0; i < list1.length; i++) {
            SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
            String nowYear= new SimpleDateFormat( "yyyy" ).format(new Date());
            System.out.println(list1[i]);
            String fileDate = list1[i].split("_")[0];
            if(fileDate.equals(""))
                continue;
            Date createDate = sdf.parse(nowYear+"-"+fileDate);
            Calendar now = java.util.Calendar.getInstance();
            now.add(Calendar.DATE,-3); //3天前日期
            Date date2 = now.getTime();
            if(date2.getTime()-createDate.getTime()>0){
                File file = new File("log/"+list1[i]);
                System.out.println("刪除檔案: "+file.delete());
            }
        }

    }
}
