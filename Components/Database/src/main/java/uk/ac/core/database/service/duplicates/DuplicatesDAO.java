package uk.ac.core.database.service.duplicates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface DuplicatesDAO {

    public Integer getParentId(Integer id);

    public List<Integer> getChildrenIds(Integer idParent);

}
