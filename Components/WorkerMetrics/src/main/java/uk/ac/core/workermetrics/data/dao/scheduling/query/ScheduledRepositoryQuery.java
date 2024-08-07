package uk.ac.core.workermetrics.data.dao.scheduling.query;

public class ScheduledRepositoryQuery {

    /**
     * Calculate SCORE: (MAX(DNH - RDO),0) * PRIORITY where DNH = days not
     * harvested RDO = repository days offset:
     *   Targets:
     *   Premium Data Providers: 14
     *   UK: 15
     *   Rest of the World: 45
     *
     *   However, our vales must be half this so that the oldest repository is, on average, less than the target
     *
     */
    private final int RDO_VERY_HIGH = 5;
    private final int RDO_HIGH = 7;
    private final int RDO_OTHER = 20;
    private final int WEIGHT_VERY_HIGH = 6;
    private final int WEIGHT_HIGH = 4;
    private final int WEIGHT_OTHER = 1;

    private final String SQL = "SELECT *, GREATEST(t.DNH - t.RDO, 0)  * t.WEIGHT AS SCORE\n" +
            "FROM (SELECT s.id_repository,\n" +
            "             scheduled_state,\n" +
            "             repository_priority,\n" +
            "             last_time_scheduled,\n" +
            "             watched_repository,\n" +
            "             DATEDIFF(NOW(), last_time_scheduled)    AS DNH,\n" +
            "             CASE repository_priority\n" +
            "               WHEN 'VERY-HIGH' THEN " + RDO_VERY_HIGH + "\n" +
            "               WHEN 'HIGH' THEN " + RDO_HIGH + "\n" +
            "               ELSE " + RDO_OTHER + " END                            AS RDO,\n" +
            "             CASE repository_priority\n" +
            "               WHEN 'VERY-HIGH' THEN " + WEIGHT_VERY_HIGH + "\n" +
            "               WHEN 'HIGH' THEN " + WEIGHT_HIGH + "\n" +
            "               ELSE " + WEIGHT_OTHER + " END                            AS WEIGHT\n" +
            "      FROM scheduled_repository s\n" +
            "             JOIN repository r ON r.id_repository = s.id_repository\n" +
            "      WHERE " +
            "        %WHERE% ) t\n" +
            "ORDER BY SCORE desc;";


    /***
     * Scoring SQL query for retrieving 1 repository
     *
     * @param id_repository
     * @return
     */
    public String sqlWithIdRepository() {
        return this.SQL.replace("%WHERE%", "s.id_repository = ?");
    }

    /***
     * Order repositories by priority and scoring
     * @return
     */
    public String sqlWithFullScoring() {
        return this.SQL.replace("%WHERE%", "scheduled_state = 'PENDING'\n" +
                "        AND prevent_harvest_until < NOW()\n" +
                "        AND repository_priority != \"SKIP\"\n" +
                "        AND r.disabled = 0");
    }

    public String sqlWithPendingRepositories() {
        return this.SQL.replace("%WHERE%", "scheduled_state !='PENDING' AND repository_priority != \"SKIP\"");
    }
}
