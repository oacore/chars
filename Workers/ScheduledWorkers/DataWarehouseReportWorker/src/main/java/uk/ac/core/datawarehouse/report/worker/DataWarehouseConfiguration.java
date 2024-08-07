package uk.ac.core.datawarehouse.report.worker;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

/**
 * @author lucas
 */
@Configuration
@ComponentScan(basePackages = "uk.ac.core")
@PropertySource("file:/data/core-properties/jisc-warehouse-${spring.profiles.active}.properties")
public class DataWarehouseConfiguration {

    @Value("${jisc_warehouse.aws_access_key_prod}")
    public String AWS_ACCESS_KEY_PROD;
    @Value("${jisc_warehouse.aws_access_key_stagin}")
    public String AWS_ACCESS_KEY_STAGIN;
    @Value("${jisc_warehouse.aws_secret_key_prod}")
    public String AWS_SECRET_KEY_PROD;
    @Value("${jisc_warehouse.aws_secret_key_stagin}")
    public String AWS_SECRET_KEY_STAGIN;
    @Value("${jisc_warehouse.aws_bucket_prod}")
    public String AWS_BUCKET_PROD;
    @Value("${jisc_warehouse.aws_bucket_stagin}")
    public String AWS_BUCKET_STAGIN;

    // todo: add to file configuration
    public static String REPORTS_BASE_DIRECTORY = "/tmp/jisc_reports/";


}
