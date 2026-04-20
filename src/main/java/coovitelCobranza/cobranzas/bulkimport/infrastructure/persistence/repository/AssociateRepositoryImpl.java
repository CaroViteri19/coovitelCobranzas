package coovitelCobranza.cobranzas.bulkimport.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.bulkimport.domain.model.AssociateImport;
import coovitelCobranza.cobranzas.bulkimport.domain.repository.AssociateImportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Adaptador (adapter) del puerto {@link AssociateImportRepository}.
 *
 * <p><b>Estrategia de inserción:</b> Usa {@link JdbcTemplate#batchUpdate} en lugar de
 * {@code JpaRepository.saveAll()} para evitar el problema de Hibernate con
 * {@code GenerationType.IDENTITY} que desactiva el batch de JDBC.
 *
 * <p>Con {@code rewriteBatchedStatements=true} en el JDBC URL, MySQL convierte
 * los N batches en un único multi-row INSERT, maximizando el throughput.
 *
 * <p>Rendimiento esperado en 10,000 registros con chunks de 500:
 * <ul>
 *   <li>Sin rewrite: ~20 INSERTs de 500 parámetros → ~2-5 segundos</li>
 *   <li>Con rewrite: ~20 multi-row INSERTs → &lt;500ms</li>
 * </ul>
 */
@Repository
public class AssociateRepositoryImpl implements AssociateImportRepository {

    private static final Logger log = LoggerFactory.getLogger(AssociateRepositoryImpl.class);

    private static final String INSERT_SQL = """
            INSERT INTO associate_imports (
              document_type, document_number, full_name, obligation_number,
              total_balance, delinquency_days, due_date, phone_1,
              email, phone_2, city, preferred_channel,
              segment, product, agent_code, created_at
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

    private final JdbcTemplate        jdbcTemplate;
    private final AssociateJpaRepository jpaRepository;

    public AssociateRepositoryImpl(JdbcTemplate jdbcTemplate,
                                   AssociateJpaRepository jpaRepository) {
        this.jdbcTemplate   = jdbcTemplate;
        this.jpaRepository  = jpaRepository;
    }

    // ── Puerto: batch insert ──────────────────────────────────────────────────

    @Override
    public void batchInsert(List<AssociateImport> asociados, int chunkSize) {
        List<List<AssociateImport>> chunks = partition(asociados, chunkSize);
        LocalDateTime now = LocalDateTime.now();

        log.debug("[BATCH-INSERT] {} registros → {} chunks de {}",
                asociados.size(), chunks.size(), chunkSize);

        for (int i = 0; i < chunks.size(); i++) {
            List<AssociateImport> chunk = chunks.get(i);
            int chunkIdx = i;

            jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    AssociateImport a = chunk.get(j);
                    ps.setString(1,  a.getTipoId());
                    ps.setString(2,  a.getNumDocumento());
                    ps.setString(3,  a.getNombreCompleto());
                    ps.setString(4,  a.getNumObligacion());
                    ps.setBigDecimal(5, a.getSaldoTotal());
                    ps.setInt(6,     a.getDiasMora());
                    ps.setDate(7,    Date.valueOf(a.getFechaVenc()));
                    ps.setString(8,  a.getTelefono1());
                    setNullable(ps, 9,  a.getEmail(),          Types.VARCHAR);
                    setNullable(ps, 10, a.getTelefono2(),       Types.VARCHAR);
                    setNullable(ps, 11, a.getCiudad(),          Types.VARCHAR);
                    setNullable(ps, 12, a.getCanalPreferido(),  Types.VARCHAR);
                    setNullable(ps, 13, a.getSegmento(),        Types.VARCHAR);
                    setNullable(ps, 14, a.getProducto(),        Types.VARCHAR);
                    setNullable(ps, 15, a.getCodigoAgente(),    Types.VARCHAR);
                    ps.setTimestamp(16, Timestamp.valueOf(now));
                }

                @Override
                public int getBatchSize() { return chunk.size(); }
            });

            log.debug("[BATCH-INSERT] Chunk {}/{} insertado ({} registros)",
                    chunkIdx + 1, chunks.size(), chunk.size());
        }

        log.info("[BATCH-INSERT] Completado: {} registros en {} chunks", asociados.size(), chunks.size());
    }

    // ── Puerto: queries de unicidad ───────────────────────────────────────────

    @Override
    public Set<String> findExistingDocumentos(Set<String> documentos) {
        if (documentos.isEmpty()) return Set.of();
        return jpaRepository.findDocumentosExistentes(documentos);
    }

    @Override
    public Set<String> findExistingEmails(Set<String> emails) {
        if (emails.isEmpty()) return Set.of();
        return jpaRepository.findEmailsExistentes(emails);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    /**
     * Establece NULL o valor VARCHAR en un PreparedStatement.
     */
    private void setNullable(PreparedStatement ps, int idx,
                              String value, int sqlType) throws SQLException {
        if (value == null) {
            ps.setNull(idx, sqlType);
        } else {
            ps.setString(idx, value);
        }
    }

    /**
     * Divide una lista en sublistas de tamaño {@code size}.
     * Implementación sin dependencia de Guava.
     */
    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
