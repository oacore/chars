package uk.ac.core.services.web.affiliations.service;

import org.apache.lucene.search.spell.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponse;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponseItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegexExtractionService {

    private static final String FULL_TEXT_PATH_PREFIX = "/data/remote/core/filesystem/text/";
    private static final String FULL_TEXT_PATH_PREFIX_OLD = "/data/filesystem/core/filesystem/text/";

    private static final String REGEX = "([A-Za-z0-9+_.-]+|\\{.+\\})@[A-Za-z0-9.-]+\\.[A-Za-z]+";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    private static final Logger log = LoggerFactory.getLogger(RegexExtractionService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AffiliationsDiscoveryResponse extract(AffiliationsDiscoveryRequest request) {
        log.info("Extracting affiliations with regular expression method ...");
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setCoreId(request.getCoreId());
        response.setRepoId(request.getRepoId());
        response.setDateCreated(new Date());
        response.setSource("regex");
        long start = System.currentTimeMillis(), end;
        try {

            /**
             * STEP 0: ACCESS THE FULL TEXT
             */
            File fullText = this.getFullText(request.getRepoId(), request.getCoreId());
//            File fullText = this.getFullTextMock(request.getCoreId());
            if (fullText == null) {
                log.info("Could not found a fulltext on the disk");
                response.setMessage("No full text");
                response.setCount(0);
                end = System.currentTimeMillis();
                response.setTook(end - start);
                return response;
            }

            /**
             * STEP 1: EXTRACT EMAILS
             */
            List<AffiliationsDiscoveryResponseItem> responseItems = this.extractEmails(fullText);
            if (responseItems.isEmpty()) {
                log.info("Could not extract any email from the fulltext");
                response.setMessage("No emails");
                response.setCount(0);
                end = System.currentTimeMillis();
                response.setTook(end - start);
                return response;
            }

            /**
             * STEP 2: MATCH AFFILIATIONS
             */
            this.matchAffiliations(responseItems);
            if (responseItems.isEmpty()) {
                log.info("Could not match any email to an affiliation");
                response.setMessage("No affiliations matched");
                response.setCount(0);
                end = System.currentTimeMillis();
                response.setTook(end - start);
                return response;
            }

            /**
             * STEP 3: MATCH AUTHORS
             */
            this.matchAuthors(responseItems, request.getAuthors());
            if (responseItems.isEmpty()) {
                log.info("Could not match any email to an author");
                response.setMessage("No authors matched");
                response.setCount(0);
                end = System.currentTimeMillis();
                response.setTook(end - start);
                return response;
            }

            /**
             * STEP 4: PREPARE RESPONSE
             */
            response.setHits(responseItems);
            response.setCount(responseItems.size());
            response.setMessage("OK");
            end = System.currentTimeMillis();
            response.setTook(end - start);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            response.setCount(0);
            response.setMessage("Error message: " + e.getMessage());
            end = System.currentTimeMillis();
            response.setTook(end - start);
        }
        log.info("Regular expression method finished in {} ms", response.getTook());
        log.info("Extracted {} affiliations", response.getCount());
        return response;
    }

    private File getFullTextMock(Integer coreId) {
        String filePath = FULL_TEXT_PATH_PREFIX + "mock/" + coreId.toString() + ".pdf";
        return new File(filePath);
    }

    private void matchAuthors(List<AffiliationsDiscoveryResponseItem> responseItems, List<String> authors) throws Exception {
        for (AffiliationsDiscoveryResponseItem responseItem: responseItems) {
            String email = responseItem.getEmail();
            Map<String, Double> authorConfidenceMap = new HashMap<>();
            // calculate matching of this email to all authors
            for (String authorName: authors) {
                // generate list of possible emails for an author
                String[] parsedAuthorName = authorName.split("[-().,\\s]+");
                List<String> possibleEmails = this.predictPossibleEmails(parsedAuthorName);

                // evaluate distances
                LevenshteinDistance ld = new LevenshteinDistance();
                List<Float> distances = new ArrayList<>();
                for (String possibleEmail: possibleEmails) {
                    distances.add(ld.getDistance(
                            email.split("@")[0].toLowerCase(),
                            possibleEmail
                    ));
                }

                // find the best distance
                double bestDistance = distances.stream().mapToDouble(Float::doubleValue)
                        .max()
                        .orElseThrow(() -> new Exception("Could not find max value from the list"));

                authorConfidenceMap.put(authorName, bestDistance);
            }
            Map.Entry<String, Double> bestMatching = null;
            for (Map.Entry<String, Double> matching: authorConfidenceMap.entrySet()) {
                if (bestMatching == null || matching.getValue().compareTo(bestMatching.getValue()) > 0) {
                    bestMatching = matching;
                }
            }
            responseItem.setAuthor(bestMatching.getKey());
            responseItem.setConfidence(bestMatching.getValue());
        }
    }

    private List<String> predictPossibleEmails(String[] authorName) {
        List<String> possibleEmails = new ArrayList<>();
        int len = authorName.length;

        String firstName = authorName[0].toLowerCase();
        String middleName1 = "";
        if (len > 2) {
            middleName1 = authorName[1].toLowerCase();
        }
        String lastName = authorName[len - 1].toLowerCase();

        // rule - lastname@domain.com
        possibleEmails.add(String.format("%s", lastName));
        if (middleName1.length() > 0) {
            // rule - [fml]@domain.com
            possibleEmails.add(String.format("%s%s%s", firstName.charAt(0), middleName1.charAt(0), lastName.charAt(0)));
            possibleEmails.add(String.format("%s%s%s", firstName.charAt(0), lastName.charAt(0), middleName1.charAt(0)));
            possibleEmails.add(String.format("%s%s%s", middleName1.charAt(0), firstName.charAt(0), lastName.charAt(0)));
            possibleEmails.add(String.format("%s%s%s", middleName1.charAt(0), lastName.charAt(0), firstName.charAt(0)));
            possibleEmails.add(String.format("%s%s%s", lastName.charAt(0), middleName1.charAt(0), firstName.charAt(0)));
            possibleEmails.add(String.format("%s%s%s", lastName.charAt(0), firstName.charAt(0), middleName1.charAt(0)));
            // rule - [firstname.middlename.lastname]@domain.com
            possibleEmails.add(String.format("%s%s%s", firstName, middleName1, lastName));
            possibleEmails.add(String.format("%s%s%s", firstName, lastName, middleName1));
            possibleEmails.add(String.format("%s%s%s", middleName1, firstName, lastName));
            possibleEmails.add(String.format("%s%s%s", middleName1, lastName, firstName));
            possibleEmails.add(String.format("%s%s%s", lastName, middleName1, firstName));
            possibleEmails.add(String.format("%s%s%s", lastName, firstName, middleName1));
        }
        // rule - [lastname.f]@domain.com
        possibleEmails.add(String.format("%s%s", lastName, firstName.charAt(0)));
        possibleEmails.add(String.format("%s.%s", lastName, firstName.charAt(0)));
        possibleEmails.add(String.format("%s%s", firstName.charAt(0), lastName));
        possibleEmails.add(String.format("%s.%s", firstName.charAt(0), lastName));
        // rule - [firstname.l]@domain.com
        possibleEmails.add(String.format("%s.%s", firstName, lastName.charAt(0)));
        possibleEmails.add(String.format("%s%s", firstName, lastName.charAt(0)));
        possibleEmails.add(String.format("%s.%s", lastName.charAt(0), firstName));
        possibleEmails.add(String.format("%s%s", lastName.charAt(0), firstName));
        // rule - [firstname.lastname]@domain.com
        possibleEmails.add(String.format("%s%s", firstName, lastName));
        possibleEmails.add(String.format("%s.%s", firstName, lastName));
        possibleEmails.add(String.format("%s%s", lastName, firstName));
        possibleEmails.add(String.format("%s.%s", lastName, firstName));
        if (middleName1.length() > 0) {
            // rule - [lastname.f.m]@domain.com
            possibleEmails.add(String.format("%s%s%s", lastName, firstName.charAt(0), middleName1.charAt(0)));
            possibleEmails.add(String.format("%s%s%s", lastName, middleName1.charAt(0), firstName.charAt(0)));
            possibleEmails.add(String.format("%s.%s.%s", lastName, firstName.charAt(0), middleName1.charAt(0)));
            possibleEmails.add(String.format("%s.%s.%s", lastName, middleName1.charAt(0), firstName.charAt(0)));
            // rule - [f.m.lastname]@domain.com
            possibleEmails.add(String.format("%s%s%s", firstName.charAt(0), middleName1.charAt(0), lastName));
            possibleEmails.add(String.format("%s%s%s", middleName1.charAt(0), firstName.charAt(0), lastName));
            possibleEmails.add(String.format("%s.%s.%s", firstName.charAt(0), middleName1.charAt(0), lastName));
            possibleEmails.add(String.format("%s.%s.%s", middleName1.charAt(0), firstName.charAt(0), lastName));
            // rule - [m.lastname]@domain.com
            possibleEmails.add(String.format("%s%s", lastName, middleName1.charAt(0)));
            possibleEmails.add(String.format("%s.%s", lastName, middleName1.charAt(0)));
            possibleEmails.add(String.format("%s%s", middleName1.charAt(0), lastName));
            possibleEmails.add(String.format("%s.%s", middleName1.charAt(0), lastName));
            // rule - [middlename.l]@domain.com
            possibleEmails.add(String.format("%s%s", middleName1, lastName.charAt(0)));
            possibleEmails.add(String.format("%s.%s", middleName1, lastName.charAt(0)));
            possibleEmails.add(String.format("%s%s", lastName.charAt(0), middleName1));
            possibleEmails.add(String.format("%s.%s", lastName.charAt(0), middleName1));
            // rule - [middlename.f]@domain.com
            possibleEmails.add(String.format("%s%s", middleName1, firstName.charAt(0)));
            possibleEmails.add(String.format("%s.%s", middleName1, firstName.charAt(0)));
            possibleEmails.add(String.format("%s%s", firstName.charAt(0), middleName1));
            possibleEmails.add(String.format("%s.%s", firstName.charAt(0), middleName1));
        }

        return possibleEmails;
    }

    private void matchAffiliations(List<AffiliationsDiscoveryResponseItem> responseItems) {
        for (AffiliationsDiscoveryResponseItem responseItem: responseItems) {
            String domain = responseItem.getEmail().split("@")[1];
            String institution = this.getInstitutionName(domain);
            responseItem.setInstitution(institution);
            log.info("Matched affiliation for the email {}: {}", responseItem.getEmail(), responseItem.getInstitution());
        }
        responseItems.removeIf(ri -> ri.getInstitution() == null);
    }

    private String getInstitutionName(String domain) {
        String sql = "SELECT i.name FROM institutions i WHERE i.domain = ?";
        return this.jdbcTemplate.query(
                sql,
                resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getString("name");
                    } else {
                        return null;
                    }
                },
                domain);
    }

    private List<AffiliationsDiscoveryResponseItem> extractEmails(File fullText) throws IOException {
        List<AffiliationsDiscoveryResponseItem> responseItems = new ArrayList<>();
        String text = new String(Files.readAllBytes(fullText.toPath()));
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String email = matcher.group(0);
            AffiliationsDiscoveryResponseItem responseItem = new AffiliationsDiscoveryResponseItem();
            responseItem.setEmail(email);
            responseItems.add(responseItem);
        }
        return responseItems;
    }

    private File getFullText(Integer repoId, Integer coreId) {
        String filePath = FULL_TEXT_PATH_PREFIX + repoId + "/" + coreId + ".txt";
        String filePathOld = FULL_TEXT_PATH_PREFIX_OLD + repoId + "/" + coreId + ".txt";
        File txt = new File(filePath);
        File txtOld = new File(filePathOld);
        if (txt.exists()) {
            return txt;
        } else if (txtOld.exists()) {
            return txtOld;
        } else {
            return null;
        }
    }
}
