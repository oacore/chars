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
 * <p>Java class for versionList.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="versionList">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AO"/>
 *     &lt;enumeration value="SMUR"/>
 *     &lt;enumeration value="AM"/>
 *     &lt;enumeration value="P"/>
 *     &lt;enumeration value="VoR"/>
 *     &lt;enumeration value="CVoR"/>
 *     &lt;enumeration value="EVoR"/>
 *     &lt;enumeration value="NA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "versionList")
@XmlEnum
public enum VersionList {

    AO("AO"),
    SMUR("SMUR"),
    AM("AM"),
    P("P"),
    @XmlEnumValue("VoR")
    VO_R("VoR"),
    @XmlEnumValue("CVoR")
    C_VO_R("CVoR"),
    @XmlEnumValue("EVoR")
    E_VO_R("EVoR"),
    NA("NA");
    private final String value;

    VersionList(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VersionList fromValue(String v) {
        for (VersionList c: VersionList.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
