package uk.ac.core.workermetrics.data.state;

/**
 *
 * @author lucasanastasiou
 */
public enum RepositoryPriority {
    VERY_HIGH, // e.g. Premium repositories
    HIGH,//e.g. uk repositories
    NORMAL,
    LOW,
    SKIP;// special repositories not controlled by the scheduler
}
