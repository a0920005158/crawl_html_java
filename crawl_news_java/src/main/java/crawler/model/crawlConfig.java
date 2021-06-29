package crawler.model;

public class crawlConfig {
    private String url;
    private String titleItem;
    private String titleNameSelector;
    private String titleImgSelector;
    private String titleDateSelector;
    private String contentLinkSelector;
    private String contentItem;
    private String contentSelector;
    private String contentImgSelector;

    public crawlConfig(
            String url,String titleItem, String titleNameSelector,
            String titleImgSelector,String titleDateSelector,String contentLinkSelector,
            String contentItem,String contentSelector,String contentImgSelector
    ){
        this.url = url;
        this.titleItem = titleItem;
        this.titleNameSelector = titleNameSelector;
        this.titleImgSelector = titleImgSelector;
        this.titleDateSelector = titleDateSelector;
        this.contentLinkSelector = contentLinkSelector;
        this.contentItem = contentItem;
        this.contentSelector = contentSelector;
        this.contentImgSelector = contentImgSelector;
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

    public String getTitleDateSelector(){ return this.titleDateSelector;}

    public String getContentLinkSelector() {
        return this.contentLinkSelector;
    }

    public String getContentItem() {
        return this.contentItem;
    }

    public String getContentSelector() {
        return this.contentSelector;
    }

    public String getContentImgSelector() {
        return this.contentImgSelector;
    }
}
