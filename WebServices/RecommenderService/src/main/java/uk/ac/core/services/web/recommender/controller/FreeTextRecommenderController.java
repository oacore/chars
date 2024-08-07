package uk.ac.core.services.web.recommender.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.services.web.recommender.model.RecommenderServiceResponse;
import uk.ac.core.services.web.recommender.services.FreeTextRecommenderService;

/**
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
@RestController
public class FreeTextRecommenderController {

    @Autowired
    ArticleMetadataRepository elasticsearchArticleMetadataRepository;

    @Autowired
    FreeTextRecommenderService recommendService;

    @RequestMapping(value = "/recommend/freetext", method = {RequestMethod.GET, RequestMethod.POST})
    public RecommenderServiceResponse recommend(
            @RequestBody String freetext
    ) throws UnsupportedEncodingException {

        String text = URLDecoder.decode(freetext, "UTF-8");
        text = (text.startsWith("text={\"text\":\"")) ? text.substring(14, text.length() - 2) : text;

        return recommendService.recommend(text);
    }
}
