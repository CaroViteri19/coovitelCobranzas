package coovitelCobranza.cobranzas.audit.domain.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EntityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long EntityId;
    private String EntityName;

    public EntityType(Long entityId, String entityName) {
        EntityId = entityId;
        EntityName = entityName;
    }

    public EntityType() {}

    public Long getEntityId() {
        return EntityId;
    }

    public void setEntityId(Long entityId) {
        EntityId = entityId;
    }

    public String getEntityName() {
        return EntityName;
    }

    public void setEntityName(String entityName) {
        EntityName = entityName;
    }
}
