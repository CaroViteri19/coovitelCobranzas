-- ==========================================
-- Catálogos de Gestión de Casos y Autorización
-- ==========================================
-- Este script extiende el schema con tablas para estados de casos,
-- transiciones permitidas, permisos y mapeos de rol ↔ permiso.
-- Fecha: 2026-04-07
-- Referencia: Definiciones de conceptos de reunión

-- ==========================================
-- Table: case_statuses (Catálogo de Estados de Casos)
-- ==========================================
DROP TABLE IF EXISTS `case_statuses`;
CREATE TABLE `case_statuses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(30) NOT NULL UNIQUE,
  `name` VARCHAR(80) NOT NULL,
  `description` VARCHAR(255),
  `is_initial` BOOLEAN NOT NULL DEFAULT FALSE,
  `is_final` BOOLEAN NOT NULL DEFAULT FALSE,
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_case_statuses_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: case_status_transitions (Matriz de Transiciones)
-- ==========================================
DROP TABLE IF EXISTS `case_status_transitions`;
CREATE TABLE `case_status_transitions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `from_status_id` BIGINT NOT NULL,
  `to_status_id` BIGINT NOT NULL,
  `trigger_event` VARCHAR(50),
  `description` VARCHAR(255),
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transitions` (`from_status_id`, `to_status_id`),
  KEY `idx_from_status` (`from_status_id`),
  KEY `idx_to_status` (`to_status_id`),
  CONSTRAINT `fk_transition_from_status` FOREIGN KEY (`from_status_id`)
    REFERENCES `case_statuses` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_transition_to_status` FOREIGN KEY (`to_status_id`)
    REFERENCES `case_statuses` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: app_permissions (Catálogo de Permisos)
-- ==========================================
DROP TABLE IF EXISTS `app_permissions`;
CREATE TABLE `app_permissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(80) NOT NULL UNIQUE,
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(255),
  `module` VARCHAR(30),
  `resource` VARCHAR(50),
  `action` VARCHAR(30),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_permissions_code` (`code`),
  INDEX `idx_module_resource_action` (`module`, `resource`, `action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: role_permissions (Mapeo Rol ↔ Permiso)
-- ==========================================
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `access_level` VARCHAR(20) NOT NULL DEFAULT 'READ',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_role_permissions_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permissions_role` FOREIGN KEY (`role_id`)
    REFERENCES `app_roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permissions_permission` FOREIGN KEY (`permission_id`)
    REFERENCES `app_permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Table: escalation_rules (Reglas de Desborde Automático)
-- ==========================================
DROP TABLE IF EXISTS `escalation_rules`;
CREATE TABLE `escalation_rules` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255),
  `rule_type` VARCHAR(30) NOT NULL,
  `condition_field` VARCHAR(50),
  `condition_operator` VARCHAR(10),
  `condition_value` VARCHAR(100),
  `target_status` VARCHAR(30),
  `assign_to_agent` BOOLEAN NOT NULL DEFAULT TRUE,
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
  `priority` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_escalation_rules_name` (`name`),
  INDEX `idx_rule_type_enabled` (`rule_type`, `enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- SEED DATA: Case Statuses (según definición de reunión)
-- ==========================================
INSERT INTO `case_statuses` (`code`, `name`, `description`, `is_initial`, `is_final`, `enabled`)
VALUES
  ('NUEVO', 'Nuevo', 'El caso entra al sistema y espera la primera acción', TRUE, FALSE, TRUE),
  ('EN_GESTION', 'En Gestión', 'El asesor o la IA están interactuando con el asociado', FALSE, FALSE, TRUE),
  ('ILOCALIZADO', 'Ilocalizado / Sin Respuesta', 'Se han agotado los canales sin éxito', FALSE, FALSE, TRUE),
  ('PROMESA_PAGO', 'Promesa de Pago', 'Se formaliza un compromiso de pago', FALSE, FALSE, TRUE),
  ('PREJURIDICO', 'Prejurídico', 'Etapa de advertencia legal. Última instancia antes de demanda', FALSE, FALSE, TRUE),
  ('COBRO_JUDICIAL', 'Cobro Judicial', 'Caso escalado a proceso judicial', FALSE, FALSE, TRUE),
  ('CERRADO', 'Cerrado', 'El asociado normalizó su deuda o el caso fue castigado', FALSE, TRUE, TRUE);

-- ==========================================
-- SEED DATA: Case Status Transitions (según matriz de reunión)
-- ==========================================
-- Suponiendo que los IDs de estados son 1-7 en orden de INSERT anterior:
-- Nuevo (1) → En Gestión (2), Ilocalizado (3)
-- En Gestión (2) → Promesa de Pago (4), Ilocalizado (3), Prejurídico (5)
-- Ilocalizado (3) → En Gestión (2), Prejurídico (5)
-- Promesa de Pago (4) → Cerrado (7), En Gestión (2)
-- Prejurídico (5) → Cerrado (7), Cobro Judicial (6)
-- Cobro Judicial (6) → Cerrado (7)
-- Cerrado (7) → (sin transiciones)

INSERT INTO `case_status_transitions` (`from_status_id`, `to_status_id`, `trigger_event`, `description`, `enabled`)
VALUES
  (1, 2, 'FIRST_CONTACT', 'Primera acción en el caso', TRUE),
  (1, 3, 'NO_RESPONSE', 'No se logró contacto inicial', TRUE),
  (2, 4, 'PROMISE_ACCEPTED', 'Cliente acepta pagar', TRUE),
  (2, 3, 'UNREACHABLE', 'No se logró contacto después de intentos', TRUE),
  (2, 5, 'ESCALATE_LEGAL', 'Caso escalado a instancia prejurídica', TRUE),
  (3, 2, 'RETRY', 'Reintentamos contacto', TRUE),
  (3, 5, 'ESCALATE_LEGAL', 'Sin respuesta, se escala a prejurídico', TRUE),
  (4, 7, 'PAYMENT_RECEIVED', 'Promesa de pago cumplida', TRUE),
  (4, 2, 'PROMISE_BROKEN', 'Promesa incumplida, vuelve a gestión', TRUE),
  (5, 7, 'CASE_CLOSED', 'Finalizado sin ir a judicial', TRUE),
  (5, 6, 'JUDICIAL_ACTION', 'Se inicia demanda', TRUE),
  (6, 7, 'CASE_CLOSED', 'Cobro judicial finalizado', TRUE);

-- ==========================================
-- SEED DATA: App Permissions (según matriz de reunión)
-- ==========================================
INSERT INTO `app_permissions` (`code`, `name`, `description`, `module`, `resource`, `action`)
VALUES
  -- Dashboard
  ('DASHBOARD:VIEW', 'Ver Dashboard', 'Acceso al dashboard de KPIs', 'dashboard', 'dashboard', 'read'),

  -- Integración (M1)
  ('INTEGRATION:UPLOAD', 'Cargar Archivos Batch', 'Carga de archivos batch y sincronización', 'integration', 'batch', 'write'),
  ('INTEGRATION:SYNC', 'Sincronizar CORE', 'Sincronización con sistema CORE', 'integration', 'sync', 'write'),
  ('INTEGRATION:VIEW', 'Ver Integraciones', 'Lectura de estados de integración', 'integration', 'integration', 'read'),

  -- Analítica (M2)
  ('ANALYTICS:VIEW_SCORE', 'Ver Scoring', 'Visualización de scoring de riesgo', 'analytics', 'scoring', 'read'),
  ('ANALYTICS:CONFIGURE', 'Configurar Modelos', 'Configuración de modelos analíticos', 'analytics', 'model', 'write'),

  -- Políticas (M3)
  ('POLICIES:VIEW', 'Ver Políticas', 'Visualización de políticas de cobranza', 'policies', 'policy', 'read'),
  ('POLICIES:CREATE', 'Crear Políticas', 'Creación de nuevas políticas', 'policies', 'policy', 'write'),
  ('POLICIES:EDIT', 'Editar Políticas', 'Edición de políticas existentes', 'policies', 'policy', 'write'),
  ('POLICIES:DELETE', 'Eliminar Políticas', 'Eliminación de políticas', 'policies', 'policy', 'delete'),

  -- Orquestación (M4)
  ('ORCHESTRATION:SEND_SMS', 'Enviar SMS', 'Envío de mensajes SMS', 'orchestration', 'sms', 'write'),
  ('ORCHESTRATION:SEND_WHATSAPP', 'Enviar WhatsApp', 'Envío por WhatsApp Business', 'orchestration', 'whatsapp', 'write'),
  ('ORCHESTRATION:SEND_VOICE', 'Llamadas Automáticas', 'Bot de voz y llamadas automáticas', 'orchestration', 'voice', 'write'),
  ('ORCHESTRATION:SEND_EMAIL', 'Enviar Email', 'Envío de correos electrónicos', 'orchestration', 'email', 'write'),
  ('ORCHESTRATION:VIEW', 'Ver Orquestación', 'Visualización de ejecuciones', 'orchestration', 'execution', 'read'),

  -- Gestión de Casos (M5)
  ('CASE_MANAGEMENT:VIEW', 'Ver Casos', 'Visualización de casos de gestión', 'case_management', 'case', 'read'),
  ('CASE_MANAGEMENT:ASSIGN', 'Asignar Casos', 'Asignación de casos a asesores', 'case_management', 'case', 'write'),
  ('CASE_MANAGEMENT:MANAGE', 'Gestionar Casos', 'Operación operativa de cobranza', 'case_management', 'case', 'write'),

  -- Recaudo (M6)
  ('COLLECTION:VIEW', 'Ver Recaudos', 'Visualización de pagos y recaudos', 'collection', 'payment', 'read'),
  ('COLLECTION:REGISTER', 'Registrar Pagos', 'Registro de pagos y conciliación', 'collection', 'payment', 'write'),

  -- Reportería (M7)
  ('REPORTING:VIEW', 'Ver Reportes', 'Visualización de reportes', 'reporting', 'report', 'read'),
  ('REPORTING:EXPORT', 'Exportar Datos', 'Exportación de datos y reportes', 'reporting', 'report', 'write'),
  ('REPORTING:GENERATE', 'Generar Reportes', 'Generación de nuevos reportes', 'reporting', 'report', 'write'),

  -- Configuración
  ('CONFIG:MANAGE_USERS', 'Gestionar Usuarios', 'Creación y edición de usuarios', 'config', 'user', 'write'),
  ('CONFIG:MANAGE_ROLES', 'Gestionar Roles', 'Administración de roles y permisos', 'config', 'role', 'write'),
  ('CONFIG:MANAGE_SECURITY', 'Gestionar Seguridad', 'Configuración de seguridad y auditoría', 'config', 'security', 'write');

-- ==========================================
-- SEED DATA: Role Permissions (según matriz de reunión)
-- ==========================================
-- Asumiendo que app_roles contiene: ADMIN (1), SUPERVISOR (2), AGENTE (3), AUDITOR (4)
-- Esto será completado después de que los roles sean creados en bootstrap

-- Para Admin: Todo acceso
-- Para Supervisor: Todo excepto CONFIG
-- Para Agente: Solo operaciones, sin configuración
-- Para Auditor: Solo lectura general

