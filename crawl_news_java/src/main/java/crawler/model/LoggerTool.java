package crawler.model;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.File;
import org.apache.log4j.Logger;

public class LoggerTool {
    private static Logger logger;
    private static String filepath;
    public LoggerTool(String path){      //path:日誌儲存路徑
        filepath=path;
        new File(filepath).mkdir();  //建立資料夾
    }
    public static void infoMsg(String filename,String msg){       //filename:生成日誌的檔名  msg:日誌資訊
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
    }
    public Logger getLogger() {
        return logger;
    }
}
