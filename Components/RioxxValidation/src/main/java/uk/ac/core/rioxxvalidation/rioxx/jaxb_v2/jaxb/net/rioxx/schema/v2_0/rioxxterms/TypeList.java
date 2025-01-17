//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.03 at 04:39:08 PM BST 
//


package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.jaxb.net.rioxx.schema.v2_0.rioxxterms;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeList.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeList">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Book"/>
 *     &lt;enumeration value="Book chapter"/>
 *     &lt;enumeration value="Book edited"/>
 *     &lt;enumeration value="Conference Paper/Proceeding/Abstract"/>
 *     &lt;enumeration value="Journal Article/Review"/>
 *     &lt;enumeration value="Manual/Guide"/>
 *     &lt;enumeration value="Monograph"/>
 *     &lt;enumeration value="Policy briefing report"/>
 *     &lt;enumeration value="Technical Report"/>
 *     &lt;enumeration value="Technical Standard"/>
 *     &lt;enumeration value="Thesis"/>
 *     &lt;enumeration value="Consultancy Report"/>
 *     &lt;enumeration value="Working paper"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeList")
@XmlEnum
public enum TypeList {

    @XmlEnumValue("Book")
    BOOK("Book"),
    @XmlEnumValue("Book chapter")
    BOOK_CHAPTER("Book chapter"),
    @XmlEnumValue("Book edited")
    BOOK_EDITED("Book edited"),
    @XmlEnumValue("Conference Paper/Proceeding/Abstract")
    CONFERENCE_PAPER_PROCEEDING_ABSTRACT("Conference Paper/Proceeding/Abstract"),
    @XmlEnumValue("Journal Article/Review")
    JOURNAL_ARTICLE_REVIEW("Journal Article/Review"),
    @XmlEnumValue("Manual/Guide")
    MANUAL_GUIDE("Manual/Guide"),
    @XmlEnumValue("Monograph")
    MONOGRAPH("Monograph"),
    @XmlEnumValue("Policy briefing report")
    POLICY_BRIEFING_REPORT("Policy briefing report"),
    @XmlEnumValue("Technical Report")
    TECHNICAL_REPORT("Technical Report"),
    @XmlEnumValue("Technical Standard")
    TECHNICAL_STANDARD("Technical Standard"),
    @XmlEnumValue("Thesis")
    THESIS("Thesis"),
    @XmlEnumValue("Consultancy Report")
    CONSULTANCY_REPORT("Consultancy Report"),
    @XmlEnumValue("Working paper")
    WORKING_PAPER("Working paper");
    private final String value;

    TypeList(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeList fromValue(String v) {
        for (TypeList c: TypeList.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
