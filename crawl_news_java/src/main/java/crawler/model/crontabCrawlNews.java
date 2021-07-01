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
//                System.out.println(config.getKey() + ":" + config.getValue());

                List<Map<String, String>> result = reader.getList(config.getKey(),this.crawlNewsTime);
                for (Map<String, String> data:result){
//                  System.out.println(data.get("titleImg"));
//                  System.out.println(data.get("contentLink"));
//                  System.out.println(data.get("contentText"));
//                  System.out.println(data.get("contentImg"));
                    Integer insertID = MySQL.update("insert into news(title,content,image_title,image_content) values('"+
                            data.get("titleName")+"','"+
                            data.get("contentText")+"','"+
                            data.get("titleImg")+"','"+
                            data.get("contentImg")
                            +"')");
                    String sqlInsetError = "sqlInsetError:{}";
                    String sqlInsetSuccess = "sqlInsetSuccess:{}";
                    if(insertID==-1){
                        System.out.println("sqlInsetError:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}");
                        sqlInsetError = "sqlInsetError:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}";
                    }else{
                        System.out.println("sqlInsetSuccess:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}");
                        sqlInsetSuccess = "sqlInsetSuccess:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}";
                    }
                    new LoggerTool("log");
                    LoggerTool.infoMsg(nowTime+" crawl_news",
                            sqlInsetSuccess+" ,"+sqlInsetError);
                }
            }
        } catch (IOException e) {
            LoggerTool.infoMsg(this.nowTime+" excutError","處理 IOException 錯誤: " + e);
            e.printStackTrace();
        } catch (ParseException e) {
            LoggerTool.infoMsg(this.nowTime+" excutError","處理 ParseException 錯誤: " + e);
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
