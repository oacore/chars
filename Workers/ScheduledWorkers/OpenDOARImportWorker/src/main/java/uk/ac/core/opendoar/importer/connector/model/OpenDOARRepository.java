package uk.ac.core.opendoar.importer.connector.model;

import uk.ac.core.dataprovider.logic.entity.DataProviderLocation;
import uk.ac.core.opendoar.importer.connector.json.Item;

/**
 *
 * @author Tomas Korec
 */
public class OpenDOARRepository {

    private String id;
    private Item item;
    private DataProviderLocation dataProviderLocation;
    private OpenDOARRepositoryStatus status = OpenDOARRepositoryStatus.NOT_DETECTED;

    /**
     * Status.
     */
    public enum OpenDOARRepositoryStatus {

        SYNCHRONIZED("synchronized"),
        NEW("new"),
        NEW_WITH_URI("new_with_URI"),
        CHANGED("changed"),
        NOT_DETECTED("not_detected"),;

        private String value;

        OpenDOARRepositoryStatus(String val) {
            this.value = val;
        }

        public String getValue() {
            return value;
        }

        static public OpenDOARRepositoryStatus getStatus(String status) {
            if (status != null) {
                for (OpenDOARRepositoryStatus s : OpenDOARRepositoryStatus.values()) {
                    if (status.equalsIgnoreCase(s.value)) {
                        return s;
                    }
                }
            }
            return OpenDOARRepositoryStatus.NOT_DETECTED;
        }
    }

    /**
     * Constructor.
     */
    public OpenDOARRepository() {
        // default values
        id = null;
        dataProviderLocation = new DataProviderLocation();
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OpenDOARRepositoryStatus getStatus() {
        return status;
    }

    public void setStatus(OpenDOARRepositoryStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        setStatus(OpenDOARRepositoryStatus.getStatus(status));
    }

    public String getStatusString() {
        return status.getValue().toLowerCase().replace("_", " ");
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public DataProviderLocation getDataProviderLocation() {
        return dataProviderLocation;
    }

    public void setDataProviderLocation(DataProviderLocation dataProviderLocation) {
        this.dataProviderLocation = dataProviderLocation;
    }
    
    
}
