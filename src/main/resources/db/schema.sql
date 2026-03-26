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

