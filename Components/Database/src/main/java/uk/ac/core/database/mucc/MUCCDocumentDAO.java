package uk.ac.core.database.mucc;

import java.util.Optional;

/**
 *
 * @author mc26486
 */
public interface MUCCDocumentDAO {
    
    public void save(MUCCDocument crossRefDocument);
    public MUCCDocument load(Integer coreId);
    public Optional<Long> getCoreId(String doi);
}
