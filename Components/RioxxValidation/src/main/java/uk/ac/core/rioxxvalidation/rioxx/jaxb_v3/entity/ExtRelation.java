package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ext_relation")
public class ExtRelation {

    @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;
    @XmlAttribute(name = "rel")
    protected String rel;

    @XmlAttribute(name = "coar_type")
    @XmlSchemaType(name = "anyURI")
    protected String coarType;

    @XmlAttribute(name = "coar_version")
    @XmlSchemaType(name = "anyURI")
    protected String coarVersion;

    public ExtRelation() {
    }

    public ExtRelation(String value, String rel, String coarType, String coarVersion) {
        this.value = value;
        this.rel = rel;
        this.coarType = coarType;
        this.coarVersion = coarVersion;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getCoarType() {
        return coarType;
    }

    public void setCoarType(String coarType) {
        this.coarType = coarType;
    }

    public String getCoarVersion() {
        return coarVersion;
    }

    public void setCoarVersion(String coarVersion) {
        this.coarVersion = coarVersion;
    }
}
