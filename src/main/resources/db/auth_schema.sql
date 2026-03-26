-- ============================================
-- USER MANAGEMENT TABLES
-- ============================================
-- Agregadas para sistema de autenticación OAuth2/JWT

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

-- Table: roles
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id_role` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL UNIQUE,
  `description` varchar(255),
  PRIMARY KEY (`id_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar roles por defecto
INSERT INTO `roles` (`name`, `description`) VALUES
('ROLE_USER', 'Usuario estándar del sistema'),
('ROLE_ADVISOR', 'Asesor de cobranzas'),
('ROLE_MANAGER', 'Gerente de cobranzas'),
('ROLE_ADMIN', 'Administrador del sistema');

-- Table: users
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id_user` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email` (`email`),
  KEY `active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: user_roles (Relationship)
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id_user` bigint NOT NULL,
  `id_role` int NOT NULL,
  PRIMARY KEY (`id_user`, `id_role`),
  KEY `id_role` (`id_role`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`id_role`) REFERENCES `roles` (`id_role`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- AUDIT/TRACKING TABLES
-- ============================================

-- Table: audit_logs
DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint,
  `entity_type` varchar(50) NOT NULL,
  `entity_id` bigint NOT NULL,
  `action` varchar(50) NOT NULL,
  `old_values` json,
  `new_values` json,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `entity_type_id` (`entity_type`, `entity_id`),
  KEY `timestamp` (`timestamp`),
  CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- PERMISSIONS/OAUTH2 SCOPES
-- ============================================

-- Table: permissions (Opcional - para RBAC granular)
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL UNIQUE,
  `name` varchar(100) NOT NULL,
  `description` varchar(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar permisos por defecto
INSERT INTO `permissions` (`code`, `name`, `description`) VALUES
('READ_CUSTOMER', 'Leer clientes', 'Puede ver la información de clientes'),
('WRITE_CUSTOMER', 'Crear/Editar clientes', 'Puede crear y editar clientes'),
('DELETE_CUSTOMER', 'Eliminar clientes', 'Puede eliminar clientes'),
('READ_CASE', 'Leer casos', 'Puede ver los casos de cobranza'),
('WRITE_CASE', 'Crear/Editar casos', 'Puede crear y editar casos'),
('MANAGE_USERS', 'Gestionar usuarios', 'Puede crear, editar y eliminar usuarios'),
('MANAGE_ROLES', 'Gestionar roles', 'Puede crear y editar roles');

-- Table: role_permissions (Relationship)
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id_role` int NOT NULL,
  `permission_id` int NOT NULL,
  PRIMARY KEY (`id_role`, `permission_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`id_role`) REFERENCES `roles` (`id_role`) ON DELETE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

