package crawler.components;

import java.util.regex.Pattern;

public class filterHtml {
    public static String Html2Text(String inputString) {
        String htmlStr = inputString; // 含html標簽的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_iframe;
        java.util.regex.Matcher m_iframe;
        java.util.regex.Pattern p_link;
        java.util.regex.Matcher m_link;
        java.util.regex.Pattern p_comment;
        java.util.regex.Matcher m_comment;
        java.util.regex.Pattern p_gif;
        java.util.regex.Matcher m_gif;
        java.util.regex.Pattern p_a_script;
        java.util.regex.Matcher m_a_script;
        java.util.regex.Pattern p_apostrophe;
        java.util.regex.Matcher m_apostrophe;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定義HTML標簽的正則表達式
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定義HTML標簽的正則表達式
            String regEx_iframe = "<[\\s]*?iframe[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?iframe[\\s]*?>"; // 定義HTML標簽的正則表達式
            String regEx_link = "<[\\s]*?link[^>]*?>"; // 定義HTML標簽的正則表達式
            String regEx_comment = "<!--[\\s]*?[^>]*?-->"; // 定義HTML標簽的正則表達式
            String regEx_gif = "<img (.*?)src=\"(.*?)gif\"(.*?)>"; // 定義HTML標簽的正則表達式
            String regEx_a_script = "<a href=\"javascript:;\">(.*?)</a>"; // 定義HTML標簽的正則表達式
            String regEx_apostrophe = "'";

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 過濾script標簽

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 過濾style標簽

            p_iframe = Pattern.compile(regEx_iframe, Pattern.CASE_INSENSITIVE);
            m_iframe = p_iframe.matcher(htmlStr);
            htmlStr = m_iframe.replaceAll(""); // 過濾html標簽

            p_link = Pattern.compile(regEx_link, Pattern.CASE_INSENSITIVE);
            m_link = p_link.matcher(htmlStr);
            htmlStr = m_link.replaceAll(""); // 過濾html標簽

            p_comment = Pattern.compile(regEx_comment, Pattern.CASE_INSENSITIVE);
            m_comment = p_comment.matcher(htmlStr);
            htmlStr = m_comment.replaceAll(""); // 過濾html標簽

            p_gif = Pattern.compile(regEx_gif, Pattern.CASE_INSENSITIVE);
            m_gif = p_gif.matcher(htmlStr);
            htmlStr = m_gif.replaceAll(""); // 過濾html標簽

            p_a_script = Pattern.compile(regEx_a_script, Pattern.CASE_INSENSITIVE);
            m_a_script = p_a_script.matcher(htmlStr);
            htmlStr = m_a_script.replaceAll(""); // 過濾html標簽

            p_apostrophe = Pattern.compile(regEx_apostrophe, Pattern.CASE_INSENSITIVE);
            m_apostrophe = p_apostrophe.matcher(htmlStr);
            htmlStr = m_apostrophe.replaceAll("\""); // 過濾html標簽

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }
}
