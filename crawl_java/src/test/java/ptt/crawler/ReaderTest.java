package ptt.crawler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ptt.crawler.model.Article;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


class ReaderTest {
    private Reader reader;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        try {
            reader = new Reader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void list() {
        try {
            Reader reader = new Reader();
            List<Article> result = reader.getList("Gossiping");
            Assertions.assertInstanceOf(List.class, result);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}