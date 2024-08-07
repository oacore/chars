/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author samuel
 * @param <T>
 * @param <O>
 */
public interface ExtendedMetadataDownloadStrategy<T extends TaskItem, O> {

    /**
     * Test that a software is compatible with this Strategy.
     *
     * @return boolean if the strategy is compatible with this repository
     */
    boolean isCompatible(DataProvider dataProvider);
    
    /**
     * Converts a RepositoryDocument to MetadataPageProcessTaskItem.
     *
     * All Items from the document table is processed by this method
     *
     * TaskItem can contain anything you need which you will be required to use
     * in a future called method
     * 
     * Usually, you'll pass at least the documentID to identify the document that is to be processed
     *
     * @param repositoryDocument
     * @return Optional<TaskItem> An object of TaskItem (usually MetadataPageTaskItem). Return empty optional if you do not want to add the item to the list to process
     */
    Optional<T> repositoryDocumentToMetadataPageProcessTaskItem(RepositoryDocument repositoryDocument);

    /**
     * Obtains a Metadata Page from a Data Provider.You should save the output of your result to saveLocation
     * 
     * 
     * 
     * @param taskItem
     * @param saveLocation
     * @return T this will be passed into future method calls
     * @throws java.lang.Exception Also causes subsequent calls to be skipped
     */
    O downloadMetadataPage(T taskItem, File saveLocation) throws Exception;

    /**
     * Returns the number of attachment found inside downloadMetadataPage
     * 
     * Public only attachments are preferred
     * 
     * @param taskItem
     * @param data the data from downloadMetadataPage
     * @return the count of attachments
     */
    int attachmentCount(T taskItem, O data);
    
    /**
     * Returns the DateTime when the Metadata was published in the Repository or Data Provider
     * @param taskItem
     * @param data
     * @return LocalDateTime|Null May return null if there is no date found
     */
    LocalDateTime repositoryMetadataRecordPublishDate(T taskItem, O data);
}
