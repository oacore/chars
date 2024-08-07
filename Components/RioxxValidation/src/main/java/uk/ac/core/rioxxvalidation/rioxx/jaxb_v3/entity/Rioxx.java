//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.03 at 04:39:08 PM BST 
//
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}apc" minOccurs="0"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}author"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}contributor" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}coverage" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/terms/}dateAccepted"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}description" minOccurs="0"/>
 *         &lt;element ref="{http://ali.niso.org/2014/ali/1.0}free_to_read" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}format" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}language"/>
 *         &lt;element ref="{http://ali.niso.org/2014/ali/1.0}license_ref"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}project"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}publication_date" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}publisher" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}relation" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}source" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}subject" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}type"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}version"/>
 *         &lt;element ref="{http://www.rioxx.net/schema/v2.0/rioxxterms/}version-of-record" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "rioxx")
public class Rioxx {


    @XmlElement(name = "coverage", namespace = "http://purl.org/dc/elements/1.1/", required = false)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<String> coverage;

    @XmlElement(name = "description", namespace = "http://purl.org/dc/elements/1.1/", type = String.class, required = false)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<String> description;

    @XmlElement(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    @RioxxCompliance_v3(minOccur = "1", maxOccur = "1")
    protected String identifier;

    @XmlElement(name = "language", namespace = "http://purl.org/dc/elements/1.1/")
    @RioxxCompliance_v3(minOccur = "1")
    protected List<String> language;

    @XmlElement(name = "relation", namespace = "http://purl.org/dc/elements/1.1/", required = false)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<Relation> relation;

    @XmlElement(name = "source", namespace = "http://purl.org/dc/elements/1.1/", required = false)
    @RioxxCompliance_v3(minOccur = "0", maxOccur = "1")
    protected String source;

    @XmlElement(name = "subject", namespace = "http://purl.org/dc/elements/1.1/", required = false)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<String> subject;

    @XmlElement(name = "title", namespace = "http://purl.org/dc/elements/1.1/")
    @RioxxCompliance_v3(minOccur = "1", maxOccur = "1")
    protected String title;

    @XmlElement(name = "type", namespace = "http://purl.org/dc/elements/1.1/")
    @RioxxCompliance_v3(minOccur = "1", maxOccur = "1")
    protected String type;

    @XmlElement(name= "dateAccepted", namespace = "http://purl.org/dc/terms/", required = false)
    @RioxxCompliance_v3(minOccur = "0", maxOccur = "1")
    protected String dateAccepted;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/", required = true)
    @RioxxCompliance_v3(minOccur = "1")
    protected List<Creator> creator;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    @RioxxCompliance_v3(minOccur = "0")
    protected List<Contributor> contributor;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/", required = false)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<ExtRelation> ext_relation;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    @RioxxCompliance_v3(minOccur = "0")
    protected List<Grant> grant;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/", required = true)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<String> project;

    @XmlElement(name = "publication_date", namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    @RioxxCompliance_v3(minOccur = "0")
    protected String publicationDate;


    @XmlElement(name = "publisher", namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/", required = false)
    @RioxxCompliance_v3(minOccur = "0")
    protected List<Publisher> publisher;

    @XmlElement(name = "record_public_release_date", namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    @RioxxCompliance_v3(minOccur = "0", maxOccur = "1")
    protected String recordPublicReleaseDate;


    public List<String> getCoverage() {
        return coverage;
    }

    public void setCoverage(List<String> coverage) {
        this.coverage = coverage;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<Relation> getRelation() {
        return relation;
    }

    public void setRelation(List<Relation> relation) {
        this.relation = relation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(String dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public List<Creator> getCreator() {
        return creator;
    }

    public void setCreator(List<Creator> creator) {
        this.creator = creator;
    }

    public List<Contributor> getContributor() {
        return contributor;
    }

    public void setContributor(List<Contributor> contributor) {
        this.contributor = contributor;
    }

    public List<ExtRelation> getExt_relation() {
        return ext_relation;
    }

    public void setExt_relation(List<ExtRelation> ext_relation) {
        this.ext_relation = ext_relation;
    }

    public List<Grant> getGrant() {
        return grant;
    }

    public void setGrant(List<Grant> grant) {
        this.grant = grant;
    }

    public List<String> getProject() {
        return project;
    }

    public void setProject(List<String> project) {
        this.project = project;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<Publisher> getPublisher() {
        return publisher;
    }

    public void setPublisher(List<Publisher> publisher) {
        this.publisher = publisher;
    }

    public String getRecordPublicReleaseDate() {
        return recordPublicReleaseDate;
    }

    public void setRecordPublicReleaseDate(String recordPublicReleaseDate) {
        this.recordPublicReleaseDate = recordPublicReleaseDate;
    }

    @Override
    public String toString() {
        return "Rioxx{" +
                "coverage=" + coverage +
                ", description=" + description +
                ", identifier='" + identifier + '\'' +
                ", language=" + language +
                ", relation=" + relation +
                ", source='" + source + '\'' +
                ", subject=" + subject +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", dateAccepted='" + dateAccepted + '\'' +
                ", creators=" + creator +
                ", contributor=" + contributor +
                ", ext_relation=" + ext_relation +
                ", grant=" + grant +
                ", project=" + project +
                ", publicationDate='" + publicationDate + '\'' +
                ", publisher=" + publisher +
                ", recordPublicReleaseDate='" + recordPublicReleaseDate + '\'' +
                '}';
    }
}
