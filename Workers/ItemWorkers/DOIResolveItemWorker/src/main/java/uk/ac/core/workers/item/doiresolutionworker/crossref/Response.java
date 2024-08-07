
package uk.ac.core.workers.item.doiresolutionworker.crossref;

import java.util.LinkedList;
import java.util.List;

/**
 * Model of CrossRef API response (for decoding the JSON response string).
 * @author dh9635
 */
public class Response {
    
    public class Result {
        // response text
        private String text = null;
        // does the string match some record?
        private boolean match = false;
        // the resolved DOI
        private String doi = null;
        // confidence score
        private double score = 0.0;
        // reason why the query doesn't match any record
        private String reason = null;
        // URL encdoed information about the citation
        private String coins = "";
        
        /**
         * Constructor for successful request.
         * @param text
         * @param match
         * @param doi
         * @param score 
         */
        public Result(String text, boolean match, String doi, double score) {
            this.text = text;
            this.match = match;
            this.doi = doi;
            this.score = score;
        }
        
        /**
         * Constructor for unsuccessful request.
         * @param text
         * @param match
         * @param reason 
         */
        public Result(String text, boolean match, String reason) {
            this.text = text;
            this.match = match;
            this.reason = reason;
        }

        public String getDoi() {
            return doi;
        }

        public boolean isMatch() {
            return match;
        }

        public String getReason() {
            return reason;
        }

        public double getScore() {
            return score;
        }

        public String getText() {
            return text;
        }

        public String getCoins() {
            return coins;
        }

        public void setCoins(String coins) {
            this.coins = coins;
        }        
    }
    
    private boolean query_ok = false;
    private List<Result> results;
    
    /**
     * Constructor.
     */
    public Response() {
        this.results = new LinkedList<Result>();
    }

    public boolean isQueryOk() {
        return query_ok;
    }

    public List<Result> getResults() {
        return results;
    }
    
}
