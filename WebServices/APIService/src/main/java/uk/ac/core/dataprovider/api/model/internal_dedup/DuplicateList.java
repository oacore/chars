package uk.ac.core.dataprovider.api.model.internal_dedup;

import java.util.ArrayList;
import java.util.List;

public class DuplicateList {

    private List<DuplicateItem> items;


    public DuplicateList() {
        items = new ArrayList<>();
    }

    public List<DuplicateItem> getItems() {
        return items;
    }

    public void setItems(List<DuplicateItem> items) {
        this.items = items;
    }

//      private Map<Integer, List<Integer>> map;
//    public Map<Integer, List<Integer>> getMap() {
//        return map;
//    }
//
//    public void setMap(Map<Integer, List<Integer>> map) {
//        this.map = map;
//    }
//
//    public void addArticleIdToWorkId(int workId, int articleId) {
//        this.map.get(workId).add(articleId);
//    }
//
//    public void putFirstValues(int workId, int articleId) {
//        this.map.put(workId, new ArrayList<>());
//        this.addArticleIdToWorkId(workId, articleId);
//    }
//
//    public boolean checkIfKeyExist(int workId) {
//        return this.map.containsKey(workId);
//
//    }
}
