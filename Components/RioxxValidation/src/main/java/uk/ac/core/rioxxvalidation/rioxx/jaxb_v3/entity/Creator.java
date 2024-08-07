package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "creator")
public class Creator {

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    protected Name name;

    @XmlElement(namespace = "http://www.rioxx.net/schema/v3.0/rioxxterms/")
    protected List<Id> id;

    @XmlAttribute(name = "first-named-author")
    protected boolean firstNamedAuthor;

    public Creator() {
    }

    public Creator(Name name, List<Id> id, boolean firstNamedAuthor) {
        this.name = name;
        this.id = id;
        this.firstNamedAuthor = firstNamedAuthor;
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

    public boolean isFirstNamedAuthor() {
        return firstNamedAuthor;
    }

    public void setFirstNamedAuthor(boolean firstNamedAuthor) {
        this.firstNamedAuthor = firstNamedAuthor;
    }

    @Override
    public String toString() {
        return "Creator{" +
                "name=" + name +
                ", id=" + id +
                ", firstNamedAuthor=" + firstNamedAuthor +
                '}';
    }
}
