package uk.ac.core.services.web.recommender.model;

import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;

/**
 * @author mc26486
 */
public class RecommenderServiceRequest {

    private String title;
    private String url;
    private String oai;
    private String aabstract;
    private String algorithm;
    private String referer;
    private String repositoryId;
    private ElasticSearchArticleMetadata targetArticle;
    private Integer targetArticleId;
    private Integer size;
    private String recType;
    private String idRecommender;
    private String resultType;

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOai() {
        return oai;
    }

    public void setOai(String oai) {
        this.oai = oai;
    }

    public String getAabstract() {
        return aabstract;
    }

    public void setAabstract(String aabstract) {
        this.aabstract = aabstract;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public ElasticSearchArticleMetadata getTargetArticle() {
        return targetArticle;
    }

    public void setTargetArticle(ElasticSearchArticleMetadata targetArticle) {
        this.targetArticle = targetArticle;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getIdRecommender() {
        return idRecommender;
    }

    public void setIdRecommender(String idRecommender) {
        this.idRecommender = idRecommender;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Integer getTargetArticleId() {
        return targetArticleId;
    }

    public void setTargetArticleId(Integer targetArticleId) {
        this.targetArticleId = targetArticleId;
    }

    @Override
    public String toString() {
        return "RecommenderServiceRequest{" + "title=" + title + ", url=" + url + ", oai=" + oai
                + ", aabstract=" + aabstract + ", algorithm=" + algorithm + ", referer=" + referer
                + ", repositoryId=" + repositoryId
                + ", targetArticle ID=" + (targetArticle == null ? "" : targetArticle.getId())
                + ", size=" + size + ", recType=" + recType
                + ", idRecommender=" + idRecommender
                + '}';
    }

    public String getSHA1code() {
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title);
        }
        if (oai != null) {
            sb.append(oai);
        }
        if (targetArticle != null && targetArticle.getId() != null) {
            sb.append(targetArticle.getId());
        }
        if (repositoryId != null) {
            sb.append(repositoryId);
        }
        if (!recType.equalsIgnoreCase("output")) {
            sb.append(recType);
        }

        String key = DigestUtils.sha1Hex(sb.toString());
        return key;
    }

}
