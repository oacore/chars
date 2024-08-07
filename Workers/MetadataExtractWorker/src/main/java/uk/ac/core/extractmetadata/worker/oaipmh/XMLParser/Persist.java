/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

/**
 *
 * @author samuel */
public interface Persist<T> {
    
    void persist(T obj);

    /**
     * If called, it must be implemented to run outside of the Article Loop. I.E. as a finalise.
     * <p>
     * Intended use for logging, reporting statistics or to clear any non-commited queries
     */
    default void finalise(boolean generateStatistics) { }
}
