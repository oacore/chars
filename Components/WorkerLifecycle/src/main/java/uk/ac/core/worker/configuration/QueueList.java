package uk.ac.core.worker.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author samuel
 */
public class QueueList {

    private final List<String> list;

    public QueueList() {
        list = new ArrayList();
    }

    public QueueList(String queueNameToSubscribe) {
        this();
        list.add(queueNameToSubscribe);
    }
    
    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }


    public Iterator<String> iterator() {
        return list.iterator();
    }

    public String[] toArray() {
        return (String[])list.toArray(new String[0]);
    }

    public boolean add(String e) {
        return list.add(e);
    }

    public boolean remove(String o) {
        return list.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public String toString() {
        return "QueueList{" + "list=" + Arrays.toString(list.toArray()) + '}';
    }
}
