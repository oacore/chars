package uk.ac.core.database.model;

public class BigRepoHarvestingStatistic {
    private Integer idRepository;
    private String uniqueId;
    private String routingKey;
    private String taskParameters;

    public BigRepoHarvestingStatistic() {}

    public BigRepoHarvestingStatistic(Integer idRepository, String uniqueId, String routingKey,
                                      String taskParameters) {
        this.idRepository = idRepository;
        this.uniqueId = uniqueId;
        this.routingKey = routingKey;
        this.taskParameters = taskParameters;
    }

    public Integer getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(Integer idRepository) {
        this.idRepository = idRepository;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getTaskParameters() {
        return taskParameters;
    }

    public void setTaskParameters(String taskParameters) {
        this.taskParameters = taskParameters;
    }
}
