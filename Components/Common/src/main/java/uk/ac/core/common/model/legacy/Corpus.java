/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.core.common.model.legacy;

/**
 *
 * @author pk3295
 */
public interface Corpus<T> {

    public void start();

    public boolean hasNext();

    public T get();

}
