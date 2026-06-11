mysqldump : mysqldump: [Warning] Using a password on the command line interface
 can be insecure.
所在位置 行:1 字符: 5
+ & { mysqldump -h localhost -u root -p123520 --default-character-set=u ...
+     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    + CategoryInfo          : NotSpecified: (mysqldump: [War...an be insecure. 
   :String) [], RemoteException
    + FullyQualifiedErrorId : NativeCommandError
 
-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: localhost    Database: railway
-- ------------------------------------------------------
-- Server version	8.0.37

--
-- Current Database: `railway`
--

CREATE DATABASE IF NOT EXISTS `railway` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `railway`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `category_id` bigint NOT NULL COMMENT '??ID',
  `organizer_id` bigint NOT NULL COMMENT '???ID',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '????',
  `start_time` datetime NOT NULL COMMENT '????',
  `end_time` datetime NOT NULL COMMENT '????',
  `cancel_deadline` datetime DEFAULT NULL COMMENT '????????',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????',
  `max_participants` int DEFAULT NULL COMMENT '??????',
  `current_participants` int DEFAULT '0' COMMENT '??????',
  `credit_hours` decimal(5,2) DEFAULT '0.00' COMMENT '??',
  `credit_points` decimal(5,2) DEFAULT '0.00' COMMENT '??',
  `is_contact_activity` tinyint(1) DEFAULT '0' COMMENT '???????',
  `issue_certificate` tinyint(1) DEFAULT '0' COMMENT '??????',
  `certificate_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT' COMMENT '???DRAFT-?? PUBLISHED-??? STARTED-??? ENDED-??? CANCELLED-???',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `is_certificate` tinyint(1) DEFAULT '0' COMMENT '鏄惁棰佸彂璇佷功',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_organizer` (`organizer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='???';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (9,'娴嬭瘯1',4,8,'1','2026-02-12 16:00:00','2026-02-28 16:00:00',NULL,'1',200,0,2.00,2.00,0,0,NULL,'PUBLISHED',1,'2026-02-15 03:00:18','2026-02-15 03:11:57',0),(10,'1',4,8,'1','2026-02-11 16:00:00','2026-02-27 16:00:00',NULL,'1',200,0,2.00,2.00,0,0,NULL,'PUBLISHED',1,'2026-02-15 03:42:59','2026-02-15 04:03:55',0),(11,'娴嬭瘯1',4,8,'1','2026-02-13 16:00:00','2026-02-27 16:00:00',NULL,'1',200,0,2.00,2.00,0,0,NULL,'PUBLISHED',1,'2026-02-15 04:12:21','2026-02-15 04:15:11',0),(12,'1娴嬭瘯',4,8,'1','2026-01-31 16:00:00','2026-02-27 16:00:00',NULL,'1',200,0,2.00,2.00,0,0,NULL,'PUBLISHED',1,'2026-02-15 04:15:05','2026-02-15 04:27:36',0),(13,'test1',4,8,'1','2026-02-13 16:00:00','2026-02-27 16:00:00',NULL,'1',200,2,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-02-15 04:28:17','2026-02-15 04:28:17',0),(14,'绉戞妧1',2,8,'1','2026-02-13 16:00:00','2026-02-27 16:00:00',NULL,'1',200,0,3.00,3.00,0,0,NULL,'PUBLISHED',1,'2026-02-16 23:36:59','2026-02-17 00:15:58',0),(15,'绉戞妧2',2,8,'1','2026-02-09 16:00:00','2026-02-27 16:00:00',NULL,'1',200,0,4.00,4.00,0,0,NULL,'PUBLISHED',1,'2026-02-17 00:16:26','2026-02-17 02:24:58',0),(16,'绉戞妧3',2,8,'1','2026-02-09 16:00:00','2026-02-27 16:00:00',NULL,'1',200,1,3.00,3.00,0,0,NULL,'PUBLISHED',0,'2026-02-17 02:25:23','2026-02-17 02:25:23',0),(17,'鏁板绔炶禌1',2,8,'绔炶禌','2026-02-24 16:00:00','2026-03-30 16:00:00',NULL,'1',1000,1,4.00,4.00,0,0,'????????','PUBLISHED',0,'2026-02-26 22:32:13','2026-02-26 22:32:13',1),(18,'鏁板绔炶禌1',1,1,'鏁板绔炶禌娲诲姩','2026-02-25 23:00:05','2026-02-26 21:00:05',NULL,'鏁欏妤糀101',100,1,4.00,4.00,0,0,'鏁板绔炶禌浼樿儨璇佷功','ENDED',1,'2026-02-26 23:00:06','2026-02-26 23:45:56',1),(19,'鏁板绔炶禌1',1,1,'鏁板绔炶禌娲诲姩','2026-02-25 23:03:59','2026-02-26 21:03:59',NULL,'鏁欏妤糀101',100,1,4.00,4.00,0,0,'鏁板绔炶禌浼樿儨璇佷功','ENDED',1,'2026-02-26 23:03:59','2026-02-26 23:45:58',1),(20,'鏁板绔炶禌2',5,8,'1','2026-02-25 16:00:00','2026-03-30 16:00:00',NULL,'1',200,1,2.00,5.00,0,0,NULL,'PUBLISHED',0,'2026-02-27 22:59:43','2026-02-27 22:59:43',0),(21,'骞垮窞绉戞妧鑱屼笟鎶€鏈ぇ瀛︽瘯涓氱敓鏍″洯鎷涜仒浼?,4,8,'姣曚笟鐢熸牎鍥嫑鑱?,'2026-03-31 16:00:00','2026-05-30 16:00:00',NULL,'骞垮窞绉戞妧鑱屼笟鎶€鏈ぇ瀛﹀浘涔﹂鏋剁┖灞?,200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-18 22:27:47','2026-04-18 22:27:47',0),(22,'瀛︽椂瀛﹀垎娲诲姩',1,8,'a','2026-03-31 16:00:00','2026-04-29 16:00:00',NULL,'1',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-21 20:03:47','2026-04-21 20:03:47',0),(23,'娴嬭瘯娲诲姩1',1,8,'娴嬭瘯娲诲姩','2026-03-31 16:00:00','2026-04-30 16:00:00',NULL,'1',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-21 20:26:34','2026-04-21 20:26:34',0),(24,'2',5,8,'闃挎柉椤裤€乗n','2026-03-31 16:00:00','2026-04-28 16:00:00',NULL,'1',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-21 20:50:16','2026-04-21 20:50:16',0),(25,'瀛︽椂瀛﹀垎1',1,8,'鍟?,'2026-03-31 16:00:00','2026-04-29 16:00:00',NULL,'1',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-24 22:24:14','2026-04-24 22:24:14',0),(26,'adasd',1,8,'asda','2026-03-31 16:00:00','2026-04-29 16:00:00',NULL,'dasd',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-25 01:03:40','2026-04-25 01:03:40',0),(27,'ad',1,8,'asd','2026-03-31 16:00:00','2026-04-29 16:00:00',NULL,'asdas',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-25 01:09:13','2026-04-25 01:09:13',0),(28,'asdasd',1,8,'adssa','2026-03-31 16:00:00','2026-04-29 16:00:00',NULL,'ads',200,1,2.00,2.00,0,0,NULL,'PUBLISHED',0,'2026-04-25 15:41:50','2026-04-25 15:41:50',0);
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_category`
--

DROP TABLE IF EXISTS `activity_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_category`
--

LOCK TABLES `activity_category` WRITE;
/*!40000 ALTER TABLE `activity_category` DISABLE KEYS */;
INSERT INTO `activity_category` VALUES (1,'蹇楁効鏈嶅姟','鍖呮嫭绀惧尯鏈嶅姟銆佸叕鐩婃椿鍔ㄧ瓑',0,'2026-02-15 02:02:04','2026-02-15 02:02:04'),(2,'瀛︽湳绉戞妧','鍖呮嫭瀛︽湳璁插骇銆佺鎶€绔炶禌绛?,0,'2026-02-15 02:02:04','2026-02-15 02:02:04'),(3,'鏂囧寲浣撹偛','鍖呮嫭鏂囪壓娲诲姩銆佷綋鑲茬珵璧涚瓑',0,'2026-02-15 02:02:04','2026-02-15 02:02:04'),(4,'绀句細瀹炶返','鍖呮嫭瀹炰範銆佽皟鐮旂瓑',0,'2026-02-15 02:02:04','2026-02-15 02:02:04'),(5,'鍏朵粬娲诲姩','鍏朵粬绫诲瀷鐨勫疄璺垫椿鍔?,0,'2026-02-15 02:02:04','2026-02-15 02:02:04');
/*!40000 ALTER TABLE `activity_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_evaluation`
--

DROP TABLE IF EXISTS `activity_evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `registration_id` bigint NOT NULL COMMENT '??ID',
  `activity_id` bigint NOT NULL COMMENT '??ID',
  `student_id` bigint NOT NULL COMMENT '??ID',
  `score` decimal(3,1) NOT NULL COMMENT '??',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '??',
  `evaluator_id` bigint NOT NULL COMMENT '???ID',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_registration` (`registration_id`),
  KEY `idx_activity` (`activity_id`),
  KEY `idx_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_evaluation`
--

LOCK TABLES `activity_evaluation` WRITE;
/*!40000 ALTER TABLE `activity_evaluation` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_evaluation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_registration`
--

DROP TABLE IF EXISTS `activity_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `activity_id` bigint NOT NULL COMMENT '??ID',
  `student_id` bigint NOT NULL COMMENT '??ID',
  `registration_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '???PENDING-??? APPROVED-??? REJECTED-??? CANCELLED-??? COMPLETED-???',
  `check_in_time` datetime DEFAULT NULL COMMENT '????',
  `check_in_location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????',
  `check_out_time` datetime DEFAULT NULL COMMENT '????',
  `actual_hours` decimal(5,2) DEFAULT NULL COMMENT '??????',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  `proof_file_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '璇佹槑鏂囦欢璺緞',
  `proof_file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '璇佹槑鏂囦欢鍚?,
  `proof_submit_time` datetime DEFAULT NULL COMMENT '璇佹槑鎻愪氦鏃堕棿',
  `proof_verified` tinyint(1) DEFAULT '0' COMMENT '璇佹槑鏄惁宸查獙璇?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_student` (`activity_id`,`student_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_registration`
--

LOCK TABLES `activity_registration` WRITE;
/*!40000 ALTER TABLE `activity_registration` DISABLE KEYS */;
INSERT INTO `activity_registration` VALUES (13,10,10,'2026-02-15 03:57:34','CANCELLED',NULL,NULL,NULL,NULL,0,'2026-02-15 03:57:34','2026-02-15 03:57:34',NULL,NULL,NULL,0),(14,10,7,'2026-02-15 03:59:27','CANCELLED',NULL,NULL,NULL,NULL,0,'2026-02-15 03:59:27','2026-02-15 03:59:27',NULL,NULL,NULL,0),(18,11,10,'2026-02-15 04:12:42','CANCELLED',NULL,NULL,NULL,NULL,0,'2026-02-15 04:12:42','2026-02-15 04:12:42',NULL,NULL,NULL,0),(19,11,7,'2026-02-15 04:12:53','CANCELLED',NULL,NULL,NULL,NULL,0,'2026-02-15 04:12:53','2026-02-15 04:12:53',NULL,NULL,NULL,0),(20,12,10,'2026-02-15 04:15:28','CANCELLED',NULL,NULL,NULL,NULL,0,'2026-02-15 04:15:28','2026-02-15 04:15:28',NULL,NULL,NULL,0),(21,13,10,'2026-02-15 04:28:26','COMPLETED','2026-02-16 20:13:10',NULL,'2026-02-16 20:13:42',2.00,0,'2026-02-15 04:28:26','2026-02-15 04:28:26','/uploads/proofs/3379a0cb-023a-47c7-a217-2aa9b90af63f.jpg','test.jpg','2026-02-16 20:13:17',1),(22,13,7,'2026-02-15 04:28:41','COMPLETED','2026-02-15 04:38:47',NULL,'2026-02-16 18:12:27',2.00,0,'2026-02-15 04:28:41','2026-02-15 04:28:41','/uploads/proofs/4f1e8009-4924-413c-83ed-4bcbb3b53b12.jpg','test.jpg','2026-02-15 04:45:37',1),(23,14,7,'2026-02-16 23:37:10','CANCELLED',NULL,NULL,NULL,NULL,1,'2026-02-16 23:37:10','2026-02-16 23:59:26',NULL,NULL,NULL,0),(32,15,7,'2026-02-17 00:16:37','CANCELLED','2026-02-17 00:23:05',NULL,NULL,NULL,0,'2026-02-17 00:16:37','2026-02-17 00:16:37','/uploads/proofs/ca074ae9-736c-42c2-a3f3-d5ac56c8d718.jpg','test.jpg','2026-02-17 00:23:10',0),(33,16,7,'2026-02-17 02:25:35','COMPLETED','2026-02-17 02:25:49',NULL,'2026-02-17 02:59:24',3.00,0,'2026-02-17 02:25:35','2026-02-17 02:25:35','/uploads/proofs/d42e1de6-2498-4b23-b131-b0da9e30c7db.jpg','test.jpg','2026-02-17 02:25:54',1),(34,17,7,'2026-02-26 22:32:27','COMPLETED','2026-02-26 22:33:05',NULL,'2026-02-26 22:33:32',4.00,0,'2026-02-26 22:32:27','2026-02-26 22:32:27','/uploads/proofs/2336bbdc-0036-4774-9d31-45b91288fd39.jpg','test.jpg','2026-02-26 22:33:00',1),(35,20,7,'2026-02-27 22:59:57','COMPLETED','2026-02-27 23:00:19',NULL,'2026-02-27 23:01:43',2.00,0,'2026-02-27 22:59:57','2026-02-27 22:59:57','/uploads/proofs/4ddeda73-9a85-4ace-9591-d426f52e75b2.jpg','test.jpg','2026-02-27 23:00:26',1),(36,21,7,'2026-04-18 22:34:36','COMPLETED','2026-04-18 22:36:01',NULL,'2026-04-18 22:38:37',2.00,0,'2026-04-18 22:34:36','2026-04-18 22:34:36','/uploads/proofs/795ee604-d8f2-472a-8d22-ccae59289b6c.jpg','test.jpg','2026-04-18 22:37:25',1),(37,22,7,'2026-04-21 20:04:21','COMPLETED','2026-04-21 20:08:15',NULL,'2026-04-21 20:08:54',2.00,0,'2026-04-21 20:04:21','2026-04-21 20:04:21','/uploads/proofs/19356e1b-f231-416c-8414-c149c11225cf.jpg','test.jpg','2026-04-21 20:06:10',1),(38,23,7,'2026-04-21 20:28:10','APPROVED','2026-05-24 13:56:55',NULL,NULL,NULL,0,'2026-04-21 20:28:10','2026-04-21 20:28:10','/uploads/proofs/dfc9fc97-05bb-4f0b-b9a2-572e8f6b7293.jpg','test.jpg','2026-04-21 20:29:29',0),(39,24,7,'2026-04-21 20:50:31','COMPLETED','2026-04-21 20:51:04',NULL,'2026-04-21 20:52:14',2.00,0,'2026-04-21 20:50:31','2026-04-21 20:50:31','/uploads/proofs/b6746107-6c4e-4cf7-99a7-7bcd02b51c29.jpg','test.jpg','2026-04-21 20:51:15',1),(40,25,7,'2026-04-24 22:24:37','COMPLETED','2026-04-24 22:25:13',NULL,'2026-04-24 22:26:21',2.00,0,'2026-04-24 22:24:37','2026-04-24 22:24:37','/uploads/proofs/8832705e-cf85-4a8e-9144-c1b301d630ea.jpg','test.jpg','2026-04-24 22:25:27',1),(41,26,7,'2026-04-25 01:04:00','COMPLETED','2026-04-25 01:04:30',NULL,'2026-04-25 01:05:52',2.00,0,'2026-04-25 01:04:00','2026-04-25 01:04:00','/uploads/proofs/7704a8d3-0d16-4528-b43f-fc50fe82b324.jpg','test.jpg','2026-04-25 01:04:51',1),(42,27,7,'2026-04-25 01:09:34','COMPLETED','2026-04-25 01:10:05',NULL,'2026-04-25 01:11:24',2.00,0,'2026-04-25 01:09:34','2026-04-25 01:09:34','/uploads/proofs/99bd2680-48a0-4916-ae29-07c08d136e48.jpg','test.jpg','2026-04-25 01:10:21',1),(43,28,7,'2026-04-25 15:42:04','COMPLETED','2026-04-25 15:42:29',NULL,'2026-04-25 15:43:13',2.00,0,'2026-04-25 15:42:04','2026-04-25 15:42:04','/uploads/proofs/894d05c5-0c45-4700-9512-62959810331d.jpg','test.jpg','2026-04-25 15:42:43',1);
/*!40000 ALTER TABLE `activity_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_suggestion`
--

DROP TABLE IF EXISTS `activity_suggestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_suggestion` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `activity_id` bigint DEFAULT NULL COMMENT '??ID',
  `sender_id` bigint NOT NULL COMMENT '???ID',
  `receiver_id` bigint NOT NULL COMMENT '???ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `parent_id` bigint DEFAULT NULL COMMENT '???ID?????',
  `is_read` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_activity` (`activity_id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_suggestion`
--

LOCK TABLES `activity_suggestion` WRITE;
/*!40000 ALTER TABLE `activity_suggestion` DISABLE KEYS */;
INSERT INTO `activity_suggestion` VALUES (1,16,8,7,'asdad',NULL,1,0,'2026-02-17 02:44:28','2026-02-17 02:44:28'),(2,NULL,7,8,'asdas',1,1,0,'2026-02-17 02:45:02','2026-02-17 02:45:02'),(3,16,8,7,'123',NULL,1,0,'2026-02-17 02:49:24','2026-02-17 02:49:24'),(4,20,8,7,'111',NULL,1,0,'2026-02-27 23:01:00','2026-02-27 23:01:00'),(5,NULL,7,8,'ok',4,1,0,'2026-02-27 23:01:16','2026-02-27 23:01:16'),(6,NULL,8,7,'1',5,1,0,'2026-04-04 21:38:24','2026-04-04 21:38:24'),(7,21,8,7,'鏃?,NULL,1,0,'2026-04-18 22:38:29','2026-04-18 22:38:29');
/*!40000 ALTER TABLE `activity_suggestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `publisher_id` bigint NOT NULL COMMENT '???ID',
  `target_audience` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ALL' COMMENT '?????ALL-?? STUDENT-?? TEACHER-??',
  `priority` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'NORMAL' COMMENT '????LOW-NORMAL-HIGH',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED' COMMENT '???DRAFT-?? PUBLISHED-??? ARCHIVED-???',
  `publish_time` datetime DEFAULT NULL COMMENT '????',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='???';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (1,'????????????','??????????????????????????????',1,'ALL','HIGH','PUBLISHED','2026-02-14 23:52:29',1,'2026-02-14 23:52:29','2026-02-15 01:36:06'),(2,'??????','?????????????????????????????????',1,'ALL','NORMAL','PUBLISHED','2026-02-14 23:52:29',1,'2026-02-14 23:52:29','2026-02-15 01:36:05'),(3,'鍏充簬寮€灞?024骞村ぇ瀛︾敓鏆戞湡绀句細瀹炶返娲诲姩鐨勯€氱煡','涓烘繁鍏ヨ疮褰昏惤瀹炵珛寰锋爲浜烘牴鏈换鍔★紝鎻愬崌瀛︾敓瀹炶返鑳藉姏鍜岀患鍚堢礌璐紝瀛︽牎鍐冲畾寮€灞?024骞村ぇ瀛︾敓鏆戞湡绀句細瀹炶返娲诲姩銆傝鍚勫闄㈢Н鏋佺粍缁囧鐢熷弬涓庯紝娲诲姩鎶ュ悕鎴鏃堕棿涓?024骞?鏈?0鏃ャ€?,9,'ALL','HIGH','PUBLISHED','2026-02-28 01:25:17',0,'2026-02-28 01:25:17','2026-02-28 01:25:17'),(4,'鏁板寤烘ā绔炶禌鎶ュ悕閫氱煡','2024骞村叏鍥藉ぇ瀛︾敓鏁板寤烘ā绔炶禌鍗冲皢寮€濮嬶紝娆㈣繋瀵规暟瀛﹀缓妯℃劅鍏磋叮鐨勫悓瀛﹁笂璺冩姤鍚嶃€傜珵璧涙椂闂翠负2024骞?鏈?2鏃ヨ嚦15鏃ワ紝鎶ュ悕鎴鏃堕棿涓?024骞?鏈?1鏃ャ€傝鏈夋剰鍚戠殑鍚屽鍦ㄦ椿鍔ㄨ鎯呴〉闈㈡煡鐪嬭缁嗕俊鎭苟鎶ュ悕銆?,9,'ALL','MEDIUM','PUBLISHED','2026-02-28 01:25:17',0,'2026-02-28 01:25:17','2026-02-28 01:25:17'),(5,'鍒涙柊鍒涗笟澶ц禌娲诲姩棰勫憡','瀛︽牎灏嗕簬2024骞?0鏈堜妇鍔炲垱鏂板垱涓氬ぇ璧涳紝鏃ㄥ湪鍩瑰吇瀛︾敓鐨勫垱鏂版剰璇嗗拰鍒涗笟鑳藉姏銆傛瘮璧涘垎涓哄垱鎰忕粍鍜屽垱涓氱粍锛屾杩庡悇涓撲笟瀛︾敓缁勯槦鍙傚姞銆傝缁嗘椿鍔ㄤ俊鎭皢鍦ㄨ繎鏈熷彂甯冿紝璇峰叧娉ㄥ悗缁€氱煡銆?,9,'ALL','MEDIUM','PUBLISHED','2026-02-28 01:25:17',0,'2026-02-28 01:25:17','2026-02-28 01:25:17'),(6,'蹇楁効鏈嶅姟娲诲姩鎷涘嫙閫氱煡','涓哄煿鍏诲鐢熺殑绀句細璐ｄ换鎰燂紝瀛︽牎灏嗙粍缁囩郴鍒楀織鎰挎湇鍔℃椿鍔紝鍖呮嫭绀惧尯鏈嶅姟銆佺幆淇濆叕鐩娿€佹敮鏁欏姪瀛︾瓑銆傚弬涓庡織鎰挎湇鍔″彲鑾峰緱鐩稿簲瀛︽椂瀛﹀垎璁ゅ畾銆傝鏈夋剰鍚戠殑鍚屽鍦ㄦ椿鍔ㄨ鎯呴〉闈㈡煡鐪嬪叿浣撴椿鍔ㄥ苟鎶ュ悕銆?,9,'STUDENT','LOW','PUBLISHED','2026-02-28 01:25:17',0,'2026-02-28 01:25:17','2026-02-28 01:25:17'),(7,'瀛︽湳璁插骇娲诲姩瀹夋帓閫氱煡','瀛︽牎灏嗛個璇风煡鍚嶄笓瀹跺鑰呬妇鍔炵郴鍒楀鏈搴э紝娑电洊绉戞妧鍓嶆部銆佷汉鏂囩ぞ绉戙€佽亴涓氬彂灞曠瓑澶氫釜涓婚銆傚弬涓庤搴у彲鑾峰緱瀛︽椂璁ゅ畾锛屽叿浣撹搴у畨鎺掑皢鍦ㄦ椿鍔ㄨ鎯呴〉闈㈠彂甯冿紝璇峰悓瀛︿滑鍏虫敞骞剁Н鏋佸弬涓庛€?,9,'ALL','MEDIUM','PUBLISHED','2026-02-28 01:25:17',0,'2026-02-28 01:25:17','2026-02-28 01:25:17'),(8,'鍏充簬寮€灞?024骞村ぇ瀛︾敓鏆戞湡绀句細瀹炶返娲诲姩鐨勯€氱煡','涓烘繁鍏ヨ疮褰昏惤瀹炵珛寰锋爲浜烘牴鏈换鍔★紝鎻愬崌瀛︾敓瀹炶返鑳藉姏鍜岀患鍚堢礌璐紝瀛︽牎鍐冲畾寮€灞?024骞村ぇ瀛︾敓鏆戞湡绀句細瀹炶返娲诲姩銆傝鍚勫闄㈢Н鏋佺粍缁囧鐢熷弬涓庯紝娲诲姩鎶ュ悕鎴鏃堕棿涓?024骞?鏈?0鏃ャ€?,9,'ALL','HIGH','PUBLISHED','2026-02-28 01:25:24',0,'2026-02-28 01:25:24','2026-02-28 01:25:24'),(9,'鏁板寤烘ā绔炶禌鎶ュ悕閫氱煡','2024骞村叏鍥藉ぇ瀛︾敓鏁板寤烘ā绔炶禌鍗冲皢寮€濮嬶紝娆㈣繋瀵规暟瀛﹀缓妯℃劅鍏磋叮鐨勫悓瀛﹁笂璺冩姤鍚嶃€傜珵璧涙椂闂翠负2024骞?鏈?2鏃ヨ嚦15鏃ワ紝鎶ュ悕鎴鏃堕棿涓?024骞?鏈?1鏃ャ€傝鏈夋剰鍚戠殑鍚屽鍦ㄦ椿鍔ㄨ鎯呴〉闈㈡煡鐪嬭缁嗕俊鎭苟鎶ュ悕銆?,9,'ALL','MEDIUM','PUBLISHED','2026-02-28 01:25:24',0,'2026-02-28 01:25:24','2026-02-28 01:25:24'),(10,'鍒涙柊鍒涗笟澶ц禌娲诲姩棰勫憡','瀛︽牎灏嗕簬2024骞?0鏈堜妇鍔炲垱鏂板垱涓氬ぇ璧涳紝鏃ㄥ湪鍩瑰吇瀛︾敓鐨勫垱鏂版剰璇嗗拰鍒涗笟鑳藉姏銆傛瘮璧涘垎涓哄垱鎰忕粍鍜屽垱涓氱粍锛屾杩庡悇涓撲笟瀛︾敓缁勯槦鍙傚姞銆傝缁嗘椿鍔ㄤ俊鎭皢鍦ㄨ繎鏈熷彂甯冿紝璇峰叧娉ㄥ悗缁€氱煡銆?,9,'ALL','MEDIUM','PUBLISHED','2026-02-28 01:25:24',0,'2026-02-28 01:25:24','2026-02-28 01:25:24'),(11,'蹇楁効鏈嶅姟娲诲姩鎷涘嫙閫氱煡','涓哄煿鍏诲鐢熺殑绀句細璐ｄ换鎰燂紝瀛︽牎灏嗙粍缁囩郴鍒楀織鎰挎湇鍔℃椿鍔紝鍖呮嫭绀惧尯鏈嶅姟銆佺幆淇濆叕鐩娿€佹敮鏁欏姪瀛︾瓑銆傚弬涓庡織鎰挎湇鍔″彲鑾峰緱鐩稿簲瀛︽椂瀛﹀垎璁ゅ畾銆傝鏈夋剰鍚戠殑鍚屽鍦ㄦ椿鍔ㄨ鎯呴〉闈㈡煡鐪嬪叿浣撴椿鍔ㄥ苟鎶ュ悕銆?,9,'STUDENT','LOW','PUBLISHED','2026-02-28 01:25:24',0,'2026-02-28 01:25:24','2026-02-28 01:25:24'),(12,'瀛︽湳璁插骇娲诲姩瀹夋帓閫氱煡','瀛︽牎灏嗛個璇风煡鍚嶄笓瀹跺鑰呬妇鍔炵郴鍒楀鏈搴э紝娑电洊绉戞妧鍓嶆部銆佷汉鏂囩ぞ绉戙€佽亴涓氬彂灞曠瓑澶氫釜涓婚銆傚弬涓庤搴у彲鑾峰緱瀛︽椂璁ゅ畾锛屽叿浣撹搴у畨鎺掑皢鍦ㄦ椿鍔ㄨ鎯呴〉闈㈠彂甯冿紝璇峰悓瀛︿滑鍏虫敞骞剁Н鏋佸弬涓庛€?,9,'ALL','MEDIUM','PUBLISHED','2026-02-28 01:25:24',0,'2026-02-28 01:25:24','2026-02-28 01:25:24');
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `certificate`
--

DROP TABLE IF EXISTS `certificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `certificate_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activity_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `certificate_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `issue_date` date NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `certificate_number` (`certificate_number`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificate`
--

LOCK TABLES `certificate` WRITE;
/*!40000 ALTER TABLE `certificate` DISABLE KEYS */;
INSERT INTO `certificate` VALUES (1,'CERT-TEST000001',1,7,'娴嬭瘯璇佷功','2026-02-26','2026-02-26 15:06:49'),(4,'CERT-8FAA718BCC20',17,7,'????????','2026-02-26','2026-02-26 15:20:55');
/*!40000 ALTER TABLE `certificate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact_relation`
--

DROP TABLE IF EXISTS `contact_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `requester_id` bigint NOT NULL COMMENT '???ID',
  `addressee_id` bigint NOT NULL COMMENT '????ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '???PENDING-??? ACCEPTED-??? REJECTED-???',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_requester_addressee` (`requester_id`,`addressee_id`),
  KEY `idx_requester` (`requester_id`),
  KEY `idx_addressee` (`addressee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='??????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_relation`
--

LOCK TABLES `contact_relation` WRITE;
/*!40000 ALTER TABLE `contact_relation` DISABLE KEYS */;
INSERT INTO `contact_relation` VALUES (10,8,10,'ACCEPTED','2026-02-15 04:14:29','2026-02-15 04:14:29'),(11,8,7,'ACCEPTED','2026-02-16 20:44:34','2026-02-16 20:44:34');
/*!40000 ALTER TABLE `contact_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_record`
--

DROP TABLE IF EXISTS `credit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `student_id` bigint NOT NULL COMMENT '??ID',
  `activity_id` bigint NOT NULL COMMENT '??ID',
  `registration_id` bigint NOT NULL COMMENT '??ID',
  `credit_hours` decimal(5,2) DEFAULT '0.00' COMMENT '??',
  `credit_points` decimal(5,2) DEFAULT '0.00' COMMENT '??',
  `academic_year` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '??',
  `semester` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '??',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_academic_year` (`academic_year`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_record`
--

LOCK TABLES `credit_record` WRITE;
/*!40000 ALTER TABLE `credit_record` DISABLE KEYS */;
INSERT INTO `credit_record` VALUES (13,7,13,22,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-02-16 18:12:27','2026-02-16 18:12:27'),(14,10,13,21,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-02-16 20:13:42','2026-02-16 20:13:42'),(15,7,16,33,3.00,3.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-02-17 02:59:24','2026-02-17 02:59:24'),(16,7,17,34,4.00,4.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-02-26 22:33:32','2026-02-26 22:33:32'),(17,7,20,35,2.00,5.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-02-27 23:01:43','2026-02-27 23:01:43'),(18,7,21,36,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-18 22:38:37','2026-04-18 22:38:37'),(19,7,22,37,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-21 20:08:54','2026-04-21 20:08:54'),(20,7,24,39,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-21 20:52:14','2026-04-21 20:52:14'),(21,7,25,40,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-24 22:26:21','2026-04-24 22:26:21'),(22,7,26,41,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-25 01:05:52','2026-04-25 01:05:52'),(23,7,27,42,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-25 01:11:24','2026-04-25 01:11:24'),(24,7,28,43,2.00,2.00,'2025-2026','鏄ュ瀛︽湡',0,'2026-04-25 15:43:13','2026-04-25 15:43:13');
/*!40000 ALTER TABLE `credit_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閻劍鍩涢崥',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '??',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閻喎鐤勬慨鎾虫倳',
  `student_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '??',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '???',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '??',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '???STUDENT/TEACHER/ADMIN',
  `status` tinyint DEFAULT '1' COMMENT '???0-?? 1-??',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `username_2` (`username`),
  UNIQUE KEY `username_3` (`username`),
  UNIQUE KEY `username_4` (`username`),
  UNIQUE KEY `username_5` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='???';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (7,'鍒樻捣楦?,'$2a$10$84I3oAUKppfwgtfU7QFlNeHHMXOv97fTy9brK73578V1l5cwoVovy','鍒樻捣楦?,'2022001','151796121684','lhh@qq.com','STUDENT',1,0,'2026-02-15 01:33:39','2026-06-11 11:04:27'),(8,'鏁欏笀鍒樻捣楦?,'$2a$10$75jRFDcal.7JtMCG2Ywm7.kvBv6gWwRvx6DPII/NdzYOVU.OZrGka','鏁欏笀鍒樻捣楦?,'1001','187456415618','ahh@qq.com','TEACHER',1,0,'2026-02-15 01:35:43','2026-02-15 01:35:43'),(9,'root','$2a$10$84I3oAUKppfwgtfU7QFlNeHHMXOv97fTy9brK73578V1l5cwoVovy','root','1','15618156156','root@qq.com','ADMIN',1,0,'2026-02-15 01:37:15','2026-02-15 01:37:15'),(10,'闄堜寒鏂?,'$2a$10$D9XYJ3Q1g24tCf9RFLWMeuU4ltQNjoVOxjUGqDAZVUO2uGx2CJsgm','闄堜寒鏂?,'2022002','156198491321','qqq@qq.com','STUDENT',1,0,'2026-02-15 02:51:12','2026-02-15 02:51:12'),(11,'student1','$2a$10$n51hE3qE3kRdD7UMCz086OBss/v5Pbhj8iM1fplwqh2gsl4yQkLr2','寮犱笁','2024001',NULL,NULL,'STUDENT',1,0,'2026-02-28 19:48:32','2026-06-11 11:03:29'),(12,'student2','$2a$10$n51hE3qE3kRdD7UMCz086OBss/v5Pbhj8iM1fplwqh2gsl4yQkLr2','鏉庡洓','2024002',NULL,NULL,'STUDENT',1,0,'2026-02-28 19:48:32','2026-06-11 11:03:29');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_notification`
--

DROP TABLE IF EXISTS `system_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `user_id` bigint NOT NULL COMMENT '??ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '????',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'INFO' COMMENT '???INFO-WARNING-SUCCESS-ERROR',
  `is_read` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `related_id` bigint DEFAULT NULL COMMENT '??ID',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '????',
  `deleted` tinyint DEFAULT '0' COMMENT '?????0-? 1-?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_notification`
--

LOCK TABLES `system_notification` WRITE;
/*!40000 ALTER TABLE `system_notification` DISABLE KEYS */;
INSERT INTO `system_notification` VALUES (12,7,'娲诲姩寤鸿鍥炲','鎮ㄦ敹鍒颁簡娲诲姩寤鸿鐨勫洖澶嶏紝璇锋煡鐪嬨€?,'INFO',1,6,'SUGGESTION_REPLY',0,'2026-04-04 21:38:24'),(13,7,'娲诲姩淇敼寤鸿','鎮ㄦ敹鍒颁簡鏂扮殑娲诲姩淇敼寤鸿锛岃鏌ョ湅銆?,'INFO',1,7,'ACTIVITY_SUGGESTION',0,'2026-04-18 22:38:29');
/*!40000 ALTER TABLE `system_notification` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-12  5:25:12
