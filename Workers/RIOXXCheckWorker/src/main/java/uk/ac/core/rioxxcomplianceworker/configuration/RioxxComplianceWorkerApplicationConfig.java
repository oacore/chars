package uk.ac.core.rioxxcomplianceworker.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.elasticsearch.configuration.ElasticsearchConfiguration;
import uk.ac.core.rioxxcomplianceworker.worker.RioxxComplianceWorker;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.AbstractQueueWorkerConfiguration;
import uk.ac.core.worker.configuration.QueueList;

/**
 *
 * @author mc26486
 */
@Configuration
@ComponentScan("uk.ac.core")
@EnableElasticsearchRepositories(basePackages = {
        "uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic",
        "uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic"})
@PropertySource("file:/data/core-properties/elasticsearch-${spring.profiles.active}.properties")
@Import(ElasticsearchConfiguration.class)
public class RioxxComplianceWorkerApplicationConfig extends AbstractQueueWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        TaskType[] tasksToRegister;

        tasksToRegister = new TaskType[]{
            TaskType.RIOXX_COMPLIANCE
        };

        for (TaskType taskToRegister : tasksToRegister) {
            String fullQueueName = taskToRegister.getName() + "-queue";
            queueList.add(fullQueueName);
        }
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new RioxxComplianceWorker();
    }

}
