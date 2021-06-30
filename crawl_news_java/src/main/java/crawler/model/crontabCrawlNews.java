package crawler.model;

import crawler.config.Config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class crontabCrawlNews {
    public void crontabCrawlNews(){

    }
    public void excute() throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowTime = sdf.format(new Date());
            String newTime = calculateTime(sdf.parse(nowTime),-1);
            //newTime = "2021-06-28";
            MySQL MySQL = new MySQL();
            System.out.println(newTime);
            Reader reader = new Reader();

            Map<String,crawlConfig> configList = Config.CONFIG_LIST;
            for (Map.Entry<String, crawlConfig> config : configList.entrySet()) {
//                System.out.println(config.getKey() + ":" + config.getValue());

                List<Map<String, String>> result = reader.getList(config.getKey(),newTime);
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
                    if(insertID==-1){
                        System.out.println("sqlInsetError:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}");
                    }else{
                        System.out.println("sqlInsetSuccess:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}");
                    }
                    new LoggerTool("D:\\test\\vcmslog");
                    LoggerTool.infoMsg(nowTime+" crawl_news",
                            "sqlInsetError:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"},"+
                                    "sqlInsetSuccess:{titleName:"+data.get("titleName")+",web:"+config.getKey()+"}");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
