package uk.ac.core.rioxxcomplianceworker.rioxx.jaxb_v3;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Author;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Contributor;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Grant;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.LicenseRef;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Publisher;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Relation;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Rioxx;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.entity.Type;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.validation.JAXBClassValidator;
import uk.ac.core.rioxxvalidator.rioxx.jaxb_v3.validation.ValidationReport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JAXBClassValidatorTest {

    @Test
    public void testLicenseRef() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        ValidationReport result = validator.validate(rioxx);
        assertEquals(1, result.getMissingRequiredData().get("licenseRef").size());
        assertTrue(result.getMissingRequiredData().get("licenseRef").contains("licenseRef is missing"));

        LicenseRef licenseRef = new LicenseRef("https://creativecommons.org/licenses/by/4.0", "2014-05-25");
        rioxx.setLicenseRef(Collections.singletonList(licenseRef));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingRequiredData().containsKey("licenseRef"));
        assertFalse(result.getMissingOptionalData().containsKey("licenseRef"));


        licenseRef.setStartDate(null);
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("licenseRef"));
        assertEquals(1, result.getMissingRequiredData().get("licenseRef").size());
        assertTrue(result.getMissingRequiredData().get("licenseRef").contains("Attribute start_date is null"));

        licenseRef.setStartDate("09-11-2021");
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("licenseRef"));
        assertEquals(1, result.getMissingRequiredData().get("licenseRef").size());
        assertTrue(result.getMissingRequiredData().get("licenseRef").contains("start_date doesn't follow format YYYY-MM-DD"));

        List<LicenseRef> licenseRefs = new ArrayList<>();
        licenseRef.setValue("jdvjdls");
        licenseRef.setStartDate("2011-07");
        licenseRefs.add(licenseRef);
        LicenseRef licenseRef1 = new LicenseRef(null, "2011-07-07");
        licenseRefs.add(licenseRef1);
        rioxx.setLicenseRef(licenseRefs);
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("licenseRef"));
        assertEquals(3, result.getMissingRequiredData().get("licenseRef").size());
        assertTrue(result.getMissingRequiredData().get("licenseRef").containsAll(asList(
                "start_date doesn't follow format YYYY-MM-DD",
                "Value is not HTTP(S) URI",
                "Value is empty")));
    }


    @Test
    public void testDescription() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingRequiredData().containsKey("description"));
        assertFalse(result.getMissingOptionalData().containsKey("description"));

        String description = new String("<title>Hello</title>");
        rioxx.setDescription(Collections.singletonList(description));
        result = validator.validate(rioxx);

        assertFalse(result.getMissingRequiredData().containsKey("description"));
        assertTrue(result.getMissingOptionalData().containsKey("description"));
        assertEquals(1, result.getMissingOptionalData().get("description").size());
        assertTrue(result.getMissingOptionalData().get("description").contains("Field description contains markup tags"));
    }

    @Test
    public void testFormat() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("format"));
        assertFalse(result.getMissingRequiredData().containsKey("format"));


        String format = "application/json";
        rioxx.setFormat(Collections.singletonList(format));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("format"));
        assertFalse(result.getMissingRequiredData().containsKey("format"));

        format = "Hello";
        rioxx.setFormat(Collections.singletonList(format));
        result = validator.validate(rioxx);
        assertTrue(result.getMissingRequiredData().containsKey("format"));
        assertFalse(result.getMissingOptionalData().containsKey("format"));
        assertEquals(1, result.getMissingRequiredData().get("format").size());
        assertTrue(result.getMissingRequiredData().get("format").contains("Format is not correct"));

        List<String> formats = new ArrayList<>();
        formats.add("application/json");
        formats.add("Hello");
        rioxx.setFormat(formats);
        result = validator.validate(rioxx);
        assertTrue(result.getMissingRequiredData().containsKey("format"));
        assertFalse(result.getMissingOptionalData().containsKey("format"));
        assertEquals(1, result.getMissingRequiredData().get("format").size());
        assertTrue(result.getMissingRequiredData().get("format").contains("format should not be greater then 1"));
    }

    @Test
    public void testIdentifier() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("identifier"));
        assertTrue(result.getMissingRequiredData().containsKey("identifier"));
        assertTrue(result.getMissingRequiredData().get("identifier").contains("identifier is missing"));

        rioxx.setIdentifier(new String("http://oro.open.ac.uk/84523/1/Three%20participatory%20geographers_SCG_final%20version.docx"));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("identifier"));
        assertFalse(result.getMissingRequiredData().containsKey("identifier"));

        rioxx.setIdentifier(new String("geographers_SCG_final%20version.docx"));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("identifier"));
        assertTrue(result.getMissingRequiredData().containsKey("identifier"));
        assertTrue(result.getMissingRequiredData().get("identifier").contains("Identifier is not a HTTP(S) URI"));
    }

    @Test
    public void testLanguage() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        List<String> languages = new ArrayList<>();
        languages.add(new String("eng"));
        languages.add(new String("English"));
        rioxx.setLanguage(languages);
        ValidationReport result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("language"));
        assertTrue(result.getMissingRequiredData().containsKey("language"));
        assertEquals(1, result.getMissingRequiredData().get("language").size());
        assertTrue(result.getMissingRequiredData().get("language").contains("Language doesn't conform to ISO 639-3"));

        rioxx.setLanguage(Collections.singletonList(new String("eng")));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("language"));
        assertFalse(result.getMissingRequiredData().containsKey("language"));
    }

    @Test
    public void testPublisher() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        Publisher publisher = new Publisher("Public Library of Science", "https://isni.org/isni/40482455X");
        rioxx.setPublisher(Collections.singletonList(publisher));
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("publisher"));
        assertFalse(result.getMissingRequiredData().containsKey("publisher"));

        publisher = new Publisher("Public Library of Science", "blabla");
        rioxx.setPublisher(Collections.singletonList(publisher));
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("publisher"));
        assertFalse(result.getMissingRequiredData().containsKey("publisher"));
        assertEquals(1, result.getMissingOptionalData().get("publisher").size());
        assertTrue(result.getMissingOptionalData().get("publisher").contains("Attribute Uri for Publisher is not HTTP(S) URI"));

        List<Publisher> publishers = new ArrayList<>();
        publishers.add(new Publisher("Public Library of Science", null));
        publishers.add(new Publisher(null, "blabla"));
        rioxx.setPublisher(publishers);
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("publisher"));
        assertFalse(result.getMissingRequiredData().containsKey("publisher"));
        assertEquals(2, result.getMissingOptionalData().get("publisher").size());
        assertTrue(result.getMissingOptionalData().get("publisher").contains("Attribute Uri for Publisher is not HTTP(S) URI"));
        assertTrue(result.getMissingOptionalData().get("publisher").contains("Attribute Uri for Publisher is empty"));
    }

    @Test
    public void testRelation() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        Relation relation = new Relation("https://doi.org/10.5281/zenodo.3538919", "test",
                "2022-01-13", "2022-01-13");
        rioxx.setRelation(Collections.singletonList(relation));
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("relation"));
        assertFalse(result.getMissingRequiredData().containsKey("relation"));

        List<Relation> relations = new ArrayList<>();
        relations.add(new Relation("notLink", "test", "2022-01-13", "2022-01-13"));
        relations.add(new Relation("https:", "test", "2022-01-13", "20-01-2013"));
        relations.add(new Relation("https:", "test", "20-01-2013", "2022-01-13"));
        relations.add(new Relation(null, null, null, null));
        rioxx.setRelation(relations);
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("relation"));
        assertFalse(result.getMissingRequiredData().containsKey("relation"));
        assertEquals(7, result.getMissingOptionalData().get("relation").size());
    }

    @Test
    public void testSource() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setSource(new String("1360-2241"));
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("source"));
        assertFalse(result.getMissingRequiredData().containsKey("source"));
    }

    @Test
    public void testDateAccepted() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setDateAccepted(new String("2022-01-01"));
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("dateAccepted"));
        assertFalse(result.getMissingRequiredData().containsKey("dateAccepted"));

        rioxx.setDateAccepted(new String("wrong"));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("dateAccepted"));
        assertTrue(result.getMissingRequiredData().containsKey("dateAccepted"));
        assertTrue(result.getMissingRequiredData().get("dateAccepted").contains("date_accepted doesn't follow format YYYY-MM-DD"));
    }

    @Test
    public void testAuthor() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        Author author = new Author("Denning, Stephanie", "https://orcid.org/0000-0001-5305-9450", true);
        rioxx.setAuthor(Collections.singletonList(author));
        ValidationReport result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("author"));
        assertFalse(result.getMissingRequiredData().containsKey("author"));

        List<Author> authors = new ArrayList<>();
        authors.add(new Author("Denning", null, false));
        authors.add(new Author("Maria", "https://orcid.org/0000-0001-5305-9450", false));
        rioxx.setAuthor(authors);
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("author"));
        assertFalse(result.getMissingRequiredData().containsKey("author"));
        assertEquals(2, result.getMissingOptionalData().get("author").size());
        assertTrue(result.getMissingOptionalData().get("author").contains("Author contains empty URI attribute"));
        assertTrue(result.getMissingOptionalData().get("author").contains("No author is marked as main author"));
    }

    @Test
    public void testContributor() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        Contributor contributor = new Contributor("Milgrom, Paul", "https://orcid.org/0000-0002-1919-4138");
        rioxx.setContributor(Collections.singletonList(contributor));
        ValidationReport result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("contributor"));
        assertFalse(result.getMissingRequiredData().containsKey("contributor"));

        List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("Mari", "notUrl"));
        contributors.add(new Contributor("Mari1", "notUrl1"));
        contributors.add(new Contributor("Mari", null));
        rioxx.setContributor(contributors);
        result = validator.validate(rioxx);

        assertTrue(result.getMissingOptionalData().containsKey("contributor"));
        assertFalse(result.getMissingRequiredData().containsKey("contributor"));
        assertEquals(2, result.getMissingOptionalData().get("contributor").size());
        assertTrue(result.getMissingOptionalData().get("contributor").contains("Contributor contains empty URI attribute"));
        assertTrue(result.getMissingOptionalData().get("contributor").contains("URI for Contributor is not a HTTP(S) URI"));
    }

    @Test
    public void testGrant() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        Grant grant = new Grant("https://doi.org/10.35", "Welcome", "https://isni.org/isni/00");
        rioxx.setGrant(Collections.singletonList(grant));
        ValidationReport result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("grant"));
        assertFalse(result.getMissingRequiredData().containsKey("grant"));

        List<Grant> grants = new ArrayList<>();
        grants.add(new Grant(null, "Welcome", "https://isni.org/isni/00"));
        grants.add(new Grant(null, "Welcome", "https://isni.org/isni/00"));
        grants.add(new Grant("notUrl", null, null));
        grants.add(new Grant("https://isni.org/isni/00", "Welcome", "notUrl"));
        rioxx.setGrant(grants);
        result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("grant"));
        assertTrue(result.getMissingRequiredData().containsKey("grant"));
        assertEquals(4, result.getMissingRequiredData().get("grant").size());
    }

    @Test
    public void testProject() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();
        rioxx.setProject(Collections.singletonList(new String("Test")));
        ValidationReport result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("project"));
        assertFalse(result.getMissingRequiredData().containsKey("project"));

        List<String> projects = new ArrayList<>();
        projects.add(new String("test1"));
        projects.add("");
        rioxx.setProject(projects);
        result = validator.validate(rioxx);

        assertFalse(result.getMissingOptionalData().containsKey("project"));
        assertTrue(result.getMissingRequiredData().containsKey("project"));
        assertEquals(1, result.getMissingRequiredData().get("project").size());
        assertTrue(result.getMissingRequiredData().get("project").contains("Project content is empty"));
    }

    @Test
    public void testPublicationDate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setPublicationDate(new String("2022-01-01"));
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("publicationDate"));
        assertFalse(result.getMissingRequiredData().containsKey("publicationDate"));

        rioxx.setPublicationDate(new String("wrong"));
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("publicationDate"));
        assertFalse(result.getMissingRequiredData().containsKey("publicationDate"));
        assertTrue(result.getMissingOptionalData().get("publicationDate").contains("publication_date doesn't follow format YYYY-MM-DD"));

        rioxx.setPublicationDate(new String("2022-12"));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("publicationDate"));
        assertFalse(result.getMissingRequiredData().containsKey("publicationDate"));
    }

    @Test
    public void testRecordPublicReleaseDate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setRecordPublicReleaseDate(new String("2022-01-01"));
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("recordPublicReleaseDate"));
        assertFalse(result.getMissingRequiredData().containsKey("recordPublicReleaseDate"));

        rioxx.setRecordPublicReleaseDate(new String("wrong"));
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("recordPublicReleaseDate"));
        assertFalse(result.getMissingRequiredData().containsKey("recordPublicReleaseDate"));
        assertTrue(result.getMissingOptionalData().get("recordPublicReleaseDate").contains("record_public_release_date " +
                "doesn't follow format YYYY-MM-DD"));

        rioxx.setRecordPublicReleaseDate(new String("2022-12"));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("recordPublicReleaseDate"));
        assertFalse(result.getMissingRequiredData().containsKey("recordPublicReleaseDate"));
    }

    @Test
    public void testType() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setType(Collections.singletonList(new Type("Journal Article/Review",
                "https://purl.org/coar/resource_type/c_5794")));

        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("type"));
        assertFalse(result.getMissingRequiredData().containsKey("type"));

        rioxx.setType(Collections.singletonList(new Type("Journal Article/Review", "notUrl")));
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("type"));
        assertTrue(result.getMissingRequiredData().containsKey("type"));
        assertTrue(result.getMissingRequiredData().get("type").contains("Uri is not HTTP(S) URI"));
    }

    @Test
    public void testVersion() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setVersion("AO");
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("version"));
        assertFalse(result.getMissingRequiredData().containsKey("version"));


        rioxx.setVersion("Wrong");
        result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("version"));
        assertTrue(result.getMissingRequiredData().containsKey("version"));
        assertTrue(result.getMissingRequiredData().get("version").contains("Version is not from possible Versions"));
    }

    @Test
    public void testVersionOfRecord() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JAXBClassValidator validator = new JAXBClassValidator();
        Rioxx rioxx = new Rioxx();

        rioxx.setVersionOfRecord("https://");
        ValidationReport result = validator.validate(rioxx);
        assertFalse(result.getMissingOptionalData().containsKey("versionOfRecord"));
        assertFalse(result.getMissingRequiredData().containsKey("versionOfRecord"));

        rioxx.setVersionOfRecord("not url");
        result = validator.validate(rioxx);
        assertTrue(result.getMissingOptionalData().containsKey("versionOfRecord"));
        assertFalse(result.getMissingRequiredData().containsKey("versionOfRecord"));
        assertTrue(result.getMissingOptionalData().get("versionOfRecord").contains("Property doesn't contain HTTP(S) URI"));
    }

    @Test
    public void test() throws IOException, JAXBException {
        String rioxxRecord =
                "<dc:description>This paper advances</dc:description>\n" +
                        "  <dc:format>application/vnd.openxmlformats-officedocument.wordprocessingml.document</dc:format>\n" +
                        "  <dc:identifier>http://oro.open.ac.uk/84523/1/Three%20participatory%20geographers_SCG_final%20version.docx</dc:identifier>\n" +
                        "  <dc:language>en</dc:language>\n" +
                        "  <dc:source>Social and Cultural Geography</dc:source>\n" +
                        "  <dc:title>Three Participatory Geographers: Reflections on Positionality And Working With Participants in Researching Religions, Spiritualities, And Faith. Social and Cultural Geography</dc:title>\n" +
                        "  <dcterms:date_accepted>2020-07-14</dcterms:date_accepted>\n" +
                        "  <rioxxterms:author uri=\"https://orcid.org/0000-0001-5305-9450\" first-named-author=\"true\">Denning, Stephanie</rioxxterms:author>\n" +
                        "  <rioxxterms:author>Scriven, Richard</rioxxterms:author>\n" +
                        "  <rioxxterms:author id=\"http://orcid.org/0000-0001-5590-1410\">Slatter, Ruth</rioxxterms:author>\n" +
                        "  <rioxxterms:publication_date>2022</rioxxterms:publication_date>\n" +
                        "  <rioxxterms:type uri=\"https://purl.org/coar/resource_type/c_5794\">Journal Article/Review</rioxxterms:type>\n" +
                        "  <rioxxterms:version>AM</rioxxterms:version>\n" +
                        "  <rioxxterms:version_of_record>http://dx.doi.org/10.1080/14649365.2020.1815826</rioxxterms:version_of_record>\n" +
                        "  <ali:license_ref start_date=\"2020-11-17\">https://creativecommons.org/licenses/by/4.0</ali:license_ref>\n" +
                        "  <dc:coverage>NL</dc:coverage>\n" +
                        "  <dc:publisher uri=\"https://isni.org/isni/000000040482455X\">Public Library of Science</dc:publisher>\n" +
                        "  <dc:relation type=\"https://schema.org/DataSet\" deposit_date=\"2022-01-13\" resource_exposed_date=\"2022-01-20\">https://doi.org/10.5281/zenodo.3538919</dc:relation>\n" +
                        "  <dc:source>1360-2241</dc:source>\n" +
                        "  <dc:subject>MusIns</dc:subject>\n" +
                        "  <rioxxterms:contributor uri=\"https://orcid.org/0000-0002-1919-4138\">Milgrom, Paul</rioxxterms:contributor>\n" +
                        "  <rioxxterms:grant funder_name=\"Wellcome Trust\" funder_id=\"https://isni.org/isni/0000000404277672\">https://doi.org/10.35802/218671</rioxxterms:grant>\n" +
                        "  <rioxxterms:record_public_release_date>2020-10-02</rioxxterms:record_public_release_date>\n" +
                        "  <rioxxterms:version_of_record>https://doi.org/10.1103/PhysRevD.102.043015</rioxxterms:version_of_record>";
        JAXBContext jaxbContext = JAXBContext.newInstance(Rioxx.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Source xmlFile = new StreamSource(IOUtils.toInputStream( RIOXX_START_TAG_V3 + rioxxRecord + RIOXX_END_TAG, "UTF-8"));
        Rioxx instance = (Rioxx) jaxbUnmarshaller.unmarshal(xmlFile);
        System.out.println(instance);
        System.out.println(instance.getDescription());
    }

    @Test
    public void testExample1() throws IOException, JAXBException {
        String rioxxRecord =
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                        "<rioxx xsi:schemaLocation=\"http://www.rioxx.net/schema/v3.0/rioxx/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:rioxxterms=\"http://docs.rioxx.net/schema/v3.0/rioxxterms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                        "    <dc:description>YouTube has been implicated in the transformation of users into extremists and conspiracy theorists. The alleged mechanism for this radicalizing process is YouTube’s recommender system, which is optimized to amplify and promote clips that users are likely to watch through to the end. YouTube optimizes for watch-through for economic reasons: people who watch a video through to the end are likely to then watch the next recommended video as well, which means that more advertisements can be served to them. This is a seemingly innocuous design choice, but it has a troubling side-effect. Critics of YouTube have alleged that the recommender system tends to recommend extremist content and conspiracy theories, as such videos are especially likely to capture and keep users’ attention. To date, the problem of radicalization via the YouTube recommender system has been a matter of speculation. The current study represents the first systematic, pre-registered attempt to establish whether and to what extent the recommender system tends to promote such content. We begin by contextualizing our study in the framework of technological seduction. Next, we explain our methodology. After that, we present our results, which are consistent with the radicalization hypothesis. Finally, we discuss our findings, as well as directions for future research and recommendations for users, industry, and policy-makers..</dc:description>\n" +
                        "    \n" +
                        "    <dc:language>en</dc:language>\n" +
                        "    \n" +
                        "    <rioxxterms:publisher>\n" +
                        "        <rioxxterms:name>Springer</rioxxterms:name>\n" +
                        "        <rioxxterms:id>https://isni.org/isni/0000000460111909</rioxxterms:id>\n" +
                        "    </rioxxterms:publisher>\n" +
                        "    \n" +
                        "    <dc:source>0039-7857</dc:source>\n" +
                        "    \n" +
                        "    <dc:title>Technologically scaffolded atypical cognition: the case of YouTube’s recommender system</dc:title>\n" +
                        "    \n" +
                        "    <dcterms:dateAccepted>2020-05-27</dcterms:dateAccepted>\n" +
                        "    <rioxxterms:creator\n" +
                        "           >\n" +
                        "        <rioxxterms:name>Alfano, Mark</rioxxterms:name>\n" +
                        "        <rioxxterms:id>https://viaf.org/viaf/8232163464412905680007</rioxxterms:id>\n" +
                        "    </rioxxterms:creator>\n" +
                        "    <rioxxterms:creator>\n" +
                        "        <rioxxterms:name>Fard, Amir Ebrahimi</rioxxterms:name>\n" +
                        "    </rioxxterms:creator>\n" +
                        "    <rioxxterms:creator>\n" +
                        "        <rioxxterms:name>Carter, J. Adam</rioxxterms:name>\n" +
                        "        <rioxxterms:id>http://orcid.org/0000-0002-1222-8331</rioxxterms:id>\n" +
                        "        <rioxxterms:id>isni/0000000452130579</rioxxterms:id>\n" +
                        "    </rioxxterms:creator>\n" +
                        "    <rioxxterms:creator>\n" +
                        "        <rioxxterms:name>Clutton, Peter</rioxxterms:name>\n" +
                        "    </rioxxterms:creator>\n" +
                        "    <rioxxterms:creator>\n" +
                        "        <rioxxterms:name>Klein, Colin</rioxxterms:name>\n" +
                        "    </rioxxterms:creator>\n" +
                        "    <rioxxterms:publication_date>2021-12</rioxxterms:publication_date>\n" +
                        "    \n" +
                        "    <rioxxterms:record_public_release_date>2020-06-11</rioxxterms:record_public_release_date>\n" +
                        "    \n" +
                        "    <dc:type>https://purl.org/coar/resource_type/c_2df8fbb1</dc:type>\n" +
                        "    \n" +
                        "    <rioxxterms:grant\n" +
                        "            funder_name=\"Australian Research Council\"\n" +
                        "            funder_id=\"https://ror.org/05mmh0f86\">\n" +
                        "        DP190101507\n" +
                        "    </rioxxterms:grant>\n" +
                        "    \n" +
                        "    <rioxxterms:grant\n" +
                        "            funder_name=\"John Templeton Foundation\"\n" +
                        "            funder_id=\"https://ror.org/035tnyy05\">\n" +
                        "        61387\n" +
                        "    </rioxxterms:grant>\n" +
                        "    <dc:identifier>https://eprints.gla.ac.uk/217807/</dc:identifier>\n" +
                        "    <dc:relation rel=\"item\" type=\"application/pdf\"\n" +
                        "            coar_version=\"https://purl.org/coar/version/c_ab4af688f83e57aa\"\n" +
                        "            coar_type=\"https://purl.org/coar/resource_type/c_6501\"\n" +
                        "            deposit_date=\"2023-03-28\"\n" +
                        "            resource_exposed_date=\"2023-03-28\"\n" +
                        "            access_rights_=\"https://purl.org/coar/access_right/c_abf2\"\n" +
                        "            license_ref=\"https://creativecommons.org/licenses/by-nc-nd/4.0/\">\n" +
                        "        https://eprints.gla.ac.uk/217807/7/217807.pdf\n" +
                        "    </dc:relation>\n" +
                        "    \n" +
                        "    <dc:relation rel=\"cite-as\">https://oai.core.ac.uk/oai:eprints.gla.ac.uk:217807</dc:relation>\n" +
                        "    \n" +
                        "    <rioxxterms:ext_relation\n" +
                        "            rel=\"cite-as\"\n" +
                        "            coar_type=\"https://purl.org/coar/resource_type/c_6501\"\n" +
                        "            coar_version=\"https://purl.org/coar/version/c_970fb48d4fbd8a85\">\n" +
                        "        https://doi.org/10.1007/s11229-020-02724-x\n" +
                        "    </rioxxterms:ext_relation>\n" +
                        "    <rioxxterms:ext_relation\n" +
                        "            rel=\"cite-as\"\n" +
                        "            coar_type=\"https://purl.org/coar/resource_type/c_ddb1\">\n" +
                        "        https://doi.org/10.15129/589f7af3-26b3-4a93-b042-fbc8100fc977\n" +
                        "    </rioxxterms:ext_relation>\n" +
                        "</rioxx>";
        JAXBContext jaxbContext = JAXBContext.newInstance(Rioxx.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Source xmlFile = new StreamSource(IOUtils.toInputStream( rioxxRecord, "UTF-8"));
        Rioxx instance = (Rioxx) jaxbUnmarshaller.unmarshal(xmlFile);
        System.out.println(instance);
        System.out.println(instance.getDescription());
    }

    @Test
    public void testV2() throws IOException, JAXBException {
        String rioxxRecord =
                "<dc:description>This paper advances</dc:description>\n" +
                        "  <dc:format>application/vnd.openxmlformats-officedocument.wordprocessingml.document</dc:format>\n" +
                        "  <dc:identifier>http://oro.open.ac.uk/84523/1/Three%20participatory%20geographers_SCG_final%20version.docx</dc:identifier>\n" +
                        "  <dc:language>en</dc:language>\n" +
                        "  <dc:source>Social and Cultural Geography</dc:source>\n" +
                        "  <dc:title>Three Participatory Geographers: Reflections on Positionality And Working With Participants in Researching Religions, Spiritualities, And Faith. Social and Cultural Geography</dc:title>\n" +
                        "  <dcterms:date_accepted>2020-07-14</dcterms:date_accepted>\n" +
                        "  <rioxxterms:author uri=\"https://orcid.org/0000-0001-5305-9450\" first-named-author=\"true\">Denning, Stephanie</rioxxterms:author>\n" +
                        "  <rioxxterms:author>Scriven, Richard</rioxxterms:author>\n" +
                        "  <rioxxterms:author id=\"http://orcid.org/0000-0001-5590-1410\">Slatter, Ruth</rioxxterms:author>\n" +
                        "  <rioxxterms:publication_date>2022</rioxxterms:publication_date>\n" +
                        "  <rioxxterms:type uri=\"https://purl.org/coar/resource_type/c_5794\">Journal Article/Review</rioxxterms:type>\n" +
                        "  <rioxxterms:version>AM</rioxxterms:version>\n" +
                        "  <rioxxterms:version_of_record>http://dx.doi.org/10.1080/14649365.2020.1815826</rioxxterms:version_of_record>\n" +
                        "  <ali:license_ref start_date=\"2020-11-17\">https://creativecommons.org/licenses/by/4.0</ali:license_ref>\n" +
                        "  <dc:coverage>NL</dc:coverage>\n" +
                        "  <dc:publisher uri=\"https://isni.org/isni/000000040482455X\">Public Library of Science</dc:publisher>\n" +
                        "  <dc:relation type=\"https://schema.org/DataSet\" deposit_date=\"2022-01-13\" resource_exposed_date=\"2022-01-20\">https://doi.org/10.5281/zenodo.3538919</dc:relation>\n" +
                        "  <dc:source>1360-2241</dc:source>\n" +
                        "  <dc:subject>MusIns</dc:subject>\n" +
                        "  <rioxxterms:contributor uri=\"https://orcid.org/0000-0002-1919-4138\">Milgrom, Paul</rioxxterms:contributor>\n" +
                        "  <rioxxterms:grant funder_name=\"Wellcome Trust\" funder_id=\"https://isni.org/isni/0000000404277672\">https://doi.org/10.35802/218671</rioxxterms:grant>\n" +
                        "  <rioxxterms:record_public_release_date>2020-10-02</rioxxterms:record_public_release_date>\n" +
                        "  <rioxxterms:version_of_record>https://doi.org/10.1103/PhysRevD.102.043015</rioxxterms:version_of_record>";
        JAXBContext jaxbContext = JAXBContext.newInstance(uk.ac.core.rioxxcomplianceworker.rioxx.jaxb_v2.jaxb.Rioxx.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Source xmlFile = new StreamSource(IOUtils.toInputStream( RIOXX_START_TAG_V2 + rioxxRecord + RIOXX_END_TAG, "UTF-8"));
        uk.ac.core.rioxxcomplianceworker.rioxx.jaxb_v2.jaxb.Rioxx instance = (uk.ac.core.rioxxcomplianceworker.rioxx.jaxb_v2.jaxb.Rioxx) jaxbUnmarshaller.unmarshal(xmlFile);
        System.out.println(instance);
        System.out.println(instance.getDescription());
    }
}
