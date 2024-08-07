package uk.ac.core.database.service.ctr;

import com.google.gson.JsonObject;

/**
 *
 * @author mc26486
 */
public interface CTRDAO {

    public int getAlgorithmID(String algorithmName, JsonObject algorithmParams);
    
}
