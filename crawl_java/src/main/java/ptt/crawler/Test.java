package ptt.crawler;
import ptt.crawler.model.Article;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            Reader reader = new Reader();
            List<Article> result = reader.getList("Gossiping");
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
