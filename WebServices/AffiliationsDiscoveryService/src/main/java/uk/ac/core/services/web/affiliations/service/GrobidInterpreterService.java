package uk.ac.core.services.web.affiliations.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.core.services.web.affiliations.exception.GrobidExtractionException;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidAddress;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidAffiliation;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidAuthor;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidOrganization;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrobidInterpreterService {
    private static final Logger logger = LoggerFactory.getLogger(GrobidInterpreterService.class);

    public List<GrobidAuthor> parseTei(File teiFile) throws GrobidExtractionException {
        if (!teiFile.exists()) {
            throw new GrobidExtractionException("File " + teiFile.getPath() + " does not exist");
        }
        logger.info("File {} exists, ready to process ...", teiFile.getPath());

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(teiFile);
            doc.getDocumentElement().normalize();

            List<GrobidAuthor> result = new ArrayList<>();
            NodeList xmlAuthors = doc.getElementsByTagName("author");
            for (int i = 0; i < xmlAuthors.getLength(); i++) {
                GrobidAuthor grobidAuthor = new GrobidAuthor();
                Element xmlAuthor;
                if (xmlAuthors.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    xmlAuthor = (Element) xmlAuthors.item(i);
                } else {
                    continue;
                }
                grobidAuthor.setRole(xmlAuthor.getAttribute("role"));
                NodeList xmlAuthorChildren = xmlAuthor.getChildNodes();
                for (int j = 0; j < xmlAuthorChildren.getLength(); j++) {
                    Element xmlAuthorChild;
                    if (xmlAuthorChildren.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        xmlAuthorChild = (Element) xmlAuthorChildren.item(j);
                    } else {
                        continue;
                    }
                    // set full name
                    if (xmlAuthorChild.getTagName().equals("persName")) {
                        NodeList xmlAuthorNames = xmlAuthorChild.getChildNodes();
                        List<String> authorNames = new ArrayList<>();
                        for (int k = 0; k < xmlAuthorNames.getLength(); k++) {
                            Element xmlAuthorName;
                            if (xmlAuthorNames.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                xmlAuthorName = (Element) xmlAuthorNames.item(k);
                            } else {
                                continue;
                            }
                            authorNames.add(xmlAuthorName.getTextContent());
                        }
                        grobidAuthor.setFullName(
                                authorNames.stream()
                                        .reduce((s, s2) -> s + " " + s2)
                                        .orElseThrow(GrobidExtractionException::new)
                        );
                    }
                    // set email
                    if (xmlAuthorChild.getTagName().equals("email")) {
                        grobidAuthor.setEmail(xmlAuthorChild.getTextContent());
                    }
                    // set affiliations
                    if (xmlAuthorChild.getTagName().equals("affiliation")) {
                        GrobidAffiliation affiliation = new GrobidAffiliation();
                        affiliation.setKey(xmlAuthorChild.getAttribute("key"));
                        NodeList xmlAffiliationChildren = xmlAuthorChild.getChildNodes();
                        for (int k = 0; k < xmlAffiliationChildren.getLength(); k++) {
                            Element xmlAffiliationChild;
                            if (xmlAffiliationChildren.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                xmlAffiliationChild = (Element) xmlAffiliationChildren.item(k);
                            } else {
                                continue;
                            }
                            if (xmlAffiliationChild.getTagName().equals("orgName")) {
                                GrobidOrganization organization = new GrobidOrganization();
                                organization.setKey(xmlAffiliationChild.getAttribute("key"));
                                organization.setType(xmlAffiliationChild.getAttribute("type"));
                                organization.setName(xmlAffiliationChild.getTextContent());
                                affiliation.getOrganizations().add(organization);
                            }
                            if (xmlAffiliationChild.getTagName().equals("address")) {
                                NodeList xmlAddressChildren = xmlAffiliationChild.getChildNodes();
                                for (int l = 0; l < xmlAddressChildren.getLength(); l++) {
                                    Element xmlAddressChild;
                                    if (xmlAddressChildren.item(l).getNodeType() == Node.ELEMENT_NODE) {
                                        xmlAddressChild = (Element) xmlAddressChildren.item(l);
                                    } else {
                                        continue;
                                    }
                                    GrobidAddress address = new GrobidAddress();
                                    if (xmlAddressChild.getTagName().equals("addrLine")) {
                                        address.setAddressLine(xmlAddressChild.getTextContent());
                                    }
                                    if (xmlAddressChild.getTagName().equals("postCode")) {
                                        address.setPostCode(xmlAddressChild.getTextContent());
                                    }
                                    if (xmlAddressChild.getTagName().equals("postBox")) {
                                        address.setPostBox(xmlAddressChild.getTextContent());
                                    }
                                    if (xmlAddressChild.getTagName().equals("settlement")) {
                                        address.setSettlement(xmlAddressChild.getTextContent());
                                    }
                                    if (xmlAddressChild.getTagName().equals("region")) {
                                        address.setRegion(xmlAddressChild.getTextContent());
                                    }
                                    if (xmlAddressChild.getTagName().equals("country")) {
                                        address.setCountry(xmlAddressChild.getTextContent());
                                    }
                                    affiliation.setAddress(address);
                                }
                            }
                        }
                        grobidAuthor.getAffiliations().add(affiliation);
                    }
                }
                result.add(grobidAuthor);
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception while parsing file " + teiFile.getName() + " occurred", e);
            throw new GrobidExtractionException(e.getMessage());
        }
    }
}
