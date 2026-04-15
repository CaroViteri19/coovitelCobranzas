package coovitelCobranza.security.persistence.repository;

import coovitelCobranza.security.persistence.entity.TypeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocumentEntity, Long> {

    Optional<TypeDocumentEntity> findByAbbreviationIgnoreCase(String abbreviation);

    TypeDocumentEntity findByDescription(String description);
}
