-- Consolidated Database Schema for Cobranzas System
-- Created: 2026-03-26
-- All tables in a single script with proper dependency order

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ==========================================
-- Catalog Tables (Base/Independent tables)
-- ==========================================

-- Table: catalog_channel
DROP TABLE IF EXISTS `catalog_channel`;
CREATE TABLE `catalog_channel` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: catalog_document_type
DROP TABLE IF EXISTS `catalog_document_type`;
CREATE TABLE `catalog_document_type` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: catalog_payment_method
DROP TABLE IF EXISTS `catalog_payment_method`;
CREATE TABLE `catalog_payment_method` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: catalog_payment_status
DROP TABLE IF EXISTS `catalog_payment_status`;
CREATE TABLE `catalog_payment_status` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: catalog_priority
DROP TABLE IF EXISTS `catalog_priority`;
CREATE TABLE `catalog_priority` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: catalog_result
DROP TABLE IF EXISTS `catalog_result`;
CREATE TABLE `catalog_result` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: catalog_status
DROP TABLE IF EXISTS `catalog_status`;
CREATE TABLE `catalog_status` (
  `id` int NOT NULL,
  `abbreviation` varchar(10) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ==========================================
-- Business Domain Tables
-- ==========================================

-- Table: customer (depends on catalog tables)
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `document_type` int DEFAULT NULL,
  `document_number` varchar(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `payment_status` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `document_number` (`document_number`),
  KEY `document_type` (`document_type`),
  KEY `payment_status` (`payment_status`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`document_type`) REFERENCES `catalog_document_type` (`id`),
  CONSTRAINT `customer_ibfk_2` FOREIGN KEY (`payment_status`) REFERENCES `catalog_payment_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: obligation (depends on customer and catalog tables)
DROP TABLE IF EXISTS `obligation`;
CREATE TABLE `obligation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint DEFAULT NULL,
  `obligation_number` varchar(50) DEFAULT NULL,
  `product` varchar(50) DEFAULT NULL,
  `total_balance` decimal(15,2) DEFAULT NULL,
  `overdue_balance` decimal(15,2) DEFAULT NULL,
  `overdue_days` int DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `status` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `obligation_number` (`obligation_number`),
  KEY `customer_id` (`customer_id`),
  KEY `status` (`status`),
  CONSTRAINT `obligation_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `obligation_ibfk_2` FOREIGN KEY (`status`) REFERENCES `catalog_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: case_file (depends on obligation and catalog tables)
DROP TABLE IF EXISTS `case_file`;
CREATE TABLE `case_file` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `obligation_id` bigint DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `advisor` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `closed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `obligation_id` (`obligation_id`),
  KEY `priority` (`priority`),
  KEY `status` (`status`),
  CONSTRAINT `case_file_ibfk_1` FOREIGN KEY (`obligation_id`) REFERENCES `obligation` (`id`),
  CONSTRAINT `case_file_ibfk_2` FOREIGN KEY (`priority`) REFERENCES `catalog_priority` (`id`),
  CONSTRAINT `case_file_ibfk_3` FOREIGN KEY (`status`) REFERENCES `catalog_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: payment (depends on obligation and catalog tables)
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `obligation_id` bigint DEFAULT NULL,
  `amount` decimal(15,2) DEFAULT NULL,
  `payment_date` timestamp NULL DEFAULT NULL,
  `method` int DEFAULT NULL,
  `reference` varchar(100) DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `obligation_id` (`obligation_id`),
  KEY `method` (`method`),
  KEY `status` (`status`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`obligation_id`) REFERENCES `obligation` (`id`),
  CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`method`) REFERENCES `catalog_payment_method` (`id`),
  CONSTRAINT `payment_ibfk_3` FOREIGN KEY (`status`) REFERENCES `catalog_payment_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: interaction (depends on case_file and catalog tables)
DROP TABLE IF EXISTS `interaction`;
CREATE TABLE `interaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_id` bigint DEFAULT NULL,
  `channel` int DEFAULT NULL,
  `message` text,
  `result` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `case_id` (`case_id`),
  KEY `channel` (`channel`),
  KEY `result` (`result`),
  CONSTRAINT `interaction_ibfk_1` FOREIGN KEY (`case_id`) REFERENCES `case_file` (`id`),
  CONSTRAINT `interaction_ibfk_2` FOREIGN KEY (`channel`) REFERENCES `catalog_channel` (`id`),
  CONSTRAINT `interaction_ibfk_3` FOREIGN KEY (`result`) REFERENCES `catalog_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: tracking (depends on catalog tables)
DROP TABLE IF EXISTS `tracking`;
CREATE TABLE `tracking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `entity` varchar(50) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `user` varchar(100) DEFAULT NULL,
  `channel` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `details` text,
  PRIMARY KEY (`id`),
  KEY `channel` (`channel`),
  CONSTRAINT `tracking_ibfk_1` FOREIGN KEY (`channel`) REFERENCES `catalog_channel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: roles (security module)
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(150) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: users (security module)
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id_user` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(120) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  `full_name` varchar(150) NOT NULL,
  `locked` tinyint(1) NOT NULL DEFAULT 0,
  `username` varchar(80) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `uk_users_username` (`username`),
  UNIQUE KEY `uk_users_email` (`email`),
  KEY `idx_users_active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: users_roles (security module)
DROP TABLE IF EXISTS `users_roles`;
CREATE TABLE `users_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_users_roles_role_id` (`role_id`),
  CONSTRAINT `fk_users_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE,
  CONSTRAINT `fk_users_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ==========================================
-- Case Status Catalogs and Permissions
-- ==========================================

-- Table: case_statuses (Parametrizable case statuses)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: case_status_transitions (State machine matrix)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: permissions (Granular permissions catalog)
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(80) NOT NULL UNIQUE,
  `name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(255),
  `module` VARCHAR(30),
  `resource` VARCHAR(50),
  `action` VARCHAR(30),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permissions_code` (`code`),
  INDEX `idx_module_resource_action` (`module`, `resource`, `action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: role_permissions (Role-Permission mapping)
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `access_level` VARCHAR(20) NOT NULL DEFAULT 'READ',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_role_permissions_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permissions_role` FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permissions_permission` FOREIGN KEY (`permission_id`)
    REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: escalation_rules (Automatic escalation rules for case overflow)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ==========================================
-- Reset MySQL settings
-- ==========================================

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Schema completed on 2026-03-26



