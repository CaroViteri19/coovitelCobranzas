package coovitelCobranza.security.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_document")
public class TypeDocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String description;

    @Column(length = 4)
    private String abbreviation;

    public TypeDocumentEntity(Long id, String description, String abbreviation) {
        this.id = id;
        this.description = description;
        this.abbreviation = abbreviation;
    }
    public TypeDocumentEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
