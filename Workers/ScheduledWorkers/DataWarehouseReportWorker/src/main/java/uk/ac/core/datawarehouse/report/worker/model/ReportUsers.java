package uk.ac.core.datawarehouse.report.worker.model;

/**
 *
 * @author lucas
 */
public class ReportUsers extends Report{

    Integer aPI_users;
    Integer dashboard_Users;
    Integer recommender_users;
//Active_recommender_users
    Integer total_datadump_registrations;

    public Integer getAPI_users() {
        return aPI_users;
    }

    public void setAPI_users(Integer aPI_users) {
        this.aPI_users = aPI_users;
    }

    public Integer getDashboard_Users() {
        return dashboard_Users;
    }

    public void setDashboard_Users(Integer dashboard_Users) {
        this.dashboard_Users = dashboard_Users;
    }

    public Integer getRecommender_users() {
        return recommender_users;
    }

    public void setRecommender_users(Integer recommender_users) {
        this.recommender_users = recommender_users;
    }

    public Integer getTotal_datadump_registrations() {
        return total_datadump_registrations;
    }

    public void setTotal_datadump_registrations(Integer total_datadump_registrations) {
        this.total_datadump_registrations = total_datadump_registrations;
    }
}
