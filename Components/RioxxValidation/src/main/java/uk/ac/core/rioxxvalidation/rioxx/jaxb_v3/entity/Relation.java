package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "value"
})
@XmlRootElement(name = "relation")
public class Relation {

    @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;
    @XmlAttribute(name = "rel")
    protected String rel;
    @XmlAttribute(name = "type")
    @XmlSchemaType(name = "anyURI")
    protected String type;
    @XmlAttribute(name = "coar_type")
    @XmlSchemaType(name = "anyURI")
    protected String coarType;
    @XmlAttribute(name = "coar_version")
    @XmlSchemaType(name = "anyURI")
    protected String coarVersion;
    @XmlAttribute(name = "deposit_date")
    protected String depositDate;
    @XmlAttribute(name = "resource_exposed_date")
    protected String resourceExposedDate;
    @XmlAttribute(name = "license_ref")
    @XmlSchemaType(name = "anyURI")
    protected String licenseRef;
    @XmlAttribute(name = "access_rights")
    @XmlSchemaType(name = "anyURI")
    protected String accessType;

    public Relation() {
    }

    public Relation(String value, String rel, String type, String coarType, String coarVersion, String depositDate, String resourceExposedDate, String licenseRef, String accessType) {
        this.value = value;
        this.rel = rel;
        this.type = type;
        this.coarType = coarType;
        this.coarVersion = coarVersion;
        this.depositDate = depositDate;
        this.resourceExposedDate = resourceExposedDate;
        this.licenseRef = licenseRef;
        this.accessType = accessType;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(String depositDate) {
        this.depositDate = depositDate;
    }

    public String getResourceExposedDate() {
        return resourceExposedDate;
    }

    public void setResourceExposedDate(String resourceExposedDate) {
        this.resourceExposedDate = resourceExposedDate;
    }

    public String getLicenseRef() {
        return licenseRef;
    }

    public void setLicenseRef(String licenseRef) {
        this.licenseRef = licenseRef;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "value='" + value + '\'' +
                ", rel='" + rel + '\'' +
                ", type='" + type + '\'' +
                ", coarType='" + coarType + '\'' +
                ", coarVersion='" + coarVersion + '\'' +
                ", depositDate='" + depositDate + '\'' +
                ", resourceExposedDate='" + resourceExposedDate + '\'' +
                ", licenseRef='" + licenseRef + '\'' +
                ", accessType='" + accessType + '\'' +
                '}';
    }
}
