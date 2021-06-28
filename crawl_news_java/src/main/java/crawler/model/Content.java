package crawler.model;

public class Content {
    private String contentItem;
    private String contentSelector;
    private String contentImgSelector;

    public Content(String contentItem, String contentSelector, String contentImgSelector) {
        this.contentItem = contentItem;
        this.contentSelector = contentSelector;
        this.contentImgSelector = contentImgSelector;
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
