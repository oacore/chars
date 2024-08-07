/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.issueDetection.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import uk.ac.core.elasticsearch.configuration.ElasticsearchConfiguration;
import uk.ac.core.issueDetection.data.repository.IssueRepository;
import uk.ac.core.issueDetection.data.repository.resetissue.BackwardCompatibilityIssueDao;
import uk.ac.core.issueDetection.data.repository.resetissue.BackwardCompatibilityIssueDaoImpl;
import uk.ac.core.issueDetection.service.IssueService;
import uk.ac.core.issueDetection.service.IssueServiceImpl;

/**
 *
 * @author mc26486
 */
@Configuration
@EnableElasticsearchRepositories("uk.ac.core.issueDetection.data.repository")
@Import(ElasticsearchConfiguration.class)
public class IssueCollectionConfiguration {

    @Bean
    public BackwardCompatibilityIssueDao backwardCompatibilityIssueDao(ElasticsearchTemplate elasticsearchTemplate) {
        return new BackwardCompatibilityIssueDaoImpl(elasticsearchTemplate);
    }
    
    @Bean
    public IssueService issueService(IssueRepository issueRepository, BackwardCompatibilityIssueDao backwardCompatibilityIssueDao) {
        return new IssueServiceImpl(issueRepository, backwardCompatibilityIssueDao);
    }

}
