package crawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class crontabCrawlNews {
    public static void main(String[] args) throws Exception {
        try {
            Reader reader = new Reader();
            List<Map<String, String>> result = reader.getList("sxxynews");
            System.out.println(result);
            getDBConfig();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void getDBConfig() {
            Properties properties = new Properties();
            String configFile = "DBConfig.properties";
            try {
                properties.load(new FileInputStream(configFile));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }

            // 第二個參數為預設值，如果沒取到值的時候回傳預設值
            String host = properties.getProperty("host", "jdbc:mysql://localhost/default");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password", "");
            System.out.println(host);
            System.out.println(username);
            System.out.println(password);
    }
}
