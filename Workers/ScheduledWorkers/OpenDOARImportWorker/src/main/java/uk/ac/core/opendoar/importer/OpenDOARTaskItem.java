/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.opendoar.importer;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.opendoar.importer.connector.model.OpenDOARRepository;

/**
 *
 * @author mc26486
 */
public class OpenDOARTaskItem implements TaskItem {

    OpenDOARRepository openDOARRepository;

    public OpenDOARRepository getOpenDOARRepository() {
        return openDOARRepository;
    }

    public void setOpenDOARRepository(OpenDOARRepository openDOARRepository) {
        this.openDOARRepository = openDOARRepository;
    }

}
