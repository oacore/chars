/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.ComplianceCheckerV2;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.ComplianceCheckerV3;

/**
 *
 * @author mc26486
 */
public class RioxxRecordSAXHandler extends DefaultHandler {

    private final ComplianceCheckerV2 complianceCheckerv2;
    private final ComplianceCheckerV3 complianceCheckerV3;

    private StringBuffer record;
    private StringBuffer oai;
    private StringBuffer recordAttributes;
    private boolean inRecord = false;
    private boolean inIdentifier = false;
    List<String> records = new ArrayList<>();

    public RioxxRecordSAXHandler(ComplianceCheckerV2 complianceCheckerv2, ComplianceCheckerV3 complianceCheckerV3) {
        this.complianceCheckerv2 = complianceCheckerv2;
        this.complianceCheckerV3 = complianceCheckerV3;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (inRecord) {
            record.append("<");
            record.append(qName);

            if (attributes.getLength() > 0) {
                record.append(" ");
                for (int i = 0; i < attributes.getLength(); i++) {
                    record.append(attributes.getQName(i));
                    record.append("=");
                    record.append("\"");
                    record.append(attributes.getValue(i));
                    record.append("\" ");
                }
            }
            record.append(">");
        }

        if ((qName.equalsIgnoreCase("rioxx"))) {
            inRecord = true;
            record = new StringBuffer();
            recordAttributes = new StringBuffer();
            if (attributes.getLength() > 0) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    recordAttributes.append(attributes.getQName(i));
                    recordAttributes.append("=");
                    recordAttributes.append("\"");
                    recordAttributes.append(attributes.getValue(i));
                    recordAttributes.append("\" ");
                }
            }
        }
        if ((qName.equalsIgnoreCase("identifier"))) {
            inIdentifier = true;
            oai = new StringBuffer();

        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inRecord) {
            record.append(StringEscapeUtils.escapeXml(new String(ch, start, length)));
        }
        if (inIdentifier){
            oai.append(StringEscapeUtils.escapeXml(new String(ch, start, length)));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ((qName.equalsIgnoreCase("rioxx"))) {
            inRecord = false;
            String attributes = recordAttributes.toString();
            if(attributes.contains("schema/v2.0/rioxx")){
                complianceCheckerv2.check(RIOXX_START_TAG_V2 + record.toString() + RIOXX_END_TAG, oai.toString());
            } else if(attributes.contains("schema/v3.0/rioxx")){
                complianceCheckerV3.check(RIOXX_START_TAG_V3 + record.toString() + RIOXX_END_TAG, oai.toString());
            }

            record.delete(0, record.length());
            oai.delete(0, oai.length());
        }
        if (inRecord) {
            record.append("</").append(qName).append(">");
        }
         if ((qName.equalsIgnoreCase("identifier"))) {
            inIdentifier = false;
        }
    }

    public static final String RIOXX_START_TAG_V3 = "<rioxx xmlns:ali='http://ali.niso.org/2014/ali/1.0'  xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:rioxxterms=\"http://www.rioxx.net/schema/v3.0/rioxxterms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";
    public static final String RIOXX_END_TAG = "</rioxx>";
    public static final String RIOXX_START_TAG_V2 = "<rioxx xmlns:ali='http://ali.niso.org/2014/ali/1.0'  xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:rioxxterms=\"http://www.rioxx.net/schema/v2.0/rioxxterms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";

    public List<String> getRecords() {
        return records;
    }
}
