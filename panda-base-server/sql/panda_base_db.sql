-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pandabasedb
-- ------------------------------------------------------
-- Server version	8.0.35

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
-- Table structure for table `customer_service`
--

DROP TABLE IF EXISTS `customer_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_service` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) NOT NULL COMMENT '会话标识',
  `question_type` tinyint(1) NOT NULL COMMENT '问题类型 1-售后 2-发票 3-公益证书 4-其他',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0-进行中 1-已关闭',
  `last_message_time` datetime NOT NULL COMMENT '最后消息时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session` (`session_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_message_time` (`last_message_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_service`
--

LOCK TABLES `customer_service` WRITE;
/*!40000 ALTER TABLE `customer_service` DISABLE KEYS */;
INSERT INTO `customer_service` VALUES (1,1,'SESSION202401051001',1,1,'2024-01-05 11:00:00','2024-01-05 10:30:00','2024-01-05 11:00:00'),(2,2,'SESSION202401051002',2,0,'2024-01-05 11:30:00','2024-01-05 11:00:00','2024-01-05 11:30:00');
/*!40000 ALTER TABLE `customer_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `export_record`
--

DROP TABLE IF EXISTS `export_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `export_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `export_type` tinyint(1) NOT NULL COMMENT '导出类型 1-财务营收 2-待发货清单 3-会员卡清单 4-兑换码状态',
  `file_name` varchar(200) NOT NULL COMMENT '文件名',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `query_params` text COMMENT '查询参数JSON',
  `export_user` varchar(50) DEFAULT NULL COMMENT '导出操作人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`export_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据导出记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `export_record`
--

LOCK TABLES `export_record` WRITE;
/*!40000 ALTER TABLE `export_record` DISABLE KEYS */;
INSERT INTO `export_record` VALUES (1,1,'财务营收报表_20240105.xlsx','https://export.file/finance_20240105.xlsx','{\"startDate\":\"2024-01-01\",\"endDate\":\"2024-01-05\"}','管理员','2024-01-05 15:00:00'),(2,2,'待发货清单_20240105.xlsx','https://export.file/shipping_20240105.xlsx','{\"status\":0}','管理员','2024-01-05 16:00:00');
/*!40000 ALTER TABLE `export_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_card`
--

DROP TABLE IF EXISTS `membership_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_card` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员卡ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `card_number` varchar(64) NOT NULL COMMENT '会员卡号',
  `card_type` tinyint(1) NOT NULL COMMENT '卡类型 1-年卡 2-月卡 3-次票',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0-未激活 1-已激活 2-已过期 3-已作废',
  `total_count` int DEFAULT NULL COMMENT '总次数（次票专用）',
  `used_count` int NOT NULL DEFAULT '0' COMMENT '已使用次数',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `order_id` bigint DEFAULT NULL COMMENT '来源订单ID（购买方式）',
  `redemption_record_id` bigint DEFAULT NULL COMMENT '来源兑换记录ID（兑换方式）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_card_number` (`card_number`),
  KEY `idx_user` (`user_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_order` (`order_id`),
  KEY `idx_redemption` (`redemption_record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员卡表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_card`
--

LOCK TABLES `membership_card` WRITE;
/*!40000 ALTER TABLE `membership_card` DISABLE KEYS */;
INSERT INTO `membership_card` VALUES (1,1,1,'CARD202401010001',1,1,NULL,0,'2024-01-01 00:00:00','2025-01-01 00:00:00',1,'2024-01-01 10:05:00','2024-01-01 10:05:00'),(2,2,2,'CARD202401020001',2,1,NULL,0,'2024-01-02 00:00:00','2024-02-02 00:00:00',2,'2024-01-02 11:05:00','2024-01-02 11:05:00'),(3,3,3,'CARD202401030001',3,1,10,3,'2024-01-03 00:00:00','2024-07-03 00:00:00',3,'2024-01-03 12:05:00','2024-01-10 15:00:00');
/*!40000 ALTER TABLE `membership_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单商品明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,1,1,'年卡会员','https://product.img/year.jpg',299.00,1,299.00,'2024-01-01 10:00:00'),(2,2,2,'月卡会员','https://product.img/month.jpg',39.90,1,39.90,'2024-01-02 11:00:00'),(3,3,3,'次票(10次)','https://product.img/times.jpg',199.00,1,199.00,'2024-01-03 12:00:00'),(4,4,5,'会员+玩偶套餐','https://product.img/combo.jpg',359.00,1,359.00,'2024-01-04 13:00:00'),(5,5,4,'熊猫玩偶','https://product.img/toy.jpg',99.00,1,99.00,'2024-01-05 14:00:00');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `image_url` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `type` tinyint(1) NOT NULL COMMENT '商品类型 1-年卡 2-月卡 3-次票 4-实物商品 5-组合商品',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `validity_days` int DEFAULT NULL COMMENT '有效期天数（虚拟票证专用）',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐 0-否 1-是',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_recommend` (`is_recommend`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,1,'年卡会员','享受一年内无限次入园权限','https://product.img/year.jpg',1,299.00,399.00,1000,365,1,1,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(2,1,'月卡会员','享受一月内无限次入园权限','https://product.img/month.jpg',2,39.90,49.90,5000,30,1,1,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(3,1,'次票(10次)','可入园10次的有效票证','https://product.img/times.jpg',3,199.00,249.00,2000,180,0,1,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(4,2,'熊猫玩偶','限量版熊猫毛绒玩偶','https://product.img/toy.jpg',4,99.00,129.00,500,NULL,1,1,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(5,3,'会员+玩偶套餐','年卡会员+熊猫玩偶超值套餐','https://product.img/combo.jpg',5,359.00,528.00,200,NULL,1,1,'2024-01-01 09:00:00','2024-01-01 09:00:00');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `type` tinyint(1) NOT NULL COMMENT '商品类型 1-虚拟票证 2-实物周边',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (1,'虚拟票证',1,1,1,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(2,'实物周边',2,2,1,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(3,'组合商品',2,3,1,'2024-01-01 09:00:00','2024-01-01 09:00:00');
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_combo_detail`
--

DROP TABLE IF EXISTS `product_combo_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_combo_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `combo_product_id` bigint NOT NULL COMMENT '组合商品ID',
  `product_id` bigint NOT NULL COMMENT '子商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_combo` (`combo_product_id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组合商品明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_combo_detail`
--

LOCK TABLES `product_combo_detail` WRITE;
/*!40000 ALTER TABLE `product_combo_detail` DISABLE KEYS */;
INSERT INTO `product_combo_detail` VALUES (1,5,1,1,'2024-01-01 09:00:00'),(2,5,4,1,'2024-01-01 09:00:00');
/*!40000 ALTER TABLE `product_combo_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order`
--

DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态 0-待支付 1-已支付 2-已取消 3-已退款',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `expire_time` datetime NOT NULL COMMENT '订单过期时间（15分钟）',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_pay_time` (`pay_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购买订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order`
--

LOCK TABLES `purchase_order` WRITE;
/*!40000 ALTER TABLE `purchase_order` DISABLE KEYS */;
INSERT INTO `purchase_order` VALUES (1,'ORDER202401010001',1,299.00,299.00,1,'2024-01-01 10:05:00',NULL,NULL,'2024-01-01 10:20:00','WX202401010001','2024-01-01 10:00:00','2024-01-01 10:05:00'),(2,'ORDER202401020001',2,39.90,39.90,1,'2024-01-02 11:05:00',NULL,NULL,'2024-01-02 11:20:00','WX202401020001','2024-01-02 11:00:00','2024-01-02 11:05:00'),(3,'ORDER202401030001',3,199.00,199.00,1,'2024-01-03 12:05:00',NULL,NULL,'2024-01-03 12:20:00','WX202401030001','2024-01-03 12:00:00','2024-01-03 12:05:00'),(4,'ORDER202401040001',1,359.00,359.00,0,NULL,NULL,NULL,'2024-01-04 13:20:00',NULL,'2024-01-04 13:00:00','2024-01-04 13:00:00'),(5,'ORDER202401050001',2,99.00,99.00,2,NULL,'2024-01-05 14:10:00','临时改变主意','2024-01-05 14:20:00',NULL,'2024-01-05 14:00:00','2024-01-05 14:10:00');
/*!40000 ALTER TABLE `purchase_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `redemption_code`
--

DROP TABLE IF EXISTS `redemption_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `redemption_code` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '兑换码ID',
  `code` varchar(64) NOT NULL COMMENT '兑换码（加密存储）',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `batch_no` varchar(32) NOT NULL COMMENT '批次号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0-未使用 1-已使用 2-已过期',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `use_user_id` bigint DEFAULT NULL COMMENT '使用用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_product` (`product_id`),
  KEY `idx_batch` (`batch_no`),
  KEY `idx_status` (`status`),
  KEY `idx_expire` (`expire_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兑换码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redemption_code`
--

LOCK TABLES `redemption_code` WRITE;
/*!40000 ALTER TABLE `redemption_code` DISABLE KEYS */;
INSERT INTO `redemption_code` VALUES (1,'RED20240101001',1,'BATCH20240101',0,'2024-12-31 23:59:59',NULL,NULL,'2024-01-01 09:00:00','2024-01-01 09:00:00'),(2,'RED20240101002',1,'BATCH20240101',1,'2024-12-31 23:59:59','2024-01-05 10:00:00',1,'2024-01-01 09:00:00','2024-01-05 10:00:00'),(3,'RED20240102001',4,'BATCH20240102',0,'2024-12-31 23:59:59',NULL,NULL,'2024-01-02 09:00:00','2024-01-02 09:00:00'),(4,'RED20240102002',4,'BATCH20240102',2,'2024-01-15 23:59:59',NULL,NULL,'2024-01-02 09:00:00','2024-01-16 00:00:00');
/*!40000 ALTER TABLE `redemption_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `redemption_record`
--

DROP TABLE IF EXISTS `redemption_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `redemption_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `record_no` varchar(32) NOT NULL COMMENT '兑换记录编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `redemption_code_id` bigint NOT NULL COMMENT '兑换码ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_type` tinyint(1) NOT NULL COMMENT '商品类型 1-虚拟票证 2-实物商品',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0-兑换中 1-已完成 2-已发货（实物）',
  `shipping_address` text COMMENT '收货地址JSON',
  `tracking_number` varchar(100) DEFAULT NULL COMMENT '物流单号',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_code` (`redemption_code_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兑换记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redemption_record`
--

LOCK TABLES `redemption_record` WRITE;
/*!40000 ALTER TABLE `redemption_record` DISABLE KEYS */;
INSERT INTO `redemption_record` VALUES (1,'REDREC20240105001',1,2,1,1,1,NULL,NULL,NULL,'2024-01-05 10:30:00','2024-01-05 10:00:00','2024-01-05 10:30:00');
/*!40000 ALTER TABLE `redemption_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(64) NOT NULL COMMENT '微信openid',
  `unionid` varchar(64) DEFAULT NULL COMMENT '微信unionid',
  `account` varchar(20) DEFAULT NULL COMMENT '账号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '用户头像URL',
  `role` tinyint(1) NOT NULL COMMENT '用户角色 1-普通用户 2-管理员 3-超级管理员',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否匿名用户 0-否 1-是',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_unionid` (`unionid`),
  KEY `idx_phone` (`phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'oX8aV5J7jJ9KlP0m3','oX8aV5J7jJ9KlP0m3','100001','6fafed677bef3ab89e9fd905ff48f0ce','用户张三','https://avatar.url/1.jpg',1,'13800138001',0,1,'2024-01-01 10:00:00','2025-12-10 00:36:24'),(2,'oX8aV5J7jJ9KlP0m4','oX8aV5J7jJ9KlP0m4','100002','6fafed677bef3ab89e9fd905ff48f0ce','用户李四','https://avatar.url/2.jpg',1,'13800138002',0,1,'2024-01-02 11:00:00','2025-12-10 00:36:24'),(3,'oX8aV5J7jJ9KlP0m5','oX8aV5J7jJ9KlP0m5','100003','6fafed677bef3ab89e9fd905ff48f0ce','用户王五','https://avatar.url/3.jpg',1,'13800138003',0,1,'2024-01-03 12:00:00','2025-12-10 00:36:24'),(4,'oX8aV5J7jJ9KlP0m6','oX8aV5J7jJ9KlP0m6','100004','6fafed677bef3ab89e9fd905ff48f0ce','用户赵六','https://avatar.url/4.jpg',2,'13800138004',0,1,'2024-01-04 13:00:00','2025-12-10 00:36:24'),(6,'oX8aV5J7jJ9KlP0m8','oX8aV5J7jJ9KlP0m8','admin','6fafed677bef3ab89e9fd905ff48f0ce','管理员','https://rich-tams.oss-cn-beijing.aliyuncs.com/RichInterview/691e5f5fe4b081baa87c2741.jpg',3,'18888888888',0,1,'2025-12-10 00:26:00','2025-12-10 00:36:24');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `province` varchar(50) NOT NULL COMMENT '省',
  `city` varchar(50) NOT NULL COMMENT '市',
  `district` varchar(50) NOT NULL COMMENT '区',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址 0-否 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_default` (`is_default`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,1,'张三','13800138001','北京市','北京市','朝阳区','朝阳门外大街100号',1,'2024-01-01 10:30:00','2024-01-01 10:30:00'),(2,1,'张三公司','13800138001','北京市','北京市','海淀区','中关村软件园200号',0,'2024-01-02 11:30:00','2024-01-02 11:30:00'),(3,2,'李四','13800138002','上海市','上海市','浦东新区','陆家嘴环路300号',1,'2024-01-02 12:00:00','2024-01-02 12:00:00'),(4,3,'王五','13800138003','广州市','广东省','天河区','天河路400号',1,'2024-01-03 13:00:00','2024-01-03 13:00:00'),(5,4,'赵六','13800138004','深圳市','广东省','南山区','科技园路500号',1,'2024-01-04 14:00:00','2024-01-04 14:00:00');
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-10  0:38:07
