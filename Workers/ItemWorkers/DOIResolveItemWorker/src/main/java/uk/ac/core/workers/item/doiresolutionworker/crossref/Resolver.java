package uk.ac.core.workers.item.doiresolutionworker.crossref;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.article.Citation;
import uk.ac.core.workers.item.doiresolutionworker.crossref.Response.Result;

/**
 * Resolve citations to DOIs by POSTing a JSON list of free-form citations to
 * CrossRef.
 *
 * @author lucas, dh9635
 */
public class Resolver {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Resolver.class);

    // Number of requests per second
    private static final Integer REQUESTS_PER_SEC = 1;

    // maximum number of citations which can be sent in one request
    // if the number of citations is higher than this maximum, they will be sent in multiple
    // requests
    // this is a maximum defined by CrossRef API and shouldn't be changed -- classes calling
    // Resolver methods should take care of eventually passing smaller batches to Resolver.
    public static final Integer BATCH_SIZE = 30;

    // Load settings from properties file
    private static final String ENDPOINT = "https://search.crossref.org/links";

    // For the requests
    private final HttpClient client;

    /**
     * Constructor.
     */
    public Resolver() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
        client = new DefaultHttpClient(params);

    }

    /**
     * Delay the request (to not exceed request limit).
     *
     * @return
     */
    private void delay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Resolver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Resolve given JSON request string and return the response body. Gets the
     * doi using the crossRef API http://search.labs.crossref.org/help/api
     * /links given the citation string input must be in JSON form, e.g. [ "M.
     * Henrion, D. J. Mortlock, D. J. Hand, and A. Gandy, \"A Bayesian approach
     * to star-galaxy classification,\" Monthly Notices of the Royal
     * Astronomical Society, vol. 412, no. 4, pp. 2286-2302, Apr. 2011." ]
     *
     * @param request request (JSON array with citations)
     * @return response body (JSON) or null if request couldn't be resolved
     */
    private synchronized String resolve(String request) {
        logger.debug(request, this.getClass());
        // delay the request
        Long time = System.currentTimeMillis();
        this.delay();
        logger.debug("Delay: " + (System.currentTimeMillis() - time), this.getClass());

        logger.debug("Hitting Crossref server", this.getClass());

        try {
            // create the request
            HttpPost hp = new HttpPost(ENDPOINT);
            //Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            HttpEntity he = new StringEntity(request, HTTP.UTF_8);
            hp.setHeader("Accept-Charset", "utf-8");
            hp.setHeader("Content-Type", "application/json");
            hp.setEntity(he);
            HttpResponse response = client.execute(hp);
            //logger.debug("Request: " + (System.currentTimeMillis() - time), this.getClass());
            // get return code - 200, 404, 500
            int status = response.getStatusLine().getStatusCode();

            // get response body
            InputStream is = response.getEntity().getContent();

            // convert stream to string
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String responseBody = writer.toString();

            // ensure that the content stream is closed
            EntityUtils.consumeQuietly(he);
            if (response.getEntity() != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            if (hp.getEntity() != null) {
                EntityUtils.consumeQuietly(hp.getEntity());
            }

            // return response
            if (status == HttpStatus.SC_OK) {
                return responseBody;
            } else {
                logger.warn("Query not OK! HTTP status: " + status + "request: " + request, this.getClass());

                return null;
            }
        } catch (ClientProtocolException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Encode string array of citations as JSON.
     *
     * @param citations string array of citations
     * @return JSON array
     */
    private String jsonEncodeStringArray(String[] strings) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(strings);
    }

    /**
     * JSON encode citation.
     *
     * @param c citation to be encoded
     * @return JSON array with the citation
     */
    private String jsonEncodeCitation(Citation c) {
        String[] citations = {c.constructCitationText()};
        return this.jsonEncodeStringArray(citations);
    }

    /**
     * Store the retrieved DOIs with the citations. If the length of both lists
     * doesn't match, nothing will happen.
     *
     * @param cs
     * @param results
     */
    private void addDoisToDOISavable(List<CrossrefCitation> cs, List<Result> results, DOISavable savable) {
        // something went wrong, we have different numbers of citations and DOIs
        if (results.size() != cs.size()) {
            logger.error("Number of citations and DOIs don't match!", this.getClass());
            return;
        }

        // everything was OK, add DOIs to the citations
        for (int i = 0; i < results.size(); i++) {

            // if citation had DOI from before do nothing

            CrossrefCitation crossrefCitation = cs.get(i);
            
            if (crossrefCitation.getDoi()!= null && !crossrefCitation.getDoi().isEmpty()) {
                continue;
            }

            // add the DOI to the citation
            Result doi = results.get(i);
            if (doi != null) {
                savable.save(crossrefCitation, doi);
            }
        }
    }

    /**
     * Resolve DOIs of a set or CORE articles.
     *
     * @param citations
     * @param savable
     */
    public void resolveArticleDoisTo(List<CrossrefCitation> citations, DOISavable savable) {
        // for storing the resolved DOIs
        List<Result> dois = new LinkedList<>();

        // send only BATCH_SIZE number of citations at once
        for (int i = 0; i < citations.size(); i += Resolver.BATCH_SIZE) {
            List<CrossrefCitation> toResolve = new LinkedList<>();
            for (int j = i; j < i + Resolver.BATCH_SIZE && j < citations.size(); j++) {

                toResolve.add(citations.get(j));
            }

            // resolve citations
            String stringResponse = this.resolve(this.jsonEncodeCitations(toResolve));

            // read the response
            Response response = new Gson().fromJson(stringResponse, Response.class);

            // store results
            if (response == null || !response.isQueryOk()) {
                logger.warn("Query was not OK! Query was : " + toResolve, this.getClass());
                for (int j = 0; j < Resolver.BATCH_SIZE; j++) {
                    dois.add(null);
                }
            } else {
                for (Result r : response.getResults()) {
                    if (r.isMatch()) {
                        dois.add(r);
                    } else {
                        dois.add(null);
                    }
                }
            }
        }

        // ArticleMetadata implements DOI, but we need to cast it through a wildcard for the list to be cast       
        this.addDoisToDOISavable(citations, dois, savable);
    }

    /**
     * JSON encode list of citations.
     *
     * @param cs list of citations to be encoded
     * @return JSON array with the citations
     */
    private String jsonEncodeCitations(List<CrossrefCitation> cs) {
        List<String> cStrings = new LinkedList<String>();
        for (CrossrefCitation c : cs) {
            cStrings.add(c.getCitation());
        }
        return this.jsonEncodeStringArray(cStrings.toArray(new String[cStrings.size()]));
    }

}
