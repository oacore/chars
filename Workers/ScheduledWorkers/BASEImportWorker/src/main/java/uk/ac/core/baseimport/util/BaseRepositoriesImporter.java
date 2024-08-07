package uk.ac.core.baseimport.util;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import uk.ac.core.dataprovider.logic.entity.BaseRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static uk.ac.core.baseimport.util.BaseRepositoryField.*;

/**
 * Utility class, the sole purpose of which is to import BASE repositories.
 * <p>
 * All methods in this class are extremely dependant on certain HTML elements of
 * <a href="https://www.base-search.net/about/en/about_sources_date.php">the BASE site</a>.
 * <p>
 * Modifications to the HTML of <a href="https://www.base-search.net/about/en/about_sources_date.php">the BASE site</a>
 * may lead to not working or not working properly methods.
 */
public final class BaseRepositoriesImporter {

    private static final String baseSearchLink = "https://www.base-search.net/about/en/about_sources_date.php?&country=&sort=date&order=desc&search_source=&search_country=&search_date=&show=all";
    private static final String htmlContentsTableId = "TabSources";

    private static final int numberOfHeaderRows = 3;
    private static final int countryCodeCell = 3;

    private static final WebClient client = initClient();

    private static WebClient initClient() {
        final WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getCookieManager().setCookiesEnabled(false);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);
        client.getOptions().setUseInsecureSSL(true);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        return client;
    }

    public BaseRepositoriesImporter() {

    }

    /**
     * Retrieves BASE repositories from <a href="https://www.base-search.net/about/en/about_sources_date.php">https://www.base-search.net/about/en/about_sources_date.php</a>.
     *
     * @param excludedBaseRepositories BASE repositories that have been imported already
     *
     * @return list of BASE repositories
     *
     * @throws IOException when web page cannot be accessed or read
     */
    public Set<BaseRepository> importBaseRepositories(List<BaseRepository> excludedBaseRepositories) throws IOException {

        List<String> baseIdsOfExistingBaseRepositories = excludedBaseRepositories.stream()
                .map(BaseRepository::getBaseId)
                .collect(Collectors.toList());

        HtmlPage contentsPage = client.getPage(baseSearchLink);
        HtmlTable contentsTable = contentsPage.getHtmlElementById(htmlContentsTableId);

        List<HtmlTableRow> contentsTableRows = contentsTable.getRows().stream().skip(numberOfHeaderRows).collect(Collectors.toList());

        return retrieveBaseRepositories(contentsTableRows, baseIdsOfExistingBaseRepositories);
    }

    private Set<BaseRepository> retrieveBaseRepositories(List<HtmlTableRow> htmlTableRows, List<String> excludedBaseIds) {
        Set<BaseRepository> retrievedBaseRepositories = new HashSet<>();
        for (HtmlTableRow row : htmlTableRows) {
            if(isImported(row, excludedBaseIds)) {
                break;
            }
            retrievedBaseRepositories.add(formBaseRepoFromRow(row));
        }
        return retrievedBaseRepositories;
    }

    private boolean isImported(HtmlTableRow row, List<String> excludedBaseIds) {
        Optional<String> baseId = BASE_ID.getFromRow(row.asText());
        return baseId.isPresent() && excludedBaseIds.contains(baseId.get());
    }

    private BaseRepository formBaseRepoFromRow(HtmlTableRow tableRow) {
        BaseRepository baseRepository = new BaseRepository();
        String tableRowAsText = tableRow.asText();

        baseRepository.setBaseId(BASE_ID.getFromRow(tableRowAsText).orElse(null));
        baseRepository.setBaseUrl(BASE_URL.getFromRow(tableRowAsText).orElse(null));
        baseRepository.setUrl(URL.getFromRow(tableRowAsText).orElse(null));
        baseRepository.setName(NAME.getFromRow(tableRowAsText).orElse(null));
        baseRepository.setTotalDocuments(NUMBER_OF_DOCUMENTS.getFromRow(tableRowAsText).isPresent() ?
                Integer.parseInt(NUMBER_OF_DOCUMENTS.getFromRow(tableRowAsText).get()) :
                0);
        baseRepository.setNumberOfDocumentsWithOpenAccess(OPEN_ACCESS.getFromRow(tableRowAsText).isPresent() ?
                Integer.parseInt(OPEN_ACCESS.getFromRow(tableRowAsText).get()) :
                0);
        baseRepository.setSystem(SYSTEM.getFromRow(tableRowAsText).orElse(null));
        baseRepository.setInBaseSince(IN_BASE_SINCE.getFromRow(tableRowAsText).isPresent() ?
                LocalDate.parse(IN_BASE_SINCE.getFromRow(tableRowAsText).get()) :
                null);
        baseRepository.setLatitude(LATITUDE.getFromRow(tableRowAsText).isPresent() ?
                Double.parseDouble(LATITUDE.getFromRow(tableRowAsText).get()) :
                null);
        baseRepository.setLongitude(LONGITUDE.getFromRow(tableRowAsText).isPresent() ?
                Double.parseDouble(LONGITUDE.getFromRow(tableRowAsText).get()) :
                null);
        baseRepository.setCountryCode(getCountryCode(tableRow.getCell(countryCodeCell).asText()).orElse(null));
        baseRepository.setInCoreSince(LocalDate.now());

        return baseRepository;
    }

    private Optional<String> getCountryCode(String targetCell) {
        if (targetCell.isEmpty()) return Optional.empty();
        String newLineDelimiter = "\n";
        String notUsedUkAbbrev = "uk";
        String usedUkAbbrev = "gb";

        String[] countryCodeCellContents = targetCell.split(newLineDelimiter);

        String countryCode = countryCodeCellContents[0];
        if (countryCode.equals(notUsedUkAbbrev)) countryCode = usedUkAbbrev;

        return Optional.of(countryCode.toUpperCase());
    }
}