CREATE SCHEMA `kwordz_file`;

CREATE TABLE `at_file` (
  `identifier` varchar(36) NOT NULL,
  `url` varchar(2048) DEFAULT NULL,
  `source_url` varchar(2048) DEFAULT NULL,
  `name` varchar(1024) NOT NULL,
  `initial_name` varchar(1024) NOT NULL,
  `extension` varchar(10) DEFAULT NULL,
  `mime` varchar(32) DEFAULT NULL,
  `size` bigint NOT NULL,
  `sha1` varchar(40) DEFAULT NULL,
  `audit_identifier` varchar(50) NOT NULL,
  `audit_functionality` varchar(255) NOT NULL,
  `audit_action` varchar(6) NOT NULL,
  `audit_actor` varchar(320) NOT NULL,
  `audit_date` date NOT NULL,
  PRIMARY KEY (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `at_file_bytes` (
  `identifier` varchar(36) NOT NULL,
  `bytes` longblob NOT NULL,
  `audit_identifier` varchar(50) NOT NULL,
  `audit_functionality` varchar(255) NOT NULL,
  `audit_action` varchar(6) NOT NULL,
  `audit_actor` varchar(300) NOT NULL,
  `audit_date` date NOT NULL,
  PRIMARY KEY (`identifier`),
  CONSTRAINT `fk_bytes_identifier` FOREIGN KEY (`identifier`) REFERENCES `at_file` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `at_file_text` (
  `identifier` varchar(36) NOT NULL,
  `text` varchar(4096) NOT NULL,
  `audit_identifier` varchar(50) NOT NULL,
  `audit_functionality` varchar(255) NOT NULL,
  `audit_action` varchar(6) NOT NULL,
  `audit_actor` varchar(300) NOT NULL,
  `audit_date` date NOT NULL,
  PRIMARY KEY (`identifier`),
  CONSTRAINT `fk_text_identifier` FOREIGN KEY (`identifier`) REFERENCES `at_file` (`identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;