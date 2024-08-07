package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "id")
public class Id {

    @XmlValue
    @XmlSchemaType(name = "anyURI")
    protected String value;


    public Id() {
    }

    public Id(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Id{" +
                "value='" + value + '\'' +
                '}';
    }
}
