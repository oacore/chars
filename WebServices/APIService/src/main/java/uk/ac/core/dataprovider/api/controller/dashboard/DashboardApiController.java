package uk.ac.core.dataprovider.api.controller.dashboard;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchWorkMetadata;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.repositories.WorksMetadataRepository;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
@PropertySource("file:/data/core-properties/chars-components-${spring.profiles.active}.properties")
public class DashboardApiController {

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    ArticleMetadataRepository elasticsearchArticleMetadataRepository;

    @Autowired
    WorksMetadataRepository elasticsearchWorkMetadataRepository;

    @Autowired
    SupervisorClient supervisorClient;

    @RequestMapping(value = "/disable/{articleId}/{disable}", produces = {"application/json"}, method = RequestMethod.POST)
    @ApiResponses({
            @ApiResponse(code = 204, message = "successful operation", response = Void.class)
            ,
            @ApiResponse(code = 400, message = "Invalid ID supplied", response = Void.class)
            ,
            @ApiResponse(code = 404, message = "artcile_id not found", response = Void.class)})
    @ApiOperation(value = "Disable or enable a document", notes = "Marks the document as disabled or enabled and indexes the document", response = Void.class, tags = {"dashboard",})
    public ResponseEntity<Void> postDisableArticle(
            @ApiParam(value = "ID of article to disable/enable", required = true)
            @PathVariable("articleId") Long articleId,
            @ApiParam(value = "true to disable or enable", required = true)
            @PathVariable("disable") Boolean disable
    ) {

        DeletedStatus status = (disable) ? DeletedStatus.DISABLED : DeletedStatus.ALLOWED;

        this.repositoryDocumentDAO.setDocumentDeleted(articleId.intValue(), status);

        Optional<ElasticSearchArticleMetadata> opt = elasticsearchArticleMetadataRepository.findById(articleId.toString());
        opt.ifPresent((ElasticSearchArticleMetadata t) -> {
            t.setDeleted(status);
            elasticsearchArticleMetadataRepository.save(t);
        });
        if (status.equals(DeletedStatus.ALLOWED)) {
            try {
                this.supervisorClient.sendWorkIndexItemRequest(articleId.intValue());
            } catch (CHARSException e) {
                e.printStackTrace();
            }
        }
        Optional<ElasticSearchWorkMetadata> optWork = elasticsearchWorkMetadataRepository.findOneByCoreIds(articleId.toString());
        optWork.ifPresent((ElasticSearchWorkMetadata w) -> {
            if (status.equals(DeletedStatus.DELETED) || status.equals(DeletedStatus.DISABLED)) {
                w.getCoreIds().remove(articleId.toString());
                elasticsearchWorkMetadataRepository.save(w);

            }
        });
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
