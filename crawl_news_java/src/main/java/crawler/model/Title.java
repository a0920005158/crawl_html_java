package crawler.model;

public class Title {
    private String url;
    private String titleItem;
    private String titleNameSelector;
    private String titleImgSelector;
    private String contentLinkSelector;

    public Title(String url,String titleItem, String titleNameSelector, String titleImgSelector,String contentLinkSelector){
        this.url = url;
        this.titleItem = titleItem;
        this.titleNameSelector = titleNameSelector;
        this.titleImgSelector = titleImgSelector;
        this.contentLinkSelector = contentLinkSelector;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitleItem(){return  this.titleItem;}

    public String getTitleNameSelector() {
        return this.titleNameSelector;
    }

    public String getTitleImgSelector() {
        return this.titleImgSelector;
    }

    public String getContentLinkSelector() {
        return this.contentLinkSelector;
    }
}
