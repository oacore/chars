package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;


import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "contributor")
public class Contributor {

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    protected Name name;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/" )
    protected List<Id> id;

    public Contributor() {
    }

    public Contributor(Name name, List<Id> id) {
        this.name = name;
        this.id = id;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public List<Id> getId() {
        return id;
    }

    public void setId(List<Id> id) {
        this.id = id;
    }
}
