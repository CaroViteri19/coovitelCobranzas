-- ==========================================
-- DDD Modules Tables (Auto-generation compatible)
-- ==========================================
-- Este script complementa el schema.sql con las tablas de los módulos DDD
-- Ejecutar después del schema.sql principal

-- ==========================================
-- Table: clientes (Cliente Module)
-- ==========================================
DROP TABLE IF EXISTS `clientes`;
CREATE TABLE `clientes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `tipo_documento` VARCHAR(10) NOT NULL,
  `numero_documento` VARCHAR(20) NOT NULL UNIQUE,
  `nombre_completo` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(20),
  `email` VARCHAR(100),
  `acepta_whats_app` BOOLEAN NOT NULL DEFAULT FALSE,
  `acepta_sms` BOOLEAN NOT NULL DEFAULT FALSE,
  `acepta_email` BOOLEAN NOT NULL DEFAULT FALSE,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_documento` (`tipo_documento`, `numero_documento`),
  INDEX `idx_numero_doc` (`numero_documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: payments (Payment Module)
-- ==========================================
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `obligation_id` BIGINT NOT NULL,
  `amount` DECIMAL(14, 2) NOT NULL,
  `external_reference` VARCHAR(50) NOT NULL UNIQUE,
  `method` VARCHAR(20) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `confirmed_at` DATETIME,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reference` (`external_reference`),
  INDEX `idx_obligation` (`obligation_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: interacciones (Interaccion Module)
-- ==========================================
DROP TABLE IF EXISTS `interacciones`;
CREATE TABLE `interacciones` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `caso_gestion_id` BIGINT NOT NULL,
  `canal` VARCHAR(20) NOT NULL,
  `plantilla` VARCHAR(500),
  `resultado` VARCHAR(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_caso` (`caso_gestion_id`),
  INDEX `idx_resultado` (`resultado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: casos_gestion (CasoGestion Module)
-- ==========================================
DROP TABLE IF EXISTS `casos_gestion`;
CREATE TABLE `casos_gestion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `obligacion_id` BIGINT NOT NULL,
  `prioridad` VARCHAR(20) NOT NULL,
  `estado` VARCHAR(20) NOT NULL,
  `asesor_asignado` VARCHAR(100),
  `proxima_accion_at` DATETIME,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_obligacion` (`obligacion_id`),
  INDEX `idx_estado` (`estado`),
  INDEX `idx_proxima_accion` (`proxima_accion_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: estrategias (Politicas Module)
-- ==========================================
DROP TABLE IF EXISTS `estrategias`;
CREATE TABLE `estrategias` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(500),
  `activa` BOOLEAN NOT NULL DEFAULT TRUE,
  `max_intentos_contacto` INT NOT NULL,
  `dias_antes_dee_escalacion` INT NOT NULL,
  `rol_asignacion_escalada` VARCHAR(100) NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_estrategia_nombre` (`nombre`),
  INDEX `idx_estrategia_activa` (`activa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: politicas (Politicas Module)
-- ==========================================
DROP TABLE IF EXISTS `politicas`;
CREATE TABLE `politicas` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `estrategia_id` BIGINT NOT NULL,
  `tipo_cobro` VARCHAR(30) NOT NULL,
  `dias_mora_minimo` INT NOT NULL,
  `dias_mora_maximo` INT NOT NULL,
  `accion` VARCHAR(200) NOT NULL,
  `activa` BOOLEAN NOT NULL DEFAULT TRUE,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_politica_estrategia` (`estrategia_id`),
  INDEX `idx_politica_activa` (`activa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: scoring_segmentacion (Scoring Module)
-- ==========================================
DROP TABLE IF EXISTS `scoring_segmentacion`;
CREATE TABLE `scoring_segmentacion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `cliente_id` BIGINT NOT NULL,
  `obligacion_id` BIGINT NOT NULL,
  `score` DECIMAL(5,2) NOT NULL,
  `segmento` VARCHAR(20) NOT NULL,
  `version_modelo` VARCHAR(30) NOT NULL,
  `razon_principal` VARCHAR(200) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_scoring_cliente` (`cliente_id`),
  INDEX `idx_scoring_obligacion` (`obligacion_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: orchestration_executions (Orchestration Module)
-- ==========================================
DROP TABLE IF EXISTS `orchestration_executions`;
CREATE TABLE `orchestration_executions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `case_id` BIGINT NOT NULL,
  `channel` VARCHAR(20) NOT NULL,
  `destination` VARCHAR(120) NOT NULL,
  `template` VARCHAR(500) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_orchestration_case` (`case_id`),
  INDEX `idx_orchestration_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: audit_events (Audit Module)
-- ==========================================
DROP TABLE IF EXISTS `audit_events`;
CREATE TABLE `audit_events` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `entity_type` VARCHAR(80) NOT NULL,
  `entity_id` BIGINT NOT NULL,
  `action` VARCHAR(80) NOT NULL,
  `username` VARCHAR(80) NOT NULL,
  `user_role` VARCHAR(80),
  `source` VARCHAR(30) DEFAULT 'SYSTEM',
  `module_name` VARCHAR(50) DEFAULT 'GENERAL',
  `correlation_id` VARCHAR(100),
  `details` VARCHAR(1000),
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_audit_entity` (`entity_type`, `entity_id`),
  INDEX `idx_audit_created` (`created_at`),
  INDEX `idx_audit_module_action` (`module_name`, `action`),
  INDEX `idx_audit_correlation_id` (`correlation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: case_assignment_trace (Case assignment trace)
-- ==========================================
DROP TABLE IF EXISTS `case_assignment_trace`;
CREATE TABLE `case_assignment_trace` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `case_id` BIGINT NOT NULL,
  `advisor` VARCHAR(100) NOT NULL,
  `assignment_source` VARCHAR(30) NOT NULL,
  `performed_by` VARCHAR(80),
  `performed_by_role` VARCHAR(80),
  `correlation_id` VARCHAR(100),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_assignment_trace_case` (`case_id`),
  CONSTRAINT `fk_assignment_trace_case`
    FOREIGN KEY (`case_id`) REFERENCES `casos_gestion`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Indices para performance en queries comunes
-- ==========================================

-- Índices para listar casos pendientes
CREATE INDEX idx_casos_pendientes ON casos_gestion(estado) WHERE estado IN ('ABIERTO', 'EN_GESTION');

-- Índices para obtener pagos por obligación
CREATE INDEX idx_pagos_obligacion ON pagos(obligacion_id, estado);

-- Índices para obtener interacciones activas
CREATE INDEX idx_interacciones_activas ON interacciones(caso_gestion_id, resultado);

-- Índices para scoring por cliente
CREATE INDEX idx_scoring_cliente_segmento ON scoring_segmentacion(cliente_id, segmento);

-- Índices para políticas por tipo y rango
CREATE INDEX idx_politicas_tipo_rango ON politicas(tipo_cobro, dias_mora_minimo, dias_mora_maximo);

-- Indices for frequent audit searches
CREATE INDEX idx_audit_username_action ON audit_events(username, action);

-- Prevent deletion of case statuses that are already referenced by managed cases.
DROP TRIGGER IF EXISTS `trg_case_statuses_before_delete`;
DELIMITER $$
CREATE TRIGGER `trg_case_statuses_before_delete`
BEFORE DELETE ON `case_statuses`
FOR EACH ROW
BEGIN
  IF EXISTS (
    SELECT 1
    FROM `casos_gestion`
    WHERE `estado` = OLD.`code`
    LIMIT 1
  ) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Cannot delete case status with existing case history';
  END IF;
END$$
DELIMITER ;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;



