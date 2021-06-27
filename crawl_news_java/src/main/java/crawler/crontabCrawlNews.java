package crawler;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class crontabCrawlNews {
    public static void main(String[] args) {
        try {
            Reader reader = new Reader();
            List<Map<String, String>> result = reader.getList("ppt");
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
