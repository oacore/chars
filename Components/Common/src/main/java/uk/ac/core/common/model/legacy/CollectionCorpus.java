/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.core.common.model.legacy;

import java.util.List;

/**
 *
 * @author pk3295
 */
public class CollectionCorpus<T> implements Corpus {

    List<T> collection;
    int curPosition;

    public void setCollection(List<T> collection) {
        this.collection = collection;
    }

    public void start() {
        curPosition = -1;
    }

    public boolean hasNext() {
        curPosition ++;
        return (collection.size() > curPosition);
    }

    public T get() {
        return collection.get(curPosition);
    }

    public int size() {
        return this.collection.size();
    }
    
}
