package uk.ac.core.documentdownload.downloader.crawling;

import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.slf4j.LoggerFactory;
import uk.ac.core.database.service.repositories.RepositoryDomainException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mc26486
 */
public class CrawlingDelayMultiton {

    private static final ConcurrentHashMap<String, CrawlingDelayMultiton> instances = new ConcurrentHashMap<>();

    private String hostname;
    private BaseRobotRules robotRules;
    private Long latestAttempt;
    private Double latestDelay;
    private Double originalCrawlDelay;
    private List<Double> lastSpeeds;
    private double speedListsSum;
    private final URL originatingUrl;

    private static final Double ALPHA_COEFFICIENT = 0.85;
    private static final Integer MEMORY_SIZE = 10;
    private static final Integer MAX_SPEEDS_QUEUE_SIZE = 1000;

    /**
     * the minimum accepted speed (in bytes per second) for downloading, if it
     * drops below this downloading for this repository should halt
     */
    private Double MIN_ACCEPTED_SPEED = 1.0;
    private static final double MINIMUM_CRAWL_DELAY = 100.0;
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CrawlingDelayMultiton.class);

    private static CrawlingDelayMultiton getInstance(URL url) {

        CrawlingDelayMultiton result = CrawlingDelayMultiton.instances.get(url.getHost());
        if (result == null) {
            CrawlingDelayMultiton crawlingDelayMultiton = new CrawlingDelayMultiton(url);
            result = instances.putIfAbsent(url.getHost(), crawlingDelayMultiton);
            if (result == null) {
                result = crawlingDelayMultiton;
            }

        }

        return result;
    }

    public static CrawlingDelayMultiton getInstanceFromUrl(String currentUrl, String sameDomainEnforced) throws MalformedURLException {
        return CrawlingDelayMultiton.getInstanceFromUrl(null, currentUrl, sameDomainEnforced);
    }

    public static CrawlingDelayMultiton getInstanceFromUrl(List<RepositoryDomainException> repositoryDomainExceptions, String currentUrl, String sameDomainEnforced) throws MalformedURLException {
        URL url = new URL(currentUrl);
        if (sameDomainEnforced != null && !sameDomainEnforced.isEmpty()) {
            boolean domainExceptionMatch = false;
            if (repositoryDomainExceptions != null) {
                for (RepositoryDomainException domainException : repositoryDomainExceptions) {
                    boolean Contains = currentUrl.contains(domainException.getDomainUrl());
                    domainExceptionMatch |= Contains;
                }
            }
            if (!domainExceptionMatch
                    && !url.getHost().equals("hdl.handle.net")
                    && !url.getHost().equals("dx.doi.org")
                    && !url.getHost().equals("doi.org")
                    && !url.getHost().equals(sameDomainEnforced)) {
                return null;
            }
        }
        CrawlingDelayMultiton instance = CrawlingDelayMultiton.getInstance(url);

        return instance;
    }

    private CrawlingDelayMultiton(URL url) {
        this.hostname = url.getHost();
        this.originatingUrl = url;
        this.lastSpeeds = new LinkedList<>();
        this.speedListsSum = 0.0;
    }

    public boolean isAllowed(String url) {
        //String page = url.substring(url.indexOf(this.hostname) + this.hostname.length());
        return this.getRobotRules().isAllowed(url);
    }

    public Long getDelay() {
        Long now = System.currentTimeMillis();
        if (this.latestAttempt == null) {
            //no need to wait
            this.latestAttempt = now;
            return (long) 100;
        }
        if (this.latestDelay == null) {
            this.latestDelay = this.getOriginalCrawlDelay();
        }

        Long elapsedSinceLastAttempt = now - this.latestAttempt;
        if (elapsedSinceLastAttempt > this.latestDelay) {
            this.latestAttempt = now;
            return (long) 0;
        }

        Long toWait = this.latestDelay.longValue() - elapsedSinceLastAttempt;
        this.latestAttempt = now + toWait;
        return toWait;

        //TODO to be make adaptive 
        //TODO refresh robots if too old
    }

    public void updateDownloadTime(Long downloadTime, Long fileSize) {
        //do the adaptive trick
        //
        // compute the file download speed in b/ms (equivalent to 1000 b/s)
        double speedCurrent = (double) fileSize / (double) downloadTime;

        int lastSpeedsLength = lastSpeeds.size();
        double gainFactor = 1.0;
        if (this.latestDelay == null) {
            this.latestDelay = this.getOriginalCrawlDelay();
        }
        double newDelay = this.latestDelay;
        Double previousCount = this.speedListsSum;

        if (!this.lastSpeeds.isEmpty()) {
            if (lastSpeedsLength > MEMORY_SIZE) {

                //mean of all 
                Double speedMean = this.speedListsSum / lastSpeedsLength;

                //mean of latest MEMORY_SIZE
                Double last10SpeedMean = computeMean(lastSpeeds, MEMORY_SIZE);

                //in the case that the average of latest downloads is smaller
                //than a tolerated threshold then we shall halt the download 
                //process for this repository  
                if (last10SpeedMean < MIN_ACCEPTED_SPEED) {
                    
                    logger.warn("MINIMUM speed reached");
                    /*
                     throw new MinimumSpeedException(s_memory, MEMORY_SIZE, MIN_ACCEPTED_SPEED);
                     //*/
                }

                if ((speedMean != 0.0) && (last10SpeedMean != 0.0)) {
                    gainFactor = speedMean / last10SpeedMean; //MC changed the gainfactor to support what is written in the comment
                }
                // gain factor is >1 if speed decreased  -> delay should go up
                //                <1 if speed increased  -> delay should go down 

                //make adjustment only if significant deviation from the previous average speed
                if (gainFactor > 1.10 || gainFactor < 0.85) {
                    newDelay = CrawlingDelayMultiton.ALPHA_COEFFICIENT * this.latestDelay
                            + (1 - CrawlingDelayMultiton.ALPHA_COEFFICIENT) * gainFactor * latestDelay;
                    //clear memory after adjustment
                    lastSpeeds.clear();
                    speedListsSum = 0.0;
                    if (newDelay < originalCrawlDelay) {
                        newDelay = originalCrawlDelay;
                    }
                    logger.info("Adjustment made: Going from "+this.latestDelay+"ms to "+newDelay+"ms.");
                } else {
                    logger.info("NO adjustment made. Last 10 speed was:"+last10SpeedMean+" b/ms total average was:"+speedMean+" b/ms");

                }
            }
        }

        //flush the list if we exceed the 1000 marker
        if (lastSpeedsLength > MAX_SPEEDS_QUEUE_SIZE) {

            for (int i = lastSpeedsLength; i > MAX_SPEEDS_QUEUE_SIZE; i--) {
                Double lastElement = lastSpeeds.get(lastSpeeds.size() - 1);

                speedListsSum = speedListsSum - lastElement;
                lastSpeeds.remove(lastSpeeds.size() - 1);
            }
            logger.info("List holding the speeds for url:"+this.hostname+" exceeded 1000 and is flushed (cleared).");
        }

        //update maps
        this.latestDelay = newDelay;
        lastSpeeds.add(0, speedCurrent);//put in start of list

        //update the temp counter        
        this.speedListsSum += speedCurrent;
    }

    /**
     * Computes the mean average of the first N elements of a list
     *
     * @param data
     * @param howMany
     * @return
     */
    private double computeMean(List<Double> data, int howMany) {
        if (howMany <= 0) {
            return 0.0;
        }
        Iterator it = data.iterator();
        int counter = 0;
        double sum = 0.0;

        // a for loop might be cleaner 
        while (it.hasNext()) {
            Double l = (Double) it.next();
            sum += l;
            counter++;
            if (counter >= howMany) {
                break;
            }
        }
        double mean = sum / counter;

        return mean;
    }

    /**
     * Computes the average of all elements in the list
     *
     * @param data
     * @return
     */
    private double computeMean(List<Double> data) {
        int counter = 0;
        double sum = 0.0;

        for (Double d : data) {
            sum += d;
            counter++;
        }

        double mean = sum / counter;

        return mean;
    }

    private BaseRobotRules getRobotRules() {
        if (this.robotRules == null) {
            try {
                this.robotRules = this.collectRobotRules();
            } catch (MalformedURLException ex) {
                logger.error(ex.getMessage());
            }
        }
        return robotRules;
    }

    public void setRobotRules(BaseRobotRules robotRules) {
        this.robotRules = robotRules;
    }

    private BaseRobotRules collectRobotRules() throws MalformedURLException {
        UserAgent userAgent = new UserAgent("CORE", "https://core.ac.uk/contact", "https://core.ac.uk");
        BaseHttpFetcher baseHttpFetcher = RobotUtils.createFetcher(userAgent, 1);
        URL robotsUrl = new URL(this.originatingUrl.getProtocol(), this.originatingUrl.getHost(), this.originatingUrl.getPort(), "/robots.txt");
        return RobotUtils.getRobotRules(baseHttpFetcher, new SimpleRobotRulesParser(), robotsUrl);
    }

    private Double getOriginalCrawlDelay() {
        this.originalCrawlDelay = (double) this.getRobotRules().getCrawlDelay();
        //MC: when there is no delay, crawler commons return a negative integer :/
        if (this.originalCrawlDelay <= 0) {
            this.originalCrawlDelay = MINIMUM_CRAWL_DELAY;
        }
        return this.originalCrawlDelay;
    }

}
