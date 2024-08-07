package uk.ac.core.singleitemworker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.singleitemworker.SingleItemWorkerStatus;
import uk.ac.core.worker.WorkerStatus;
import uk.ac.core.worker.configuration.AbstractQueueWorkerConfiguration;

/**
 *
 * @author lucasanastasiou
 */
@ComponentScan("uk.ac.core") // search the com.company package for @Component classes
@PropertySource(
        {
            "file:/data/core-properties/queue-${spring.profiles.active}.properties",
            "file:/data/core-properties/chars-components-${spring.profiles.active}.properties"
        }
)
public abstract class AbstractSingleItemWorkerConfiguration extends AbstractQueueWorkerConfiguration {

    @Override
    @Bean
    public WorkerStatus workerStatus() {
        return new SingleItemWorkerStatus();
    }

}
