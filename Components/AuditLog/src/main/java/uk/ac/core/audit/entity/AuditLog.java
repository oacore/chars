package uk.ac.core.audit.entity;

import javax.persistence.*;

/**
 * BASE repository entity.
 */
@Entity
@Table(name = "audit_log")
public final class AuditLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    @Column(name = "identifier_type")
    private String identifierType;

    private String user;

    private String product;

    private String action;

    public AuditLog() {
    }

    public AuditLog(String identifier, String identifierType, String user, Product product, String action) {
        this();
        this.identifier = identifier;
        this.identifierType = identifierType;
        this.user = user;
        this.product = product.value();
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProduct() {
        return product;
    }

    public Product getProductAsObject() {
        return Product.fromValue(product);
    }

    public void setProduct(Product product) {
        this.product = product.value();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

