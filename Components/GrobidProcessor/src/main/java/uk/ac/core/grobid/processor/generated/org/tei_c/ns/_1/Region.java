//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.09 at 04:50:09 PM GMT 
//


package uk.ac.core.grobid.processor.generated.org.tei_c.ns._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.tei-c.org/ns/1.0}tei_macro.phraseSeq">
 *       &lt;attGroup ref="{http://www.tei-c.org/ns/1.0}tei_att.naming.attributes"/>
 *       &lt;attGroup ref="{http://www.tei-c.org/ns/1.0}tei_att.typed.attributes"/>
 *       &lt;attGroup ref="{http://www.tei-c.org/ns/1.0}tei_att.datable.attributes"/>
 *       &lt;attGroup ref="{http://www.tei-c.org/ns/1.0}tei_att.global.attributes"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "region")
public class Region
    extends TeiMacroPhraseSeq
{

    @XmlAttribute(name = "key")
    protected String key;
    @XmlAttribute(name = "ref")
    protected List<String> reves;
    @XmlAttribute(name = "role")
    protected List<String> roles;
    @XmlAttribute(name = "nymRef")
    protected List<String> nymReves;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "subtype")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String subtype;
    @XmlAttribute(name = "when")
    protected String when;
    @XmlAttribute(name = "from")
    protected String from;
    @XmlAttribute(name = "notBefore")
    protected String notBefore;
    @XmlAttribute(name = "notAfter")
    protected String notAfter;
    @XmlAttribute(name = "to")
    protected String to;
    @XmlAttribute(name = "notBefore-custom")
    protected List<String> notBeforeCustoms;
    @XmlAttribute(name = "notAfter-custom")
    protected List<String> notAfterCustoms;
    @XmlAttribute(name = "from-custom")
    protected List<String> fromCustoms;
    @XmlAttribute(name = "to-custom")
    protected List<String> toCustoms;
    @XmlAttribute(name = "when-custom")
    protected List<String> whenCustoms;
    @XmlAttribute(name = "datingPoint")
    protected String datingPoint;
    @XmlAttribute(name = "datingMethod")
    protected String datingMethod;
    @XmlAttribute(name = "period")
    protected String period;
    @XmlAttribute(name = "calendar")
    protected String calendar;
    @XmlAttribute(name = "when-iso")
    protected String whenIso;
    @XmlAttribute(name = "notAfter-iso")
    protected String notAfterIso;
    @XmlAttribute(name = "to-iso")
    protected String toIso;
    @XmlAttribute(name = "from-iso")
    protected String fromIso;
    @XmlAttribute(name = "notBefore-iso")
    protected String notBeforeIso;
    @XmlAttribute(name = "sameAs")
    protected String sameAs;
    @XmlAttribute(name = "prev")
    protected String prev;
    @XmlAttribute(name = "corresp")
    protected List<String> corresps;
    @XmlAttribute(name = "synch")
    protected List<String> synches;
    @XmlAttribute(name = "next")
    protected String next;
    @XmlAttribute(name = "select")
    protected List<String> selects;
    @XmlAttribute(name = "exclude")
    protected List<String> excludes;
    @XmlAttribute(name = "copyOf")
    protected String copyOf;
    @XmlAttribute(name = "space", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String space;
    @XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "n")
    protected String n;
    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String base;
    @XmlAttribute(name = "rend")
    protected List<String> rends;
    @XmlAttribute(name = "rendition")
    protected List<String> renditions;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;
    @XmlAttribute(name = "style")
    protected String style;

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the reves property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reves property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReves().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getReves() {
        if (reves == null) {
            reves = new ArrayList<String>();
        }
        return this.reves;
    }

    /**
     * Gets the value of the roles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        return this.roles;
    }

    /**
     * Gets the value of the nymReves property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nymReves property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNymReves().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNymReves() {
        if (nymReves == null) {
            nymReves = new ArrayList<String>();
        }
        return this.nymReves;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the subtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * Sets the value of the subtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtype(String value) {
        this.subtype = value;
    }

    /**
     * Gets the value of the when property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhen() {
        return when;
    }

    /**
     * Sets the value of the when property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhen(String value) {
        this.when = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Gets the value of the notBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotBefore() {
        return notBefore;
    }

    /**
     * Sets the value of the notBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotBefore(String value) {
        this.notBefore = value;
    }

    /**
     * Gets the value of the notAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotAfter() {
        return notAfter;
    }

    /**
     * Sets the value of the notAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotAfter(String value) {
        this.notAfter = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Gets the value of the notBeforeCustoms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notBeforeCustoms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotBeforeCustoms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNotBeforeCustoms() {
        if (notBeforeCustoms == null) {
            notBeforeCustoms = new ArrayList<String>();
        }
        return this.notBeforeCustoms;
    }

    /**
     * Gets the value of the notAfterCustoms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notAfterCustoms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotAfterCustoms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNotAfterCustoms() {
        if (notAfterCustoms == null) {
            notAfterCustoms = new ArrayList<String>();
        }
        return this.notAfterCustoms;
    }

    /**
     * Gets the value of the fromCustoms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fromCustoms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFromCustoms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFromCustoms() {
        if (fromCustoms == null) {
            fromCustoms = new ArrayList<String>();
        }
        return this.fromCustoms;
    }

    /**
     * Gets the value of the toCustoms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the toCustoms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getToCustoms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getToCustoms() {
        if (toCustoms == null) {
            toCustoms = new ArrayList<String>();
        }
        return this.toCustoms;
    }

    /**
     * Gets the value of the whenCustoms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the whenCustoms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWhenCustoms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getWhenCustoms() {
        if (whenCustoms == null) {
            whenCustoms = new ArrayList<String>();
        }
        return this.whenCustoms;
    }

    /**
     * Gets the value of the datingPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatingPoint() {
        return datingPoint;
    }

    /**
     * Sets the value of the datingPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatingPoint(String value) {
        this.datingPoint = value;
    }

    /**
     * Gets the value of the datingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatingMethod() {
        return datingMethod;
    }

    /**
     * Sets the value of the datingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatingMethod(String value) {
        this.datingMethod = value;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriod(String value) {
        this.period = value;
    }

    /**
     * Gets the value of the calendar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalendar() {
        return calendar;
    }

    /**
     * Sets the value of the calendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalendar(String value) {
        this.calendar = value;
    }

    /**
     * Gets the value of the whenIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhenIso() {
        return whenIso;
    }

    /**
     * Sets the value of the whenIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhenIso(String value) {
        this.whenIso = value;
    }

    /**
     * Gets the value of the notAfterIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotAfterIso() {
        return notAfterIso;
    }

    /**
     * Sets the value of the notAfterIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotAfterIso(String value) {
        this.notAfterIso = value;
    }

    /**
     * Gets the value of the toIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToIso() {
        return toIso;
    }

    /**
     * Sets the value of the toIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToIso(String value) {
        this.toIso = value;
    }

    /**
     * Gets the value of the fromIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromIso() {
        return fromIso;
    }

    /**
     * Sets the value of the fromIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromIso(String value) {
        this.fromIso = value;
    }

    /**
     * Gets the value of the notBeforeIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotBeforeIso() {
        return notBeforeIso;
    }

    /**
     * Sets the value of the notBeforeIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotBeforeIso(String value) {
        this.notBeforeIso = value;
    }

    /**
     * Gets the value of the sameAs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSameAs() {
        return sameAs;
    }

    /**
     * Sets the value of the sameAs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSameAs(String value) {
        this.sameAs = value;
    }

    /**
     * Gets the value of the prev property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrev() {
        return prev;
    }

    /**
     * Sets the value of the prev property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrev(String value) {
        this.prev = value;
    }

    /**
     * Gets the value of the corresps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the corresps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorresps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCorresps() {
        if (corresps == null) {
            corresps = new ArrayList<String>();
        }
        return this.corresps;
    }

    /**
     * Gets the value of the synches property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the synches property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSynches().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSynches() {
        if (synches == null) {
            synches = new ArrayList<String>();
        }
        return this.synches;
    }

    /**
     * Gets the value of the next property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNext() {
        return next;
    }

    /**
     * Sets the value of the next property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNext(String value) {
        this.next = value;
    }

    /**
     * Gets the value of the selects property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the selects property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSelects().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSelects() {
        if (selects == null) {
            selects = new ArrayList<String>();
        }
        return this.selects;
    }

    /**
     * Gets the value of the excludes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the excludes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExcludes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getExcludes() {
        if (excludes == null) {
            excludes = new ArrayList<String>();
        }
        return this.excludes;
    }

    /**
     * Gets the value of the copyOf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyOf() {
        return copyOf;
    }

    /**
     * Sets the value of the copyOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyOf(String value) {
        this.copyOf = value;
    }

    /**
     * Gets the value of the space property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpace() {
        return space;
    }

    /**
     * Sets the value of the space property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpace(String value) {
        this.space = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the n property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getN() {
        return n;
    }

    /**
     * Sets the value of the n property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setN(String value) {
        this.n = value;
    }

    /**
     * Gets the value of the base property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBase() {
        return base;
    }

    /**
     * Sets the value of the base property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBase(String value) {
        this.base = value;
    }

    /**
     * Gets the value of the rends property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rends property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRends().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRends() {
        if (rends == null) {
            rends = new ArrayList<String>();
        }
        return this.rends;
    }

    /**
     * Gets the value of the renditions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the renditions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRenditions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRenditions() {
        if (renditions == null) {
            renditions = new ArrayList<String>();
        }
        return this.renditions;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

}
