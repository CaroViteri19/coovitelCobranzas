-- =============================================================================
-- CARGA MASIVA DE ASOCIADOS — Tabla principal
-- Sistema: BankVision Coovitel
-- Módulo: M1. Integración / Carga Masiva
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: asociados_carga
-- Contiene los registros de deudores/asociados cargados vía archivo CSV.
-- Diseñada para alto volumen (~10K registros/lote, potencialmente millones en total).
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS asociados_carga (

  -- PK auto-incremental
  id_asociado       BIGINT         NOT NULL AUTO_INCREMENT,

  -- ── Campos obligatorios ──────────────────────────────────────────────────
  tipo_id           VARCHAR(2)     NOT NULL COMMENT 'CC=Cédula | NIT=NIT | CE=Cédula Extranjería | PA=Pasaporte',
  num_documento     VARCHAR(20)    NOT NULL COMMENT 'Número de documento. ÚNICO por tipo_id',
  nombre_completo   VARCHAR(120)   NOT NULL,
  num_obligacion    VARCHAR(30)    NOT NULL COMMENT 'Número de obligación/crédito',
  saldo_total       DECIMAL(18,2)  NOT NULL COMMENT 'Saldo total en mora (COP)',
  dias_mora         INT            NOT NULL COMMENT 'Días en mora a fecha de corte',
  fecha_venc        DATE           NOT NULL COMMENT 'Fecha de vencimiento de la obligación',
  telefono_1        VARCHAR(15)    NOT NULL,

  -- ── Campos opcionales ────────────────────────────────────────────────────
  email             VARCHAR(80)    NULL     COMMENT 'Email de contacto. ÚNICO cuando no es NULL',
  telefono_2        VARCHAR(15)    NULL,
  ciudad            VARCHAR(60)    NULL,
  canal_preferido   VARCHAR(20)    NULL     COMMENT 'WhatsApp | SMS | Email | Voz',
  segmento          VARCHAR(30)    NULL     COMMENT 'Segmento de cobranza según política',
  producto          VARCHAR(50)    NULL     COMMENT 'Tipo de producto financiero',
  codigo_agente     VARCHAR(10)    NULL     COMMENT 'Agente asignado por regla de distribución',

  -- ── Metadatos de carga ───────────────────────────────────────────────────
  created_at        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME       NULL     ON UPDATE CURRENT_TIMESTAMP,

  -- ── PK ───────────────────────────────────────────────────────────────────
  PRIMARY KEY (id_asociado)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Registros de asociados/deudores cargados via archivo CSV batch';

-- =============================================================================
-- ÍNDICES
-- =============================================================================

-- Unicidad de documento (clave de negocio principal)
ALTER TABLE asociados_carga
  ADD CONSTRAINT uk_asociado_documento UNIQUE (num_documento);

-- Unicidad de email — MySQL permite múltiples NULLs en índice UNIQUE
-- (cumple RFC: NULL != NULL en SQL estándar)
ALTER TABLE asociados_carga
  ADD CONSTRAINT uk_asociado_email UNIQUE (email);

-- Índices de consulta para reportes y filtros frecuentes
CREATE INDEX idx_asociado_dias_mora     ON asociados_carga (dias_mora);
CREATE INDEX idx_asociado_saldo         ON asociados_carga (saldo_total);
CREATE INDEX idx_asociado_fecha_venc    ON asociados_carga (fecha_venc);
CREATE INDEX idx_asociado_segmento      ON asociados_carga (segmento);
CREATE INDEX idx_asociado_codigo_agente ON asociados_carga (codigo_agente);
CREATE INDEX idx_asociado_tipo_id       ON asociados_carga (tipo_id);

-- Índice compuesto para búsqueda por nombre (full text alternativo básico)
CREATE INDEX idx_asociado_nombre        ON asociados_carga (nombre_completo(50));

-- =============================================================================
-- VERIFICACIÓN POST-CREACIÓN
-- =============================================================================
-- Verificar la tabla con: SHOW CREATE TABLE asociados_carga;
-- Verificar índices con:  SHOW INDEX FROM asociados_carga;
-- =============================================================================
