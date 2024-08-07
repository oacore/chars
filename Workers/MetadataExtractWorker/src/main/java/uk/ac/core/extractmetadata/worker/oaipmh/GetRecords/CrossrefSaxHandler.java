package uk.ac.core.extractmetadata.worker.oaipmh.GetRecords;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.util.TextToDateTime;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.Persist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * Parses an XML document finding MetadataFormats.
 * <p>
 * Once a MetadataFormat is parsed, an OaiMetadataFormat object is created and
 * is given as a parameter to Persist.Persist( )
 *
 * @author samuel
 */
public class CrossrefSaxHandler extends DefaultHandler {

    private final Persist<ArticleMetadata> persistence;
    private ArticleMetadata articleMetadata;

    private Attributes attributes;

    private Boolean insideRecord = false;
    private Boolean insideHeader = false;
    private boolean insideMetadata = false;
    /**
     * Indicates the start of a publication.
     * This is regardless of the type of publication (e.g. conference_paper)
     * Crossref labels the root tag differently, but we can treat them the same
     */
    private boolean insidePublication = false;
    private boolean insideContributors = false;

    /**
     * Indicates the OAI - usually inside the identifier tag in the header
     */
    private Boolean headerIdentifier = false;
    private boolean headerDatestamp = false;

    /**
     * The defined book type - we don't store it in db, but we need to know it for understanding xml context
     *
     * e.g. if <book book_type="monograph"/>, we want to import fields inside <book_series_metadata/>
     *      However, for normal books/journals, we do not want to import inside book_series_metadata
     */
    private String bookType;

    /**
     * Registers - treat as CPU registers.
     * You know what is in them because of context...
     * Helps store data across xml nodes for parsing later
     */
    private Map<String, String> mapRegister;
    private LinkedList<String> linkedListRegister;

    private boolean crmItem = false;
    private boolean givenName;
    private boolean surname;
    private boolean insidePersonName;
    private boolean title;
    private boolean publicationDate;
    private boolean month;
    private boolean day;
    private boolean year;
    private boolean publisherItem;
    private boolean identifier;
    private boolean license;
    private boolean doiData;
    private boolean doiDataDoi;

    /**
     * A list for keeping the publication dates
     * Used for picking the earliest one
     */
    private List<String> publicationDates;
    /**
     * Crossref provides abstracts inside <jats:abstract> tag
     */
    private boolean insideAbstract;


    /**
     * When a MetadataFormat is found, it is given to Persist
     *
     * @param persistence ArticleMetadata to are submitted to this object
     */
    public CrossrefSaxHandler(Persist<ArticleMetadata> persistence) {
        this.persistence = persistence;
        this.publicationDates = new ArrayList<>();
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("record")) {
            this.articleMetadata = new ArticleMetadata();
            this.bookType = "";
            this.insideRecord = true;
        }
        if (qName.equalsIgnoreCase("header")) {
            this.insideHeader = true;
        }
        if (qName.equalsIgnoreCase("metadata")) {
            this.insideMetadata = true;
        }

        if (this.insideHeader) {
            if (qName.equalsIgnoreCase("identifier")) {
                this.headerIdentifier = true;
            }
            if (qName.equalsIgnoreCase("datestamp")) {
                this.headerDatestamp = true;
            }
        }


        if (this.insideMetadata) {
            if (qName.equalsIgnoreCase("crm-item")) {
                this.crmItem = true;
                this.attributes = attributes;
            }

            if (qName.equalsIgnoreCase("book")) {
                this.bookType = attributes.getValue("book_type");
            }

            if (qName.equalsIgnoreCase("conference_paper") ||
                    qName.equalsIgnoreCase("content_item") ||
                    (this.bookType.equals("monograph") && qName.equalsIgnoreCase("book_series_metadata")) ||
                    qName.equalsIgnoreCase("journal_article")
            ) {
                this.insidePublication = true;
            }

            if (this.insidePublication) {
                if (qName.equalsIgnoreCase("contributors")) {
                    this.insideContributors = true;
                }
                if (this.insideContributors) {
                    if (qName.equalsIgnoreCase("person_name")) {
                        this.insidePersonName = true;
                        this.linkedListRegister = new LinkedList<>();
                        this.attributes = attributes;
                        mapRegister = new HashMap<>();
                        mapRegister.put("contributor_role", attributes.getValue("contributor_role"));
                        mapRegister.put("sequence", attributes.getValue("sequence"));
                    }
                    if (this.insidePersonName) {
                        if (qName.equalsIgnoreCase("given_name")) {
                            this.givenName = true;
                        }
                        if (qName.equalsIgnoreCase("surname")) {
                            this.surname = true;
                        }
                    }
                }
                if (qName.equalsIgnoreCase("jats:abstract")) {
                    this.insideAbstract = true;
                }
                if (qName.equalsIgnoreCase("title")) {
                    this.title = true;
                }
                if (qName.equalsIgnoreCase("publication_date")) {
                    this.publicationDate = true;
                    this.mapRegister = new HashMap<>();
                    this.attributes = attributes;
                    this.publicationDates = new ArrayList<>();
                }
                if (this.publicationDate) {
                    if (qName.equalsIgnoreCase("month")) {
                        this.month = true;
                    }
                    if (qName.equalsIgnoreCase("day")) {
                        this.day = true;
                    }
                    if (qName.equalsIgnoreCase("year")) {
                        this.year = true;
                    }
                }
                if (qName.equalsIgnoreCase("publisher_item")) {
                    this.publisherItem = true;
                }
                if (this.publisherItem) {
                    if (qName.equalsIgnoreCase("identifier")) {
                        this.identifier = true;
                        this.attributes = attributes;
                    }
                }

                if (qName.equalsIgnoreCase("doi_data")) {
                    this.doiData = true;
                }
                if (this.doiData) {
                    if (qName.equalsIgnoreCase("doi")) {
                        this.doiDataDoi = true;
                    }
                }

                if (qName.equalsIgnoreCase("ai:license_ref")) {
                    this.license = true;
                }
            }
        }

    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("header")) {
            this.insideHeader = false;
        }

        if (this.insideHeader) {
            if (qName.equalsIgnoreCase("identifier")) {
                this.headerIdentifier = false;
            }
            if (qName.equalsIgnoreCase("datestamp")) {
                this.headerDatestamp = false;
            }
        }

        if (this.insideMetadata) {
            if (qName.equalsIgnoreCase("crm-item")) {
                this.crmItem = false;
                this.attributes = null;
            }
            if (qName.equalsIgnoreCase("conference_paper") ||
                    qName.equalsIgnoreCase("content_item") ||
                    qName.equalsIgnoreCase("journal_article")) {
                this.insidePublication = false;
            }
            if (this.insidePublication) {
                if (qName.equalsIgnoreCase("contributors")) {
                    this.insideContributors = false;
                }
                if (this.insideContributors) {
                    if (qName.equalsIgnoreCase("person_name")) {
                        this.insidePersonName = false;
                        this.articleMetadata.addAuthor(String.join(" ", this.linkedListRegister));
                        this.linkedListRegister = null;
                        this.attributes = null;
                        this.mapRegister = null;
                    }
                    if (this.insidePersonName) {
                        if (qName.equalsIgnoreCase("given_name")) {
                            this.givenName = false;
                        }
                        if (qName.equalsIgnoreCase("surname")) {
                            this.surname = false;
                        }
                    }
                }
                if (qName.equalsIgnoreCase("title")) {
                    this.title = false;
                }
                if (qName.equalsIgnoreCase("jats:abstract")) {
                    this.insideAbstract = false;
                }
                if (qName.equalsIgnoreCase("publication_date")) {
                    StringBuilder dateStringBuilder = new StringBuilder();
                    try {
                        String year = Objects.requireNonNull(this.mapRegister.get("year"));
                        dateStringBuilder.append(year);

                        String month = Objects.requireNonNull(this.mapRegister.get("month"));
                        dateStringBuilder.append("-").append(month);

                        String day = Objects.requireNonNull(this.mapRegister.get("day"));
                        dateStringBuilder.append("-").append(day);
                    } catch (NullPointerException ignored) {}
                    if (!dateStringBuilder.toString().isEmpty()) {
                        this.publicationDates.add(dateStringBuilder.toString());
                    }

                    this.publicationDate = false;
                    this.attributes = null;

                }
                if (this.publicationDate) {
                    if (qName.equalsIgnoreCase("month")) {
                        this.month = false;
                    }
                    if (qName.equalsIgnoreCase("day")) {
                        this.day = false;
                    }
                    if (qName.equalsIgnoreCase("year")) {
                        this.year = false;
                    }
                }
                if (qName.equalsIgnoreCase("publisher_item")) {
                    this.publisherItem = false;
                }
                if (this.publisherItem) {
                    if (qName.equalsIgnoreCase("identifier")) {
                        this.identifier = false;
                        this.attributes = null;
                    }
                }
                if (qName.equalsIgnoreCase("doi_data")) {
                    this.doiData = false;
                }
                if (this.doiData) {
                    if (qName.equalsIgnoreCase("doi")) {
                        this.doiDataDoi = false;
                    }
                }
                if (qName.equalsIgnoreCase("ai:license_ref")) {
                    this.license = false;
                }
            }
        }


        // This indicates the end of a record
        if (qName.equalsIgnoreCase("record")) {
            this.insideRecord = false;

            if (this.persistence != null) {
                this.setEarliestPublicationDate();
                this.persistence.persist(articleMetadata);
            }
            this.articleMetadata = null;
        }
    }

    @Override
    public void characters(char[] ch,
                           int start, int length) throws SAXException {

        String content = new String(ch, start, length).trim();

        if (this.insideHeader) {
            if (this.headerIdentifier) {
                this.articleMetadata.setOAIIdentifier(content);
            } else if (this.headerDatestamp) {
                this.articleMetadata.setDateStamp(new TextToDateTime(content).asUtilDate());
            }
        }
        if (this.insideMetadata) {
            if (this.crmItem) {
                if (attributes.getValue("name").equalsIgnoreCase("publisher-name")) {
                    this.articleMetadata.setPublisher(content);
                }
            }
            if (this.insidePublication) {
                if (this.insideContributors) {
                    if (this.insidePersonName) {
                        if (mapRegister.get("contributor_role").equalsIgnoreCase("author")) {
                            if (this.givenName) {
                                this.linkedListRegister.addFirst(content);
                            }
                            if (this.surname) {
                                this.linkedListRegister.addLast(content);
                            }
                        }
                    }
                }
                if (this.title) {
                    this.articleMetadata.setTitle(content);
                }
                if (this.insideAbstract) {
                    this.articleMetadata.setDescription(content);
                }
                if (this.publicationDate) {
                    if (this.month) {
                        this.mapRegister.put("month", content);
                    }
                    if (this.day) {
                        this.mapRegister.put("day", content);
                    }
                    if (this.year) {
                        this.mapRegister.put("year", content);
                    }
                }
                if (this.publisherItem) {
                    if (this.identifier) {
                        if (attributes.getValue("id_type").equalsIgnoreCase("doi")) {
                            this.articleMetadata.setDoi(content);
                        }
                    }
                }

                if (this.doiData) {
                    if (this.doiDataDoi) {
                        this.articleMetadata.setDoi(content);
                    }
                }

                if (this.license) {
                    this.articleMetadata.setLicense(content);
                }
            }
        }
    }

    private void setEarliestPublicationDate() {
        String earliestPubDate = this.publicationDates.stream()
                .map(s -> s.trim().replaceAll("\\s", ""))
                .min((o1, o2) -> {
                    int year1 = Integer.parseInt(o1.split("-")[0]);
                    int year2 = Integer.parseInt(o2.split("-")[0]);
                    return Integer.compare(year1, year2);
                })
                .orElse(null);
        this.articleMetadata.setDate(earliestPubDate);
    }
}
