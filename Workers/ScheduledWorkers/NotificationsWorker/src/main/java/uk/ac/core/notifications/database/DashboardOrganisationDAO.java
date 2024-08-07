package uk.ac.core.notifications.database;

import java.util.List;
import java.util.Map;

public interface DashboardOrganisationDAO {

    Map<Integer, List<Integer>> loadRepositoryToOrganisationRelations();
}
