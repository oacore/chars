package uk.ac.core.extractmetadata.dataset.crossref.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.crossref.json.*;
import uk.ac.core.database.languages.LanguageDAO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CrossrefWorkMapper {
    private static final Logger log = LoggerFactory.getLogger(CrossrefWorkMapper.class);

    private final LanguageDAO languageDAO;

    @Autowired
    public CrossrefWorkMapper(LanguageDAO languageDAO) {
        this.languageDAO = languageDAO;
    }

    /**
     * Map Crossref Work to CORE Article Metadata
     * See <a href="https://api.crossref.org/swagger-ui/index.html">Crossref API Docs</a>
     *
     * @param work Crossref work
     * @return relevant Article Metadata object
     */
    public ArticleMetadata newArticleMetadata(CrossRefDocument work) {
        final ArticleMetadata am = new ArticleMetadata();

        PublishedPrint published = work.getPublishedPrint();
        if (published != null) {
            am.setDate(published.toDateString());
        }

        // `created` is required
        Created workCreated = work.getCreated();
        if (workCreated != null) {
            TextToDateTime dateStamp = new TextToDateTime(workCreated.getDateTime());
            am.setDateStamp(dateStamp.asUtilDate());
        }

        // `title` is required
        List<String> workTitle = work.getTitle();
        if (workTitle != null && !workTitle.isEmpty()) {
            am.setTitle(work.getTitle().get(0));
        }

        // `author` is required
        List<Author> workAuthor = work.getAuthor();
        if (workAuthor != null) {
            List<String> authorNames = work.getAuthor().stream()
                    .map(Author::getFullName)
                    .collect(Collectors.toList());
            am.setAuthors(authorNames);
        }

        am.setDescription(work.getDescription());

        am.setDoi(work.getDOI());

        am.setPublisher(work.getPublisher());

        am.setTypes(Collections.singletonList(work.getType()));

        String customOAI;
        try {
            // because of `document`.`oai` field constraints
            customOAI = "info:doi/" + URLEncoder.encode(work.getDOI(), "utf-8");
            if (customOAI.length() >= 300) {
                customOAI = "info:doi/hash" + work.getDOI().hashCode();
            }
            am.setOAIIdentifier(customOAI);
        } catch (UnsupportedEncodingException e) { // theoretically it should never happen, however ...
            throw new RuntimeException(e);
        }

        am.setJournalIssns(work.getISSN());

        am.setJournalIdentifiers(work.getISSN());

        List<Link> workLinks = work.getLink();
        if (workLinks != null) {
            List<String> links = work.getLink().stream()
                    .map(Link::getURL)
                    .collect(Collectors.toList());
            links.add(work.getURL());
            am.setPdfUrls(links);
        }

        if (published != null) {
            am.setYear(published.getYear());
        }

        am.setDeleted(DeletedStatus.ALLOWED);

        am.setSubjects(work.getSubject());

        am.setRawLanguage(work.getLanguage());
        this.languageDAO.getIdByCode(work.getLanguage())
                .ifPresent(langId -> am.setLanguage(this.languageDAO.getById(langId)));

        List<License> license = work.getLicense();
        if (license != null && !license.isEmpty()) {
            List<License> notNullLicences = license.stream()
                    .filter(l -> l.getStart() != null)
                    .collect(Collectors.toList());
            am.setLicense(this.findLicense(notNullLicences));
        }

        List<String> identifiers = new ArrayList<>();
        identifiers.add(customOAI);
        identifiers.add(work.getDOI());
        am.setIdentifiers(identifiers);

        return am;
    }

    private String findLicense(List<License> license) {
        if (license.isEmpty()) {
            return null;
        } else if (license.size() == 1) {
            return license.get(0).getURL();
        } else {
            try {
                /*
                 * if more than 1 :
                 * + search for Creative Commons,
                 * + otherwise, take the URL of the latest
                 */
                Comparator<License> compareByDate = (o1, o2) -> {
                    Timestamp t1 = CrossRefDocument.datePartsToTimestamp(o1.getStart().getDateParts().get(0));
                    Timestamp t2 = CrossRefDocument.datePartsToTimestamp(o2.getStart().getDateParts().get(0));
                    return t1.compareTo(t2);
                };

                String latest = license.stream()
                        .sorted(compareByDate)
                        .map(License::getURL)
                        .findFirst()
                        .get(); // no Exception should be thrown because `license` is not empty at this point

                return license.stream()
                        .sorted(compareByDate)
                        .map(License::getURL)
                        .filter(url -> url.contains("creativecommons"))
                        .findFirst()
                        .orElse(latest);
            } catch (Exception e) {
                log.error("Failed to find licence");
                log.error("Runtime exception", e);
                return null;
            }
        }
    }
}
