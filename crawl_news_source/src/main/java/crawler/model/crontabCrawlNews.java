package crawler.model;

import crawler.config.Config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class crontabCrawlNews {
    private String nowTime = "";
    private String crawlNewsTime = "";
    public crontabCrawlNews() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.nowTime = sdf.format(new Date());
        this.crawlNewsTime = this.calculateTime(sdf.parse(this.nowTime),-1);
    }
    public void excute() throws Exception {
        try {
            MySQL MySQL = new MySQL();
            System.out.println("crawlNewsTime: " + this.crawlNewsTime);
            Reader reader = new Reader();

            Map<String,crawlConfig> configList = Config.CONFIG_LIST;
            for (Map.Entry<String, crawlConfig> config : configList.entrySet()) {
                List<String> sqlInsetSuccess = new ArrayList<>();
                List<String> sqlInsetError = new ArrayList<>();
                List<Map<String, String>> result = reader.getList(config.getKey(),this.crawlNewsTime);
                
                for (Map<String, String> data:result){
                    Integer insertID = MySQL.update("insert into news(title,content,image_title,image_content) values('"+
                            data.get("titleName")+"','"+
                            data.get("contentText")+"','"+
                            data.get("titleImg")+"','"+
                            data.get("contentImg")
                            +"')");
                    if(insertID==-1){
                        sqlInsetError.add(
                                "{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}"
                        );
                    }else{
                        sqlInsetSuccess.add(
                                "{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}"
                        );
                    }
                }
                new LoggerTool("log");
                LoggerTool.infoMsg("crawl_news",
                        "sqlInsetSuccess: "+sqlInsetSuccess+" ,sqlInsetError: "+sqlInsetError);
            }
        } catch (IOException e) {
            LoggerTool.infoMsg("excutError","Handle IOException Error: " + e);
            e.printStackTrace();
        } catch (ParseException e) {
            LoggerTool.infoMsg("excutError","Handle ParseException Error: " + e);
            e.printStackTrace();
        }
    }
    /**
     * 計算日期
     * @param time 計算的時間
     * @param day 計算的天數
     * @return
     * @throws Exception
    */
    public String calculateTime(Date time,int day) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.DATE, day);
        String newTime = sdf.format(cal.getTime());
        return newTime;
    }
}
