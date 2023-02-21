package crawler.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class clearOverCountNews {
    private Integer keepCount;


    public clearOverCountNews(Integer keepCount){
        this.keepCount = keepCount;
    }

    public String excute(){
        MySQL MySQL = new MySQL();
        List<Map<String, Object>> newsCountQuery = MySQL.select("SELECT COUNT( * ) AS COUNT FROM news");
        Integer newsCount = Integer.parseInt(newsCountQuery.get(0).get("COUNT").toString());
        Integer difCount = newsCount - this.keepCount;
        List<String> successId= new ArrayList<>();
        if (difCount > 0){
            List<Map<String, Object>> deleteList = MySQL.select("SELECT id,image_title,image_content FROM news order by create_at ASC limit " + Integer.toString(difCount));
            for (Map<String, Object> deteData : deleteList){
                String id = deteData.get("id").toString();
                String image_title = deteData.get("image_title").toString();
                String image_content = deteData.get("image_content").toString();
                if(image_title != null && !image_title.isEmpty())
                    MySQL.update("DELETE FROM news_title_image WHERE id="+image_title);
                for (String conid : image_content.split(",")){
                    if(conid != null && !conid.isEmpty())
                        MySQL.update("DELETE FROM news_content_image WHERE id="+conid);
                }
                MySQL.update("DELETE FROM news WHERE id="+id);
                successId.add(
                        id
                );
            }
        }
        return "successId: "+successId.toString();
    }
}
