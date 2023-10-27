-- MySQL dump 10.13  Distrib 8.0.34, for Linux (x86_64)
--
-- Host: localhost    Database: healtechBd
-- ------------------------------------------------------
-- Server version	8.0.34-0ubuntu0.22.04.1

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
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` bigint DEFAULT NULL,
  `app_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6yh8n01w88jxj3cofbdqweaxk` (`app_user_id`),
  CONSTRAINT `FKdlrbgmerxspxw8v7n9y3t0tk7` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1,0,6);
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ambulance`
--

DROP TABLE IF EXISTS `ambulance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ambulance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dp` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `ambulance_provider_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq66i0dg5gxycbxp0kn7jfgjeh` (`ambulance_provider_id`),
  CONSTRAINT `FKq66i0dg5gxycbxp0kn7jfgjeh` FOREIGN KEY (`ambulance_provider_id`) REFERENCES `ambulance_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ambulance`
--

LOCK TABLES `ambulance` WRITE;
/*!40000 ALTER TABLE `ambulance` DISABLE KEYS */;
/*!40000 ALTER TABLE `ambulance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ambulance_provider`
--

DROP TABLE IF EXISTS `ambulance_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ambulance_provider` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` bigint DEFAULT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  `app_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ej9ogdt7s3tp4ieop1c4vmwi9` (`app_user_id`),
  CONSTRAINT `FKfmanoxp8djdteb0ay6fjt75bk` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ambulance_provider`
--

LOCK TABLES `ambulance_provider` WRITE;
/*!40000 ALTER TABLE `ambulance_provider` DISABLE KEYS */;
INSERT INTO `ambulance_provider` VALUES (1,0,'','Dhaka',5);
/*!40000 ALTER TABLE `ambulance_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ambulance_trips`
--

DROP TABLE IF EXISTS `ambulance_trips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ambulance_trips` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checked` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `review_checked` int DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `trx_id` varchar(255) DEFAULT NULL,
  `ambulance_provider_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo2svjjnr4lipe3uw9xsdueocu` (`ambulance_provider_id`),
  KEY `FKgc91gxes4fvtu2nkwg8a2w8uh` (`user_id`),
  CONSTRAINT `FKgc91gxes4fvtu2nkwg8a2w8uh` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKo2svjjnr4lipe3uw9xsdueocu` FOREIGN KEY (`ambulance_provider_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ambulance_trips`
--

LOCK TABLES `ambulance_trips` WRITE;
/*!40000 ALTER TABLE `ambulance_trips` DISABLE KEYS */;
INSERT INTO `ambulance_trips` VALUES (1,0,'2023-10-02','Khulna','{\"latitude\":23.607698,\"longitude\":90.515676}',NULL,NULL,1000,NULL,'Dhaka',NULL,NULL,1);
/*!40000 ALTER TABLE `ambulance_trips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ambulancetrip_bidder`
--

DROP TABLE IF EXISTS `ambulancetrip_bidder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ambulancetrip_bidder` (
  `trip_id` bigint NOT NULL,
  `ambulance_provider_id` bigint NOT NULL,
  KEY `FKm2ilqikie01ojauxxq8g75nko` (`ambulance_provider_id`),
  KEY `FKkex0jfmn3tpdpt6125hycs29g` (`trip_id`),
  CONSTRAINT `FKkex0jfmn3tpdpt6125hycs29g` FOREIGN KEY (`trip_id`) REFERENCES `ambulance_trips` (`id`),
  CONSTRAINT `FKm2ilqikie01ojauxxq8g75nko` FOREIGN KEY (`ambulance_provider_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ambulancetrip_bidder`
--

LOCK TABLES `ambulancetrip_bidder` WRITE;
/*!40000 ALTER TABLE `ambulancetrip_bidder` DISABLE KEYS */;
/*!40000 ALTER TABLE `ambulancetrip_bidder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_user`
--

DROP TABLE IF EXISTS `app_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_verified` bit(1) NOT NULL,
  `ban_removal_date` date DEFAULT NULL,
  `contact_no` varchar(255) DEFAULT NULL,
  `dp` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1j9d9a06i600gd43uu3km82jw` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_user`
--

LOCK TABLES `app_user` WRITE;
/*!40000 ALTER TABLE `app_user` DISABLE KEYS */;
INSERT INTO `app_user` VALUES (1,_binary '',NULL,'01988506830','https://www.doctorbangladesh.com/wp-content/uploads/Dr.-Abdullah-Al-Mukit.jpg','almahmudaraf@gmail.com','Mahmud','Araf','$2a$10$K07fLMNTqF0ShcqK54Y3JeukBgFG120bfXWzj.IAavNHpYaYqvXoS'),(2,_binary '',NULL,'01988506830','https://www.doctorbangladesh.com/wp-content/uploads/Dr.-Md.-Rofiqul-Islam.jpg','fahim@gmail.com','Fahim','Rahman','$2a$10$p0yVJye8a8jEoCwWLBEeiueRzSr.I2CNf/6S.DtGLJMxFossoVeFe'),(3,_binary '',NULL,'01988506830','https://www.doctorbangladesh.com/wp-content/uploads/Dr.-Mohd.-Harun-Or-Rashid.jpg','square@gmail.com','Md ','Rahman','$2a$10$mvaqnIBurMnfpzNaz9rZtONuusM7GLBDbTwEZOi29WQhfPmmq8sWC'),(4,_binary '',NULL,'01988506830','https://www.doctorbangladesh.com/wp-content/uploads/Prof.-Dr.-Md.-Jonaid-Shafiq.jpg','brothers@gmail.com','Md ','Azhar','$2a$10$Rhkn0R/FWiJ4CyLBFJMYJuTinrz9.C.SdHKs4L/GlEUruQqVct64u'),(5,_binary '',NULL,'01988506830','https://www.doctorbangladesh.com/wp-content/uploads/Prof.-Dr.-Moinul-Hossain.jpg','dibbyoAmbulance@gmail.com','Dibbyo','Roy','$2a$10$jduY7Db5xItacB5UIL0iHuXGpbFdbJ0sd6lPITeT4xHMK.aJS8OC2'),(6,_binary '',NULL,'01988506830','https://www.doctorbangladesh.com/wp-content/uploads/Dr.-Md.-Mazharul-Alam-Sohel.jpg','admin@healtechbd.com','HealTechBD','Corp.','$2a$10$MW7byQun.kwg96/5Bpyr2Ofm.1vAfvVLoLFP/R/bUTp6ubk/TnX.W'),(7,_binary '',NULL,'1234567890','https://www.doctorbangladesh.com/wp-content/uploads/Dr.-MHM-Delwar-Hossain.jpg','johndoe@yahoo.com','John','Doe','$2a$10$FfWHhL7xlReVNIWyYFdVF.Dy9Z8.6tsH590OIXdfisAlkfeOPC3WW');
/*!40000 ALTER TABLE `app_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `doctor_id` bigint DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,NULL,NULL),(2,NULL,NULL),(3,2,'Dhaka Medical'),(4,2,'Dhaka Medical'),(5,2,'Dhaka Medical'),(6,2,'c@gmail.com');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diagnosis`
--

DROP TABLE IF EXISTS `diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dept_contact_no` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `end_time` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `start_time` varchar(255) DEFAULT NULL,
  `hospital_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfvb10r5f3c9he9a4vs9lfe6au` (`hospital_id`),
  CONSTRAINT `FKfvb10r5f3c9he9a4vs9lfe6au` FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diagnosis`
--

LOCK TABLES `diagnosis` WRITE;
/*!40000 ALTER TABLE `diagnosis` DISABLE KEYS */;
INSERT INTO `diagnosis` VALUES (1,'01908027698','High Quality X-Ray','15:45','X-Ray',1000,'11:30',1);
/*!40000 ALTER TABLE `diagnosis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diagnosis_orders`
--

DROP TABLE IF EXISTS `diagnosis_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checked` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `dept_contact_no` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `reporturl` varchar(255) DEFAULT NULL,
  `review_checked` int DEFAULT NULL,
  `time` double DEFAULT NULL,
  `trx_id` varchar(255) DEFAULT NULL,
  `hospital_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr848rtub9xskbdenlafy9vpc1` (`hospital_id`),
  KEY `FKfwsw5kcjecixsrslcvggl5c15` (`user_id`),
  CONSTRAINT `FKfwsw5kcjecixsrslcvggl5c15` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKr848rtub9xskbdenlafy9vpc1` FOREIGN KEY (`hospital_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diagnosis_orders`
--

LOCK TABLES `diagnosis_orders` WRITE;
/*!40000 ALTER TABLE `diagnosis_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `diagnosis_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_available_time`
--

DROP TABLE IF EXISTS `doctor_available_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_available_time` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avail_time` double DEFAULT NULL,
  `count` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `day` varchar(255) DEFAULT NULL,
  `end_time` double DEFAULT NULL,
  `start_time` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_available_time`
--

LOCK TABLES `doctor_available_time` WRITE;
/*!40000 ALTER TABLE `doctor_available_time` DISABLE KEYS */;
INSERT INTO `doctor_available_time` VALUES (37,23.59,0,'2023-10-25','Wednesday',12,23.59);
/*!40000 ALTER TABLE `doctor_available_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_online_available_time`
--

DROP TABLE IF EXISTS `doctor_online_available_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_online_available_time` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avail_time` double DEFAULT NULL,
  `count` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `day` varchar(255) DEFAULT NULL,
  `end_time` double DEFAULT NULL,
  `start_time` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_online_available_time`
--

LOCK TABLES `doctor_online_available_time` WRITE;
/*!40000 ALTER TABLE `doctor_online_available_time` DISABLE KEYS */;
INSERT INTO `doctor_online_available_time` VALUES (9,23.59,0,'2023-10-25','Wednesday',12,23.59),(10,23.59,0,'2023-10-31','Tuesday',12,23.59);
/*!40000 ALTER TABLE `doctor_online_available_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_serials`
--

DROP TABLE IF EXISTS `doctor_serials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_serials` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appointment_date` date DEFAULT NULL,
  `checked` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `prescription` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `review_checked` int DEFAULT NULL,
  `time` double DEFAULT NULL,
  `trx_id` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `doctor_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8alrnwr19l7mb5pby9edfhxjj` (`doctor_id`),
  KEY `FKgmobpvmeaqmxi0s1n895xac6` (`user_id`),
  CONSTRAINT `FK8alrnwr19l7mb5pby9edfhxjj` FOREIGN KEY (`doctor_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKgmobpvmeaqmxi0s1n895xac6` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_serials`
--

LOCK TABLES `doctor_serials` WRITE;
/*!40000 ALTER TABLE `doctor_serials` DISABLE KEYS */;
INSERT INTO `doctor_serials` VALUES (1,'2023-10-03',0,'2023-10-02','TR00118yte2P31696253249140',NULL,310,1,23.59,NULL,'online',2,1),(2,'2023-10-03',0,'2023-10-02','TR0011n1SXevS1696253251258',NULL,310,1,23.59,NULL,'online',2,1),(3,'2023-10-03',0,'2023-10-02','TR0011e6OguEd1696253604341',NULL,310,1,24.29,NULL,'online',2,1);
/*!40000 ALTER TABLE `doctor_serials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` bigint DEFAULT NULL,
  `bio` varchar(255) NOT NULL,
  `current_hospital` varchar(255) DEFAULT NULL,
  `degrees` varchar(255) DEFAULT NULL,
  `expertise` varchar(255) DEFAULT NULL,
  `offline_fee` bigint DEFAULT NULL,
  `online_fee` bigint DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  `app_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_htv10ufkke7s17j9kliyl0p2f` (`app_user_id`),
  CONSTRAINT `FKdc34ymalqv8rk8u2729yoqs0l` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (1,900,'A good Doctor','Dhaka Medical','MBBS','',400,300,'dhaka',2);
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hospital`
--

DROP TABLE IF EXISTS `hospital`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hospital` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` bigint DEFAULT NULL,
  `bio` varchar(255) NOT NULL,
  `hospital_name` varchar(255) NOT NULL,
  `place` varchar(255) NOT NULL,
  `app_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_d5lb9bxfwvjl35x56ay2n307` (`app_user_id`),
  CONSTRAINT `FKej4irgi29n055sdsw34v2ymd8` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hospital`
--

LOCK TABLES `hospital` WRITE;
/*!40000 ALTER TABLE `hospital` DISABLE KEYS */;
INSERT INTO `hospital` VALUES (1,0,'','Square Hospital','dhaka',3);
/*!40000 ALTER TABLE `hospital` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicine_orders`
--

DROP TABLE IF EXISTS `medicine_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicine_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `delivered` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `review_checked` int DEFAULT NULL,
  `trx_id` varchar(255) DEFAULT NULL,
  `pharmacy_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq2asdotng2cwxw7pd24nqwlbl` (`pharmacy_id`),
  KEY `FK351hivd9uhd0k0lu07rrb01yi` (`user_id`),
  CONSTRAINT `FK351hivd9uhd0k0lu07rrb01yi` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKq2asdotng2cwxw7pd24nqwlbl` FOREIGN KEY (`pharmacy_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicine_orders`
--

LOCK TABLES `medicine_orders` WRITE;
/*!40000 ALTER TABLE `medicine_orders` DISABLE KEYS */;
INSERT INTO `medicine_orders` VALUES (1,'2023-10-02',0,'AFDC-2 F/C (ACI HealthCare Limited)  - 3 pieces, \n',NULL,'TR0011AcpiDhQ1696254362174','{\"lat\":23.7270478,\"lng\":90.4031032}',18,NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `medicine_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicine_reminders`
--

DROP TABLE IF EXISTS `medicine_reminders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicine_reminders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `days` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjt3q6hxwghnfq09vbht6c31n5` (`user_id`),
  CONSTRAINT `FKjt3q6hxwghnfq09vbht6c31n5` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicine_reminders`
--

LOCK TABLES `medicine_reminders` WRITE;
/*!40000 ALTER TABLE `medicine_reminders` DISABLE KEYS */;
/*!40000 ALTER TABLE `medicine_reminders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicines`
--

DROP TABLE IF EXISTS `medicines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicines` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manufacturer_name` varchar(255) DEFAULT NULL,
  `generic_name` varchar(255) DEFAULT NULL,
  `medicine_name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=499 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicines`
--

LOCK TABLES `medicines` WRITE;
/*!40000 ALTER TABLE `medicines` DISABLE KEYS */;
INSERT INTO `medicines` VALUES (1,'ACI HealthCare Limited','Isoniazid + Rifampicin','AFDC-2 F/C',6),(2,'ACI HealthCare Limited','Ethambutol + Isoniazid + Pyrazinamide + Rifampicin','AFDC-4 F/C',17),(3,'Active Fine Chemicals Ltd.','Rosuvastatin','Rosufine 5',10),(4,'Ad-din Pharmaceuticals Ltd.','Albendazole','Aldaben  DS  400',4),(5,'Ad-din Pharmaceuticals Ltd.','Chlorpheniramine Maleate','Alerjess',20),(6,'Ad-din Pharmaceuticals Ltd.','Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1','B Plex',62.19),(7,'Ad-din Pharmaceuticals Ltd.','Ciprofloxacin','Ciproxen    250',7),(8,'Ad-din Pharmaceuticals Ltd.','Sulphamethoxazole + Trimethoprim','Co-Try',21.5),(9,'Ad-din Pharmaceuticals Ltd.','Sulphamethoxazole + Trimethoprim','Co-Try',2),(10,'Ad-din Pharmaceuticals Ltd.','Aluminium Oxide + Magnesium Hydroxide','Duomeal',40),(11,'Ad-din Pharmaceuticals Ltd.','Paracetamol','Feva',18),(12,'Ad-din Pharmaceuticals Ltd.','Mebendazole','Helben',14.8),(13,'Ad-din Pharmaceuticals Ltd.','Amoxicillin','J Mox    250',2.86),(14,'Ad-din Pharmaceuticals Ltd.','Amoxicillin','J Mox',45),(15,'Ad-din Pharmaceuticals Ltd.','Amoxicillin','J Mox',28.32),(16,'Ad-din Pharmaceuticals Ltd.','Diclofenac Sodium','Jefenac  TR  100',3),(17,'Ad-din Pharmaceuticals Ltd.','Zinc','J Zinc    10mg/5ml',30),(18,'Ad-din Pharmaceuticals Ltd.','Metronidazole','Metra    400',1.05),(19,'Ad-din Pharmaceuticals Ltd.','Salbutamol','Ventosol',20),(20,'Ad-din Pharmaceuticals Ltd.','Amoxicillin','J Mox    500',5.71),(21,'Ad-din Pharmaceuticals Ltd.','Metronidazole','Metra',24),(22,'Ad-din Pharmaceuticals Ltd.','Ciprofloxacin','Ciproxen    500',14),(23,'Ad-din Pharmaceuticals Ltd.','Cephradine','Jedine    500',12.5),(24,'Ad-din Pharmaceuticals Ltd.','Cephradine','Jedine',80),(25,'Ad-din Pharmaceuticals Ltd.','Cephradine','Jedine',50),(26,'Ad-din Pharmaceuticals Ltd.','Ferrous Sulphate + Folic Acid + Zinc','Red Plus',3),(27,'Ad-din Pharmaceuticals Ltd.','Omeprazole','Omrazole    20',4.5),(28,'Ad-din Pharmaceuticals Ltd.','Cetirizine Dihydrochloride','Cirizin    10',2),(29,'Ad-din Pharmaceuticals Ltd.','Cetirizine Dihydrochloride','Cirizin',22),(30,'Ad-din Pharmaceuticals Ltd.','Domperidone','Gutset',2),(31,'Ad-din Pharmaceuticals Ltd.','Domperidone','Gutset',20),(32,'Ad-din Pharmaceuticals Ltd.','Calcium Carbonate','Calcium A',3),(33,'Ad-din Pharmaceuticals Ltd.','Aceclofenac','Fleco',2.5),(34,'Ad-din Pharmaceuticals Ltd.','Caffeine + Paracetamol','Feva   PLUS',1.5),(35,'Ad-din Pharmaceuticals Ltd.','Cefuroxime','Adrox',181),(36,'Ad-din Pharmaceuticals Ltd.','Cefixime','Cefixime A',180),(37,'Ad-din Pharmaceuticals Ltd.','Cefixime','Cefixime A   200',30),(38,'Ad-din Pharmaceuticals Ltd.','Erythromycin','Erythromycin  A',60),(39,'Ad-din Pharmaceuticals Ltd.','Loratadine','Cladin',2.25),(40,'Ad-din Pharmaceuticals Ltd.','Ascorbic Acid + Elemental Iron + Folic Acid + Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1 + Zinc','Ad - All',3),(41,'Ad-din Pharmaceuticals Ltd.','Calcium + Vitamin D3','Calcium   A&D',3.8),(42,'Ad-din Pharmaceuticals Ltd.','Albendazole','Aldaben',15),(43,'Ad-din Pharmaceuticals Ltd.','Levocetirizine Hydrochloride','Cevozin    5',2),(44,'Ad-din Pharmaceuticals Ltd.','Esomeprazole','Somazole    20',5.75),(45,'Ad-din Pharmaceuticals Ltd.','Desloratadine','Adlorin    5',2.5),(46,'Ad-din Pharmaceuticals Ltd.','Elemental Iron + Folic Acid + Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1 + Zinc','Redplus  Extra',3.5),(47,'Ad-din Pharmaceuticals Ltd.','Flucloxacillin','Oxacol',9),(48,'Ad-din Pharmaceuticals Ltd.','Metformin Hydrochloride','D-MET 850',3),(49,'Ad-din Pharmaceuticals Ltd.','Clotrimazole','Loderm',23),(50,'Ad-din Pharmaceuticals Ltd.','Fluconazole','Trigal',7),(51,'Ad-din Pharmaceuticals Ltd.','Fluconazole','Trigal',18),(52,'Ad-din Pharmaceuticals Ltd.','Gliclazide','Sugred',6),(53,'Ad-din Pharmaceuticals Ltd.','Miconazole','Mitigal',30),(54,'Ad-din Pharmaceuticals Ltd.','Econazole Nitrate','Zolecon',25),(55,'Ad-din Pharmaceuticals Ltd.','Clobetasone Butyrate','Amobet',30),(56,'Ad-din Pharmaceuticals Ltd.','Ambroxol','Mucovan',40),(57,'Ad-din Pharmaceuticals Ltd.','Azithromycin','Vinzam',30),(58,'Ad-din Pharmaceuticals Ltd.','Azithromycin','Vinzam',82),(59,'Ad-din Pharmaceuticals Ltd.','Ketorolac Tromethamine','Lacor',9),(60,'Ad-din Pharmaceuticals Ltd.','Carvedilol','Avidol 6.25',3),(61,'Ad-din Pharmaceuticals Ltd.','Ramipril','Cartace 2.5',4.6),(62,'Ad-din Pharmaceuticals Ltd.','Clopidogrel','Clogrel',9),(63,'Ad-din Pharmaceuticals Ltd.','Amlodipine','Nopidin 5',3),(64,'Ad-din Pharmaceuticals Ltd.','Metoprolol Tartrate','Prolol',1.3),(65,'Ad-din Pharmaceuticals Ltd.','Cyanocobalamin + Pyridoxine Hydrochloride + Vitamin B1','Combomin',4),(66,'Ad-din Pharmaceuticals Ltd.','Econazole Nitrate + Triamcinolone Acetonide','Conatrim',34),(67,'Ad-din Pharmaceuticals Ltd.','Diclofenac Sodium','Jefenac Gel',12.9),(68,'Ad-din Pharmaceuticals Ltd.','Baclofen','Relofen 10',8),(69,'Ad-din Pharmaceuticals Ltd.','Naproxen','Ultranax 500',8),(70,'Ad-din Pharmaceuticals Ltd.','Tramadol Hydrochloride','Ultradol',7.5),(71,'Ad-din Pharmaceuticals Ltd.','Omeprazole','Omrazol 40',7),(72,'Ad-din Pharmaceuticals Ltd.','Meclizine Hydrochloride','Meczin',2.5),(73,'Ad-din Pharmaceuticals Ltd.','Dextromethorphan Hydrobromide','Metodex',30),(74,'Ad-din Pharmaceuticals Ltd.','Fexofenadine Hydrochloride','Xofast 60',3.5),(75,'Ad-din Pharmaceuticals Ltd.','Fexofenadine Hydrochloride','Xofast 120',6),(76,'Ad-din Pharmaceuticals Ltd.','Aspirin + Clopidogrel','Clogrel Plus',11),(77,'Ad-din Pharmaceuticals Ltd.','Sucralose','Calonil',1),(78,'Ad-din Pharmaceuticals Ltd.','Atorvastatin','Vastor 10',10),(79,'Ad-din Pharmaceuticals Ltd.','Esomeprazole','Somazole 40',9),(80,'Ad-din Pharmaceuticals Ltd.','Pantoprazole','Nixpan 20',3.45),(81,'Ad-din Pharmaceuticals Ltd.','Pantoprazole','Nixpan 40',5.7),(82,'Ad-din Pharmaceuticals Ltd.','Nitazoxanide','Proxa-A',35),(83,'Ad-din Pharmaceuticals Ltd.','Amlodipine + Atenolol','Nopirol',4),(84,'Ad-din Pharmaceuticals Ltd.','Frusemide + Spironolactone','Frunal',6),(85,'Ad-din Pharmaceuticals Ltd.','Domperidone','Gutset',35),(86,'Ad-din Pharmaceuticals Ltd.','Glimepiride','Glaryl 1',2.5),(87,'Ad-din Pharmaceuticals Ltd.','Glimepiride','Glaryl 2',4),(88,'Ad-din Pharmaceuticals Ltd.','Metformin Hydrochloride','D-Met 500',4),(89,'Ad-din Pharmaceuticals Ltd.','Losartan Potassium','Carlos 50',7),(90,'Ad-din Pharmaceuticals Ltd.','Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1 + Zinc','B-Z',2),(91,'Ad-din Pharmaceuticals Ltd.','Bromhexine Hydrochloride','Mucoxin',40),(92,'Ad-din Pharmaceuticals Ltd.','Fexofenadine Hydrochloride','Xofast',50),(93,'Ad-din Pharmaceuticals Ltd.','Paracetamol','Feva',20),(94,'Ad-din Pharmaceuticals Ltd.','Etoricoxib','Aroxia',6.72),(95,'Ad-din Pharmaceuticals Ltd.','Etoricoxib','Aroxia',9.2),(96,'Ad-din Pharmaceuticals Ltd.','Etoricoxib','Aroxia',11.5),(97,'Ad-din Pharmaceuticals Ltd.','Levofloxacin','Quiva 500 mg',13),(98,'Ad-din Pharmaceuticals Ltd.','Tiemonium Methylsulphate','Tium',4),(99,'Ad-din Pharmaceuticals Ltd.','Nitazoxanide','Proxa-A',9),(100,'Ad-din Pharmaceuticals Ltd.','Clobetasol Propionate','Aclosol',45),(101,'Ad-din Pharmaceuticals Ltd.','Anhydrous Glucose + Potassium Chloride + Sodium Chloride + Trisodium Citrate','New ORS-A Oral Saline',4),(102,'Ad-din Pharmaceuticals Ltd.','Diclofenac Sodium','Jefenac 50',12),(103,'Ad-din Pharmaceuticals Ltd.','Diclofenac Sodium','Jefenac 12.5',7.5),(104,'Ad-din Pharmaceuticals Ltd.','Paracetamol','Feva 250',5),(105,'Ad-din Pharmaceuticals Ltd.','Paracetamol','Feva 125',4),(106,'Ad-din Pharmaceuticals Ltd.','Montelukast','Airflow',15),(107,'Ad-din Pharmaceuticals Ltd.','Elemental Iron + Folic Acid + Zinc','RED Plus CI',4),(108,'Adova Pharmaceuticals Ltd.','Levamisole + Triclabendazole','Levaqure Bolus Vet',25),(109,'Adova Pharmaceuticals Ltd.','Levamisole','Advasol',20),(110,'Adova Pharmaceuticals Ltd.','Albendazole','Adminth Bolus',7),(111,'Adova Pharmaceuticals Ltd.','Promethazine Hydrochloride','Thazine Bolus Vet',3.34),(112,'Adova Pharmaceuticals Ltd.','Allopurinol','A-Toril Powder (Vet)',330),(113,'Adova Pharmaceuticals Ltd.','Acetylsalicylic Acid + Vitamin C','Salic Plus Powder',40),(114,'Adova Pharmaceuticals Ltd.','Metronidazole','Meqdaz Powder (Vet)',130),(115,'Adova Pharmaceuticals Ltd.','Diphenhydramine Hydrochloride','Vetphen Bolus (Vet)',3.5),(116,'Adova Pharmaceuticals Ltd.','Amprolium + Sulfaquinoxaline Sodium + Vitamin K','Olinevet Powder',250),(117,'Adova Pharmaceuticals Ltd.','Levamisole + Triclabendazole','Levaqur DS Bolus (Vet)',29),(118,'Adova Pharmaceuticals Ltd.','Levamisole','Advasol Powder (Vet)',150),(119,'Adova Pharmaceuticals Ltd.','Albendazole','Adminth Suspension (Vet)',230),(120,'Adova Pharmaceuticals Ltd.','Furosemide','Furisim Vet Bolus',20),(121,'Adova Pharmaceuticals Ltd.','Enrofloxacin','AdovaEnro (Vet)',220),(122,'Adova Pharmaceuticals Ltd.','Oxytetracycline','Adoxte (Vet)',95),(123,'Adova Pharmaceuticals Ltd.','Ciprofloxacin','AdCflox (Vet)',20),(124,'Adova Pharmaceuticals Ltd.','Doxycycline','AdovaDox (Vet)',230),(125,'Adova Pharmaceuticals Ltd.','Ciprofloxacin','AdCflox (Vet)',233),(126,'Adova Pharmaceuticals Ltd.','Chlortetracycline Hydrochloride','Chlorel (Vet)',160),(127,'Adova Pharmaceuticals Ltd.','Tylosin','AdLos (Vet)',210),(128,'Adova Pharmaceuticals Ltd.','Norfloxacine','AdNorflox (Vet)',105),(129,'Adova Pharmaceuticals Ltd.','Oxytetracycline','Adoxter (Vet)',3.4),(130,'Adova Pharmaceuticals Ltd.','Tilmicosin','Adotil (Vet)',800),(131,'Adova Pharmaceuticals Ltd.','Amprolium','Adpoli (Vet)',213),(132,'Adova Pharmaceuticals Ltd.','Toltrazuril','AToltra (Vet)',445),(133,'Adova Pharmaceuticals Ltd.','Levofloxacin','AdLeflox (Vet) Powder',280),(134,'Adova Pharmaceuticals Ltd.','Neomycin Sulphate','AdNeocin (Vet)',290),(135,'Adova Pharmaceuticals Ltd.','Marbofloxacin','AdMarbo (Vet)',25),(136,'Adova Pharmaceuticals Ltd.','Gentamicin','Gentava (Vet)',800),(137,'Adova Pharmaceuticals Ltd.','Lincomycin','AdLinco (Vet)',200),(138,'Advanced Chemical Industries Limited','Calcium Carbonate','Acical    500',4.01),(139,'Advanced Chemical Industries Limited','Dexamethasone','Acicot',65),(140,'Advanced Chemical Industries Limited','Sparfloxacin','Aciflox    200',12),(141,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IM  1 gm',191.29),(142,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IV  500 mg',130.39),(143,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IM  500 mg',130.39),(144,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IV  250 mg',100.3),(145,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IV  1 gm',191.29),(146,'Advanced Chemical Industries Limited','Ampicillin','Acipillin',50),(147,'Advanced Chemical Industries Limited','Chlorpheniramine Maleate','Acira',14.09),(148,'Advanced Chemical Industries Limited','Cetirizine Dihydrochloride','Acitrin',30.09),(149,'Advanced Chemical Industries Limited','Cetirizine Dihydrochloride','Acitrin    10',3.01),(150,'Advanced Chemical Industries Limited','Ceftazidime','Zitum    1g',226.53),(151,'Advanced Chemical Industries Limited','Ceftazidime','Zitum    500 mg',115.78),(152,'Advanced Chemical Industries Limited','Flupenthixol + Melitracen','Adelax',5.02),(153,'Advanced Chemical Industries Limited','Metronidazole','Amotrex    400',1.27),(154,'Advanced Chemical Industries Limited','Metronidazole','Amotrex',29.9),(155,'Advanced Chemical Industries Limited','Metronidazole','Amotrex  DS  800',2.02),(156,'Advanced Chemical Industries Limited','Naproxen','Anaflex    500',9.06),(157,'Advanced Chemical Industries Limited','Naproxen','Anaflex',62.42),(158,'Advanced Chemical Industries Limited','Aluminium Oxide + Magnesium Hydroxide + Simethicone','Avlocid',75.23),(159,'Advanced Chemical Industries Limited','Amoxicillin','Avlomox    250',3.45),(160,'Advanced Chemical Industries Limited','Amoxicillin','Avlomox',30.29),(161,'Advanced Chemical Industries Limited','Amoxicillin','Avlomox    500',6.12),(162,'Advanced Chemical Industries Limited','Amoxicillin','Avlomox  DS',49.73),(163,'Advanced Chemical Industries Limited','Amoxicillin','Avlomox    500',32.1),(164,'Advanced Chemical Industries Limited','Amoxicillin','Avlomox',46.32),(165,'Advanced Chemical Industries Limited','Paracetamol','Xcel',30),(166,'Advanced Chemical Industries Limited','Chloroquine Phosphate','Avloquin',1.32),(167,'Advanced Chemical Industries Limited','Cephradine','Avlosef    250',8.02),(168,'Advanced Chemical Industries Limited','Cephradine','Avlosef',90.27),(169,'Advanced Chemical Industries Limited','Cephradine','Avlosef    1 gm',95.29),(170,'Advanced Chemical Industries Limited','Cephradine','Avlosef    500 mg',65.2),(171,'Advanced Chemical Industries Limited','Cephradine','Avlosef  DS  250',80.54),(172,'Advanced Chemical Industries Limited','Cephradine','Avlosef',65.2),(173,'Advanced Chemical Industries Limited','Cephradine','Avlosef    500',15.05),(174,'Advanced Chemical Industries Limited','Sulphamethoxazole + Trimethoprim','Avlotrin',22.21),(175,'Advanced Chemical Industries Limited','Sulphamethoxazole + Trimethoprim','Avlotrin',1.5),(176,'Advanced Chemical Industries Limited','Sulphamethoxazole + Trimethoprim','Avlotrin',2.01),(177,'Advanced Chemical Industries Limited','Cephalexin','Avloxin',69.47),(178,'Advanced Chemical Industries Limited','Cephalexin','Avloxin    500',10.57),(179,'Advanced Chemical Industries Limited','Bambuterol Hydrochloride','Buterol    10',1.51),(180,'Advanced Chemical Industries Limited','Bambuterol Hydrochloride','Buterol',35.24),(181,'Advanced Chemical Industries Limited','Bambuterol Hydrochloride','Buterol    20',3.02),(182,'Advanced Chemical Industries Limited','Salbutamol','Brodil',14.53),(183,'Advanced Chemical Industries Limited','Hyoscine Butyl Bromide','Colik    10',6.9),(184,'Advanced Chemical Industries Limited','Amlodipine','Cab    5',5.02),(185,'Advanced Chemical Industries Limited','Boric Acid + Calcium Gluconate + Dextrose Anhydrous + Magnesium Hypophosphite','Calcivit   PLUS',99.8),(186,'Advanced Chemical Industries Limited','Fluconazole','Canazole    150',22.15),(187,'Advanced Chemical Industries Limited','Fluconazole','Canazole',78.53),(188,'Advanced Chemical Industries Limited','Fluconazole','Canazole    50',8.05),(189,'Advanced Chemical Industries Limited','Chondroitin + Glucosamine','Cartilex',8.05),(190,'Advanced Chemical Industries Limited','Carbocisteine','Castin',25),(191,'Advanced Chemical Industries Limited','Carbocisteine','Castin',2.52),(192,'Advanced Chemical Industries Limited','Cefadroxil','Cedril    500',12.09),(193,'Advanced Chemical Industries Limited','Cefadroxil','Cedril',70.47),(194,'Advanced Chemical Industries Limited','Cefpodoxime','Cefdox    200',37),(195,'Advanced Chemical Industries Limited','Cefixime','Cefim-3    200',45),(196,'Advanced Chemical Industries Limited','Cefixime','Cefim',130.39),(197,'Advanced Chemical Industries Limited','Aceclofenac','Celofen',4.01),(198,'Advanced Chemical Industries Limited','Carbamazepine','Ceplep    200',4.03),(199,'Advanced Chemical Industries Limited','Cefpodoxime','Cefdox',98.67),(200,'Advanced Chemical Industries Limited','Cefpodoxime','Cefdox    100',22.13),(201,'Advanced Chemical Industries Limited','Cefuroxime','Cerox A',199.35),(202,'Advanced Chemical Industries Limited','Cefuroxime','Cerox A   250',25.17),(203,'Advanced Chemical Industries Limited','Cefuroxime','Cerox A   125',15.11),(204,'Advanced Chemical Industries Limited','Sertraline','Chear    25',3.02),(205,'Advanced Chemical Industries Limited','Sertraline','Chear    50',6.04),(206,'Advanced Chemical Industries Limited','Clopidogrel','Clorel    75',12.04),(207,'Advanced Chemical Industries Limited','Aspirin + Clopidogrel','Clorel A',12.04),(208,'Advanced Chemical Industries Limited','Clobetasol Propionate','Clovate',58.17),(209,'Advanced Chemical Industries Limited','Clobetasol Propionate','Clovate',68.2),(210,'Advanced Chemical Industries Limited','Clotrimazole','Dermasim',68.2),(211,'Advanced Chemical Industries Limited','Clotrimazole','Dermasim  VT  500',60.41),(212,'Advanced Chemical Industries Limited','Clotrimazole','Dermasim 1%',35.21),(213,'Advanced Chemical Industries Limited','Desloratadine','Deslorin',2.51),(214,'Advanced Chemical Industries Limited','Desloratadine','Deslorin',25.17),(215,'Advanced Chemical Industries Limited','Dexamethasone Sodium Phosphate','Dexacor',22),(216,'Advanced Chemical Industries Limited','Dexamethasone','Dexcor',1.1),(217,'Advanced Chemical Industries Limited','Drotaverine Hydrochloride','Drovin    40',1.77),(218,'Advanced Chemical Industries Limited','Drotaverine Hydrochloride','Drovin',10.07),(219,'Advanced Chemical Industries Limited','Econazole Nitrate','Ecoren  VT  150',24.16),(220,'Advanced Chemical Industries Limited','Econazole Nitrate','Ecoren',30.2),(221,'Advanced Chemical Industries Limited','Econazole Nitrate + Triamcinolone Acetonide','Ecoren T',43),(222,'Advanced Chemical Industries Limited','Erythromycin','Erythin',60.41),(223,'Advanced Chemical Industries Limited','Erythromycin','Erythin',61.47),(224,'Advanced Chemical Industries Limited','Esomeprazole','Esomep    40',8.02),(225,'Advanced Chemical Industries Limited','Esomeprazole','Esomep    20',5.02),(226,'Advanced Chemical Industries Limited','Levamisole','Etrax',24.07),(227,'Advanced Chemical Industries Limited','Levamisole','Etrax',1),(228,'Advanced Chemical Industries Limited','Ciprofloxacin','Floxabid',40.12),(229,'Advanced Chemical Industries Limited','Linezolid','Ezolid    600',85),(230,'Advanced Chemical Industries Limited','Linezolid','Ezolid    400',60),(231,'Advanced Chemical Industries Limited','Ferrous Sulphate + Folic Acid + Zinc','Femizin  TR',2.92),(232,'Advanced Chemical Industries Limited','Ferrous Gluconate','Feridex',32.22),(233,'Advanced Chemical Industries Limited','Ferrous Sulphate + Folic Acid','Feridex  TR',2.35),(234,'Advanced Chemical Industries Limited','Ibuprofen','Flamex',45),(235,'Advanced Chemical Industries Limited','Ibuprofen','Flamex    400',1.43),(236,'Advanced Chemical Industries Limited','Ciprofloxacin','Floxabid    250',8.56),(237,'Advanced Chemical Industries Limited','Ciprofloxacin','Floxabid    500',15.05),(238,'Advanced Chemical Industries Limited','Ciprofloxacin','Floxabid    750',18.12),(239,'Advanced Chemical Industries Limited','Flucloxacillin','Fluclox    250',8),(240,'Advanced Chemical Industries Limited','Flucloxacillin','Fluclox    500',14),(241,'Advanced Chemical Industries Limited','Flucloxacillin','Fluclox',80),(242,'Advanced Chemical Industries Limited','Flucloxacillin','Fluclox  DS',61.5),(243,'Advanced Chemical Industries Limited','Flucloxacillin','Fluclox    500',55),(244,'Advanced Chemical Industries Limited','Flucloxacillin','Fluclox    250',45),(245,'Advanced Chemical Industries Limited','Flunarizine','Fluver    10',7),(246,'Advanced Chemical Industries Limited','Flunarizine','Fluver    5',3.51),(247,'Advanced Chemical Industries Limited','Griseofulvin','Fulcinex',23.49),(248,'Advanced Chemical Industries Limited','Griseofulvin','Fulcinex',5.6),(249,'Advanced Chemical Industries Limited','Glimepiride','Glimirid    1',6),(250,'Advanced Chemical Industries Limited','Glimepiride','Glimirid    2',9),(251,'Advanced Chemical Industries Limited','Halothane','Halosin',1504),(252,'Advanced Chemical Industries Limited','Chlorhexidine Gluconate','Hexiscrub',300),(253,'Advanced Chemical Industries Limited','Chlorhexidine Gluconate','Hexisol',55),(254,'Advanced Chemical Industries Limited','Chlorhexidine Gluconate','Hexitane',90),(255,'Advanced Chemical Industries Limited','Hydrocortisone','Hison',50.34),(256,'Advanced Chemical Industries Limited','Zopiclone','Hypnoclone',4.03),(257,'Advanced Chemical Industries Limited','Chloramphenicol','Icol   E/E',34.5),(258,'Advanced Chemical Industries Limited','Sodium Cromoglycate','Icrom',65.45),(259,'Advanced Chemical Industries Limited','Gentamicin','Igen',32.12),(260,'Advanced Chemical Industries Limited','Doxycycline','Impedox    100',2.17),(261,'Advanced Chemical Industries Limited','Propranolol Hydrochloride','Indever    40',1.5),(262,'Advanced Chemical Industries Limited','Irbesartan','Isart    150',9.06),(263,'Advanced Chemical Industries Limited','Amikacin','Kacin    100',16.11),(264,'Advanced Chemical Industries Limited','Amikacin','Kacin    500',48.32),(265,'Advanced Chemical Industries Limited','Amiloride Hydrochloride + Hydrochlorothiazide','Kaltide',2.02),(266,'Advanced Chemical Industries Limited','Carvedilol','Karvedil    25',8.05),(267,'Advanced Chemical Industries Limited','Carvedilol','Karvedil    12.5',3),(268,'Advanced Chemical Industries Limited','Carvedilol','Karvedil    6.25',3.02),(269,'Advanced Chemical Industries Limited','Ketorolac Tromethamine','Minolac    30 mg',55.38),(270,'Advanced Chemical Industries Limited','Ketorolac Tromethamine','Minolac    10',10.07),(271,'Advanced Chemical Industries Limited','Ketoprofen','Ketron    50',3.52),(272,'Advanced Chemical Industries Limited','Ketoprofen','Ketron  SR  100',7.05),(273,'Advanced Chemical Industries Limited','Ketoprofen','Ketron  SR  200',10.07),(274,'Advanced Chemical Industries Limited','Lansoprazole','Lanz    15',3.03),(275,'Advanced Chemical Industries Limited','Lansoprazole','Lanz    30',5.04),(276,'Advanced Chemical Industries Limited','Levofloxacin','Leflox    500',15.11),(277,'Advanced Chemical Industries Limited','Gemfibrozil','Lipigem',7.05),(278,'Advanced Chemical Industries Limited','Losartan Potassium','Rosatan    50',5),(279,'Advanced Chemical Industries Limited','Gliclazide','Lozide    80',7.02),(280,'Advanced Chemical Industries Limited','Mefloquine','Meflon',39.61),(281,'Advanced Chemical Industries Limited','Metformin Hydrochloride','Metform    850',6),(282,'Advanced Chemical Industries Limited','Metformin Hydrochloride','Metform    500',4),(283,'Advanced Chemical Industries Limited','Miconazole','Miconex',35.24),(284,'Advanced Chemical Industries Limited','Miconazole','Micoral',60.18),(285,'Advanced Chemical Industries Limited','Hydrocortisone + Miconazole Nitrate','Micosone',40.27),(286,'Advanced Chemical Industries Limited','Hydrocortisone + Miconazole Nitrate','Micosone',40.27),(287,'Advanced Chemical Industries Limited','Diclofenac Sodium','Mobiefn    12.5',9),(288,'Advanced Chemical Industries Limited','Diclofenac Sodium','Mobifen',75.51),(289,'Advanced Chemical Industries Limited','Diclofenac Sodium','Mobifen    50',15),(290,'Advanced Chemical Industries Limited','Diclofenac + Lidocaine Hydrochloride','Mobifen   PLUS',9.57),(291,'Advanced Chemical Industries Limited','Isosorbide Mononitrate','Moniten    20',1.42),(292,'Advanced Chemical Industries Limited','Elemental Iron + Folic Acid + Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1 + Vitamin C + Zinc','Mylovit Z',3.52),(293,'Advanced Chemical Industries Limited','Ambroxol','Myrox S',30.09),(294,'Advanced Chemical Industries Limited','Ambroxol','Myrox',40.12),(295,'Advanced Chemical Industries Limited','Bacitracin Zinc + Neomycin Sulphate','Neocitrin',10.37),(296,'Advanced Chemical Industries Limited','Famotidine','Novatac    20',2.27),(297,'Advanced Chemical Industries Limited','Famotidine','Novatac    40',4.13),(298,'Advanced Chemical Industries Limited','Vitamin E','Nutrivit E   200',3.82),(299,'Advanced Chemical Industries Limited','Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1','Nutrivit-B',35.65),(300,'Advanced Chemical Industries Limited','Vitamin C','Nutrivit C   250',1.9),(301,'Advanced Chemical Industries Limited','Nystatin','Nyscan',20.44),(302,'Advanced Chemical Industries Limited','Azithromycin','Odazyth    250',25.08),(303,'Advanced Chemical Industries Limited','Azithromycin','Odazyth',140),(304,'Advanced Chemical Industries Limited','Azithromycin','Odazyth    500',40),(305,'Advanced Chemical Industries Limited','Lomefloxacin','Omeflox    400',15.11),(306,'Advanced Chemical Industries Limited','Zinc','Oral Z   10mg/5ml',26.18),(307,'Advanced Chemical Industries Limited','Chlorhexidine Gluconate','Oralon',50.15),(308,'Advanced Chemical Industries Limited','Chlorhexidine Gluconate','Oralon',55.17),(309,'Advanced Chemical Industries Limited','Ondansetron','Osetron    8',10.07),(310,'Advanced Chemical Industries Limited','Pantoprazole','Pantex    20',5.7),(311,'Advanced Chemical Industries Limited','Pantoprazole','Pantex 40 mg',8),(312,'Advanced Chemical Industries Limited','Permethrin','Permisol    5%',30.09),(313,'Advanced Chemical Industries Limited','Iron Hydroxide Polymaltose','Polyron',40.12),(314,'Advanced Chemical Industries Limited','Ketotifen','Prosma    1',3),(315,'Advanced Chemical Industries Limited','Ketotifen','Prosma    0.025%',100),(316,'Advanced Chemical Industries Limited','Ketotifen','Prosma',65),(317,'Advanced Chemical Industries Limited','Simvastatin','Recol    10',10.07),(318,'Advanced Chemical Industries Limited','Lactulose','Relacsn',65.45),(319,'Advanced Chemical Industries Limited','Montelukast','Reversair    10',16),(320,'Advanced Chemical Industries Limited','Ascorbic Acid + Copper + Manganese + Selenium + Vitamin A + Vitamin E + Vitamin K + Zinc','Revigor',5.04),(321,'Advanced Chemical Industries Limited','Hydrochlorothiazide + Losartan Potassium','Rosatan H   12.5/50',6.04),(322,'Advanced Chemical Industries Limited','Hydrochlorothiazide + Losartan Potassium','Rosatan H   25/12.5',4.03),(323,'Advanced Chemical Industries Limited','Cetrimide + Chlorhexidine Gluconate','Savlon',80),(324,'Advanced Chemical Industries Limited','Cetrimide + Chlorhexidine Hydrochloride','Savlon',50),(325,'Advanced Chemical Industries Limited','Clobazam','Sedzam    10',2.82),(326,'Advanced Chemical Industries Limited','Secnidazole','Sezol  DS',17.05),(327,'Advanced Chemical Industries Limited','Simethicone','Simet',30.2),(328,'Advanced Chemical Industries Limited','Albendazole','Sintel    400',5.02),(329,'Advanced Chemical Industries Limited','Terbinafine','Skinabin',35),(330,'Advanced Chemical Industries Limited','Fluocinolone + Neomycin Sulphate','Skinalar N',40.12),(331,'Advanced Chemical Industries Limited','Fluocinolone + Neomycin Sulphate','Skinalar N',40.12),(332,'Advanced Chemical Industries Limited','Lisinopril','Stril    5',3.02),(333,'Advanced Chemical Industries Limited','Betacarotene + Vitamin C + Vitamin E','Tasti',2.52),(334,'Advanced Chemical Industries Limited','Amlodipine + Atenolol','Tenocab    5/25',5.27),(335,'Advanced Chemical Industries Limited','Amlodipine + Atenolol','Tenocab    5/50',6.02),(336,'Advanced Chemical Industries Limited','Atenolol','Tenoren    100',1.38),(337,'Advanced Chemical Industries Limited','Domperidone','Vave',35.11),(338,'Advanced Chemical Industries Limited','Domperidone','Vave',25.08),(339,'Advanced Chemical Industries Limited','Water For Injection','Water For Injection',7),(340,'Advanced Chemical Industries Limited','Paracetamol','Xcel    250',5),(341,'Advanced Chemical Industries Limited','Paracetamol','Xcel',12.35),(342,'Advanced Chemical Industries Limited','Paracetamol','Xcel    125',4),(343,'Advanced Chemical Industries Limited','Omeprazole','Xeldrin    10',2.02),(344,'Advanced Chemical Industries Limited','Omeprazole','Xeldrin    40',8.02),(345,'Advanced Chemical Industries Limited','Olanzapine','Xytrex    10',4.53),(346,'Advanced Chemical Industries Limited','Bromazepam','Zepam',5.02),(347,'Advanced Chemical Industries Limited','Ceftazidime','Zitum    250 mg',70.47),(348,'Advanced Chemical Industries Limited','Hydrocortisone','Zocort    1%',38.25),(349,'Advanced Chemical Industries Limited','Calcium Carbonate + Calcium Lactate Gluconate + Vitamin C','Acical-C',11.03),(350,'Advanced Chemical Industries Limited','Tiemonium Methylsulphate','Tynium',8),(351,'Advanced Chemical Industries Limited','Elemental Iron + Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1 + Zinc','Livita',50.34),(352,'Advanced Chemical Industries Limited','Chlorpheniramine Maleate + Dexamethasone','Denicol',68),(353,'Advanced Chemical Industries Limited','Tramadol Hydrochloride','Tendia Injection',20.13),(354,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IM  250 mg',100.3),(355,'Advanced Chemical Industries Limited','Boron + Calcium + Copper + Magnesium + Manganese + Vitamin D + Zinc','Acical M',8),(356,'Advanced Chemical Industries Limited','Naproxen','Anaflex  SR  500',14.09),(357,'Advanced Chemical Industries Limited','Chloroquine Phosphate','Avloquin',14.98),(358,'Advanced Chemical Industries Limited','Dicycloverine Hydrochloride','Diverin 10',2.02),(359,'Advanced Chemical Industries Limited','Dicycloverine Hydrochloride','Diverin',30.2),(360,'Advanced Chemical Industries Limited','Rabeprazole Sodium','Paricel    20',6),(361,'Advanced Chemical Industries Limited','Fluocinolone Acetonide','Skinalar',38.12),(362,'Advanced Chemical Industries Limited','Fluocinolone Acetonide','Skinalar',38.12),(363,'Advanced Chemical Industries Limited','Tizanidine','Tizadin',5.04),(364,'Advanced Chemical Industries Limited','Tretinoin','Trena',45.31),(365,'Advanced Chemical Industries Limited','Ciprofloxacin','Floxabid',100.3),(366,'Advanced Chemical Industries Limited','Lamotrigine','Lamitrin',22),(367,'Advanced Chemical Industries Limited','Atorvastatin','Atasin    10',10.07),(368,'Advanced Chemical Industries Limited','Monosulfiram','Tetrasol',80),(369,'Advanced Chemical Industries Limited','Domperidone','Vave',4),(370,'Advanced Chemical Industries Limited','Losartan Potassium','Rosatan    25',3),(371,'Advanced Chemical Industries Limited','Erythromycin','Erythin    500',8.06),(372,'Advanced Chemical Industries Limited','Clobetasol Propionate + Neomycin Sulphate + Nystatin','Clovate N',65.2),(373,'Advanced Chemical Industries Limited','Inositol','Inosit    500',5.04),(374,'Advanced Chemical Industries Limited','Inositol','Inosit    750',7.55),(375,'Advanced Chemical Industries Limited','Terbinafine','Skinabin    250',40.27),(376,'Advanced Chemical Industries Limited','Cyanocobalamin + Pyridoxine Hydrochloride + Vitamin B1','Povital',12),(377,'Advanced Chemical Industries Limited','Prednisolone','Solone    5',1.72),(378,'Advanced Chemical Industries Limited','Prednisolone','Solone    20',6.27),(379,'Advanced Chemical Industries Limited','Tramadol Hydrochloride','Tendia 50 mg',7.55),(380,'Advanced Chemical Industries Limited','Atenolol + Chlorthalidone','Tenoren   PLUS 50/25',3.02),(381,'Advanced Chemical Industries Limited','Oxaprozin','Demarin',7.05),(382,'Advanced Chemical Industries Limited','Raloxifene Hydrochloride','Rolage',10.07),(383,'Advanced Chemical Industries Limited','Metformin Hydrochloride','Metform  ER  1 gm',9),(384,'Advanced Chemical Industries Limited','Thiopentone Sodium','Thiopen  IV  1 gm',100.68),(385,'Advanced Chemical Industries Limited','Nitazoxanide','Diar    500',10.07),(386,'Advanced Chemical Industries Limited','Lercanidipine Hydrochloride','Canider',5.04),(387,'Advanced Chemical Industries Limited','Etoricoxib','Coxia    90',12.09),(388,'Advanced Chemical Industries Limited','Etoricoxib','Coxia    60',7.05),(389,'Advanced Chemical Industries Limited','Olmesartan Medoxomil','Abetis    20',11),(390,'Advanced Chemical Industries Limited','Leflunomide','Motoral    20',5.04),(391,'Advanced Chemical Industries Limited','Leflunomide','Motoral    100',20.13),(392,'Advanced Chemical Industries Limited','Meclizine Hydrochloride + Pyridoxine Hydrochloride','Pyrimac',2.5),(393,'Advanced Chemical Industries Limited','Diclofenac Sodium','Mobifen  SR  100',3.02),(394,'Advanced Chemical Industries Limited','Thiopentone Sodium','Thiopen  IV  0.5 gm',70.05),(395,'Advanced Chemical Industries Limited','Etoricoxib','Coxia    120',14.09),(396,'Advanced Chemical Industries Limited','Olmesartan Medoxomil','Abetis    40',18),(397,'Advanced Chemical Industries Limited','Cyanocobalamin + Toldimfos Sodium','Acitol B12',50.34),(398,'Advanced Chemical Industries Limited','Butaphosphan + Cyanocobalamin','Catopan  VET',50.34),(399,'Advanced Chemical Industries Limited','Indapamide','Natrilex  SR +',15),(400,'Advanced Chemical Industries Limited','Salbutamol','Brodil  SR',2.42),(401,'Advanced Chemical Industries Limited','Alendronic Acid + Vitamin D3','Alen D',25.17),(402,'Advanced Chemical Industries Limited','Ceftriaxone','Aciphin  IV  2 gm',302.04),(403,'Advanced Chemical Industries Limited','Ondansetron','Osetron    8',25.17),(404,'Advanced Chemical Industries Limited','Cefepime','Pime-4    2g',1107.47),(405,'Advanced Chemical Industries Limited','Paracetamol','Xcel   DISPERSABLE',1.28),(406,'Advanced Chemical Industries Limited','Moxifloxacin','Maxiflox',100.68),(407,'Advanced Chemical Industries Limited','Lomefloxacin','Omeflox',68),(408,'Advanced Chemical Industries Limited','Metformin Hydrochloride + Pioglitazone','Politor    850',11.03),(409,'Advanced Chemical Industries Limited','Metformin Hydrochloride + Pioglitazone','Politor    500',10.03),(410,'Advanced Chemical Industries Limited','Ciprofloxacin','Floxabid  SR  1 gm',20.13),(411,'Advanced Chemical Industries Limited','Ascorbic Acid + Cod Liver Oil + Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin A + Vitamin B1 + Vitamin D3 + Vitamin E','Heptaseas',85),(412,'Advanced Chemical Industries Limited','Clobetasol Propionate + Neomycin Sulphate + Nystatin','Clovate N',70.21),(413,'Advanced Chemical Industries Limited','Cefepime','Pime-4    1g',553.73),(414,'Advanced Chemical Industries Limited','Cefepime','Pime-4    500',302.04),(415,'Advanced Chemical Industries Limited','Dicycloverine Hydrochloride','Diverin',3.52),(416,'Advanced Chemical Industries Limited','Clonazepam','Clonium',130.88),(417,'Advanced Chemical Industries Limited','Cilastatin + Imipenem','Iminem',1203.12),(418,'Advanced Chemical Industries Limited','Levosalbutamol','Brodil Levo    1',1.1),(419,'Advanced Chemical Industries Limited','Levosalbutamol','Brodil Levo',30.09),(420,'Advanced Chemical Industries Limited','Dexibuprofen (S Ibuprofen)','Flamex  DX  400',5.04),(421,'Advanced Chemical Industries Limited','Menthol + Methyl Salicylate','Viscon',40.27),(422,'Advanced Chemical Industries Limited','Menthol + Methyl Salicylate','Viscon',60.41),(423,'Advanced Chemical Industries Limited','Albendazole','Sintel',20.06),(424,'Advanced Chemical Industries Limited','Hydroxyzine Hydrochloride','Artica',40.27),(425,'Advanced Chemical Industries Limited','Clonazepam','Clonium    0.5',7),(426,'Advanced Chemical Industries Limited','Clonazepam','Clonium    2',10),(427,'Advanced Chemical Industries Limited','Nitazoxanide','Diar',35.24),(428,'Advanced Chemical Industries Limited','Cefpodoxime','Cefdox  DS',176.19),(429,'Advanced Chemical Industries Limited','Cefpodoxime','Cefdox',60.41),(430,'Advanced Chemical Industries Limited','Ivermectin','Veratin    6',10),(431,'Advanced Chemical Industries Limited','Bisoprolol Hemifumarate','Probis 5',10.03),(432,'Advanced Chemical Industries Limited','Valacyclovir','Alaclov',40.27),(433,'Advanced Chemical Industries Limited','Amlodipine + Benazepril Hydrochloride','Cacetor    5/10',6.04),(434,'Advanced Chemical Industries Limited','Amlodipine + Benazepril Hydrochloride','Cacetor    2.5/10',4.03),(435,'Advanced Chemical Industries Limited','Amlodipine + Benazepril Hydrochloride','Cacetor    5/20',8.05),(436,'Advanced Chemical Industries Limited','Levofloxacin','Leflox    750',20.13),(437,'Advanced Chemical Industries Limited','Nebivolol','Bipinor    2.5',5.04),(438,'Advanced Chemical Industries Limited','Cefuroxime','Cerox A   500',45.31),(439,'Advanced Chemical Industries Limited','Tiemonium Methylsulphate','Tynium',20),(440,'Advanced Chemical Industries Limited','Voriconazole','Progil    50',50),(441,'Advanced Chemical Industries Limited','Mecobalamin','Phyton',4.03),(442,'Advanced Chemical Industries Limited','Buprenorphine','Acron',10.07),(443,'Advanced Chemical Industries Limited','Omeprazole','Xeldrin    20',4.03),(444,'Advanced Chemical Industries Limited','Cyanocobalamin + Pyridoxine Hydrochloride + Vitamin B1','Povital',25.17),(445,'Advanced Chemical Industries Limited','Cefuroxime','Cerox A   750',126.12),(446,'Advanced Chemical Industries Limited','Cefuroxime','Cerox A   250',55.38),(447,'Advanced Chemical Industries Limited','Tanoxicam','Oxicam    20',8.05),(448,'Advanced Chemical Industries Limited','Piracetam','Memopil',75.51),(449,'Advanced Chemical Industries Limited','Nicotinamide + Pyridoxine Hydrochloride + Riboflavin + Vitamin B1 + Zinc','Oral  ZB',55),(450,'Advanced Chemical Industries Limited','Diosmin + Hesperidin','Daflon    500',12.34),(451,'Advanced Chemical Industries Limited','Hypromellose','Atier    0.3%',65.45),(452,'Advanced Chemical Industries Limited','Calcitriol','Caloren  IV',150),(453,'Advanced Chemical Industries Limited','Hyoscine Butyl Bromide','Colik    20',6.72),(454,'Advanced Chemical Industries Limited','Hyoscine Butyl Bromide','Colik',29.9),(455,'Advanced Chemical Industries Limited','Dobutamine','Dobumin',251.69),(456,'Advanced Chemical Industries Limited','Aminocaproic Acid','Minocap',25.17),(457,'Advanced Chemical Industries Limited','Mecobalamin','Phyton',30.2),(458,'Advanced Chemical Industries Limited','Bupivacaine','Pivacain    0.25%',45.31),(459,'Advanced Chemical Industries Limited','Bupivacaine','Pivacain    0.5%',60.41),(460,'Advanced Chemical Industries Limited','Bupivacaine + Dextrose','Pivacain D',30.2),(461,'Advanced Chemical Industries Limited','Vecuronium Bromide','Vecuron',82.56),(462,'Advanced Chemical Industries Limited','Lidocaine Hydrochloride','Xylone    2%',3.57),(463,'Advanced Chemical Industries Limited','Atorvastatin','Atasin    20',18.12),(464,'Advanced Chemical Industries Limited','Nebivolol','Bipinor',8.05),(465,'Advanced Chemical Industries Limited','Ivermectin','Acimec 1%',115),(466,'Advanced Chemical Industries Limited','Entecavir','Teviral    1',100.3),(467,'Advanced Chemical Industries Limited','Entecavir','Teviral    0.5',48.32),(468,'Advanced Chemical Industries Limited','Azithromycin','Odazyth  IV',251.69),(469,'Advanced Chemical Industries Limited','Omeprazole','Xeldrin  IV  40',80.24),(470,'Advanced Chemical Industries Limited','Mizolastine','Mastel  MR',6.52),(471,'Advanced Chemical Industries Limited','Clotrimazole','Dermasim  VT  200',20.14),(472,'Advanced Chemical Industries Limited','Calcium + Vitamin D3','Acical D',7),(473,'Advanced Chemical Industries Limited','Vitamin C','Nutrivit-C',30.2),(474,'Advanced Chemical Industries Limited','Vitamin C','Nutrivit-C',33.45),(475,'Advanced Chemical Industries Limited','Theophylline','Teolex  CR',3.52),(476,'Advanced Chemical Industries Limited','Theophylline','Teolex',4.25),(477,'Advanced Chemical Industries Limited','Diclofenac Sodium','Mobifen  SR  75',2.52),(478,'Advanced Chemical Industries Limited','Dexketoprofen','Ketron D',4.03),(479,'Advanced Chemical Industries Limited','Montelukast','Reversair    5',8.05),(480,'Advanced Chemical Industries Limited','Montelukast','Reversair    4',7.05),(481,'Advanced Chemical Industries Limited','Hydroxyzine Hydrochloride','Artica 10',1.25),(482,'Advanced Chemical Industries Limited','Hydroxyzine Hydrochloride','Artica 25',45),(483,'Advanced Chemical Industries Limited','Piracetam','Memopil',151.02),(484,'Advanced Chemical Industries Limited','Suxamethonium Chloride','Rapilax',17.25),(485,'Advanced Chemical Industries Limited','Neostigmine Methyl Sulphate','Stignal',6.07),(486,'Advanced Chemical Industries Limited','Dopamine Hydrochloride','Cardopa',45.31),(487,'Advanced Chemical Industries Limited','Meropenem','Aronem    500 mg',654.41),(488,'Advanced Chemical Industries Limited','Meropenem','Aronem    1 gm',1208.14),(489,'Advanced Chemical Industries Limited','Zinc','Dispazinc Dispersible',2.01),(490,'Advanced Chemical Industries Limited','Ascorbic Acid + Copper + Lutein + Vitamin E + Zinc','Tioxil',10),(491,'Advanced Chemical Industries Limited','Pregabalin','Gabarol    75',16.11),(492,'Advanced Chemical Industries Limited','Pregabalin','Gabarol    100',22.15),(493,'Advanced Chemical Industries Limited','Pregabalin','Gabarol    150',30.2),(494,'Advanced Chemical Industries Limited','Oseltamivir','Pandeflu',151.02),(495,'Advanced Chemical Industries Limited','Nefopam Hydrochloride','Acuten',3.02),(496,'Advanced Chemical Industries Limited','Nefopam Hydrochloride','Acuten',15.11),(497,'Advanced Chemical Industries Limited','Amoxicillin','Acemox    30%',267.25),(498,'Advanced Chemical Industries Limited','Pioglitazone','Diatag F/C   15',8.05);
/*!40000 ALTER TABLE `medicines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checked` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pharmacy`
--

DROP TABLE IF EXISTS `pharmacy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pharmacy` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` bigint DEFAULT NULL,
  `bio` varchar(255) NOT NULL,
  `pharmacy_name` varchar(255) NOT NULL,
  `place` varchar(255) NOT NULL,
  `app_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gmiss7vr18tt7x6mqaxqof5vx` (`app_user_id`),
  CONSTRAINT `FKs8ub2mlb75cc07t5l8xibqiwk` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pharmacy`
--

LOCK TABLES `pharmacy` WRITE;
/*!40000 ALTER TABLE `pharmacy` DISABLE KEYS */;
INSERT INTO `pharmacy` VALUES (1,0,'','Brothers Pharmacy Ltd','dhaka',4);
/*!40000 ALTER TABLE `pharmacy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relation_doctor_offline_time`
--

DROP TABLE IF EXISTS `relation_doctor_offline_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relation_doctor_offline_time` (
  `doctor_id` bigint NOT NULL,
  `time_id` bigint NOT NULL,
  KEY `FKqw4uuqpfqerpax1fvot996tif` (`time_id`),
  KEY `FKese87cjcnpm0tyt1pn0mb9r0m` (`doctor_id`),
  CONSTRAINT `FKese87cjcnpm0tyt1pn0mb9r0m` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`),
  CONSTRAINT `FKqw4uuqpfqerpax1fvot996tif` FOREIGN KEY (`time_id`) REFERENCES `doctor_available_time` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relation_doctor_offline_time`
--

LOCK TABLES `relation_doctor_offline_time` WRITE;
/*!40000 ALTER TABLE `relation_doctor_offline_time` DISABLE KEYS */;
INSERT INTO `relation_doctor_offline_time` VALUES (1,37);
/*!40000 ALTER TABLE `relation_doctor_offline_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relation_doctor_online_time`
--

DROP TABLE IF EXISTS `relation_doctor_online_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relation_doctor_online_time` (
  `doctor_id` bigint NOT NULL,
  `online_time_id` bigint NOT NULL,
  KEY `FK1rrw8tdv501ttd1b5jsxassm` (`online_time_id`),
  KEY `FKqy2jiygkofpboi79m85x7fuyp` (`doctor_id`),
  CONSTRAINT `FK1rrw8tdv501ttd1b5jsxassm` FOREIGN KEY (`online_time_id`) REFERENCES `doctor_online_available_time` (`id`),
  CONSTRAINT `FKqy2jiygkofpboi79m85x7fuyp` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relation_doctor_online_time`
--

LOCK TABLES `relation_doctor_online_time` WRITE;
/*!40000 ALTER TABLE `relation_doctor_online_time` DISABLE KEYS */;
INSERT INTO `relation_doctor_online_time` VALUES (1,9),(1,10);
/*!40000 ALTER TABLE `relation_doctor_online_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `review` varchar(255) DEFAULT NULL,
  `star_count` bigint DEFAULT NULL,
  `reviewer_id` bigint DEFAULT NULL,
  `subject_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKotti80v49ycwevibigyoxj89s` (`reviewer_id`),
  KEY `FK7556v05r23tb1eop2x2rweubj` (`subject_id`),
  CONSTRAINT `FK7556v05r23tb1eop2x2rweubj` FOREIGN KEY (`subject_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKotti80v49ycwevibigyoxj89s` FOREIGN KEY (`reviewer_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'2023-10-02','Good Doctor',5,1,2),(2,'2023-10-02','Nice Doctor',4,1,2),(3,'2023-10-02','Very Nice',4,1,2);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'USER'),(2,'DOCTOR'),(3,'HOSPITAL'),(4,'PHARMACY'),(5,'AMBULANCE'),(6,'ADMIN'),(7,'USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `two_step_codes`
--

DROP TABLE IF EXISTS `two_step_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `two_step_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `two_step_codes`
--

LOCK TABLES `two_step_codes` WRITE;
/*!40000 ALTER TABLE `two_step_codes` DISABLE KEYS */;
INSERT INTO `two_step_codes` VALUES (1,'429237','almahmudaraf@gmail.com');
/*!40000 ALTER TABLE `two_step_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_count_stats`
--

DROP TABLE IF EXISTS `user_count_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_count_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `count` bigint DEFAULT NULL,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_count_stats`
--

LOCK TABLES `user_count_stats` WRITE;
/*!40000 ALTER TABLE `user_count_stats` DISABLE KEYS */;
INSERT INTO `user_count_stats` VALUES (1,6,'2023-10-02'),(2,1,'2023-10-03');
/*!40000 ALTER TABLE `user_count_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_responses`
--

DROP TABLE IF EXISTS `user_responses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_responses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checked` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_responses`
--

LOCK TABLES `user_responses` WRITE;
/*!40000 ALTER TABLE `user_responses` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_responses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  KEY `FK6fql8djp64yp4q9b3qeyhr82b` (`user_id`),
  CONSTRAINT `FK6fql8djp64yp4q9b3qeyhr82b` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-10-25 13:17:46
