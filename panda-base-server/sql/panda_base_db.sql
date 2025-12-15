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
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Banner ID',
  `title` varchar(200) DEFAULT NULL COMMENT 'Banner标题',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `link_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接类型 0-无跳转 1-商品详情 2-页面跳转 3-外部链接',
  `link_value` varchar(500) DEFAULT NULL COMMENT '链接值（商品ID/页面路径/外部URL）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序（数字越小越靠前）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `start_time` datetime DEFAULT NULL COMMENT '生效开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '生效结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort_order`),
  KEY `idx_time` (`start_time`,`end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Banner轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'欢迎来到熊猫基地','https://img.tukuppt.com/png_preview/00/49/30/CNQEgKhT9I.jpg!/fw/780',0,NULL,1,1,'2024-01-01 00:00:00','2025-12-31 23:59:59','2025-12-11 13:36:20','2025-12-11 14:54:04'),(2,'年卡会员','/static/images/年卡VIP2.png',1,'1',2,1,'2024-01-01 00:00:00','2025-12-31 23:59:59','2025-12-11 13:36:20','2025-12-13 18:51:06'),(3,'月卡会员','/static/images/月卡VIP2.png',1,'2',3,1,'2024-01-01 00:00:00','2025-12-31 23:59:59','2025-12-11 13:36:20','2025-12-13 18:51:06');
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

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
  `void_reason` varchar(200) DEFAULT NULL COMMENT '作废原因（退款/违规等）',
  `void_time` datetime DEFAULT NULL COMMENT '作废时间',
  `order_id` bigint DEFAULT NULL COMMENT '来源订单ID',
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
  KEY `idx_redemption` (`redemption_record_id`),
  KEY `idx_user_status_type` (`user_id`,`status`,`card_type`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员卡表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_card`
--

LOCK TABLES `membership_card` WRITE;
/*!40000 ALTER TABLE `membership_card` DISABLE KEYS */;
INSERT INTO `membership_card` VALUES (18,11,1,'CARD17657130138709894',1,1,NULL,0,'2025-12-14 19:50:14','2026-12-14 19:50:14',NULL,NULL,NULL,38,'2025-12-14 19:50:14','2025-12-14 19:50:14');
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
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单商品明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (53,53,5,'年卡+基础蔬菜包*12','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',150.00,1,150.00,'2025-12-14 19:49:58'),(54,54,1,'年卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',99.00,1,99.00,'2025-12-14 19:51:02'),(55,55,1,'年卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',99.00,1,99.00,'2025-12-14 19:54:05'),(56,56,2,'月卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E6%9C%88%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',39.90,1,39.90,'2025-12-14 19:54:17'),(57,57,1,'年卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',99.00,1,99.00,'2025-12-14 19:55:57'),(58,58,1,'年卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',0.01,1,0.01,'2025-12-15 13:58:15'),(59,59,1,'年卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',0.01,1,0.01,'2025-12-15 14:14:05'),(60,60,1,'年卡会员','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',0.01,1,0.01,'2025-12-15 15:00:55');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_record`
--

DROP TABLE IF EXISTS `payment_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `pay_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付方式 1-微信支付',
  `pay_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '支付状态 0-待支付 1-支付成功 2-支付失败 3-已关闭',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `prepay_id` varchar(64) DEFAULT NULL COMMENT '微信预支付ID',
  `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `callback_count` int NOT NULL DEFAULT '0' COMMENT '回调次数（用于检测重复回调）',
  `is_callback_processed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '回调是否已处理 0-未处理 1-已处理',
  `callback_content` text COMMENT '回调内容（JSON）',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_record`
--

LOCK TABLES `payment_record` WRITE;
/*!40000 ALTER TABLE `payment_record` DISABLE KEYS */;
INSERT INTO `payment_record` VALUES (1,59,'ORDER1765779244669773C6F',12,0.01,1,0,NULL,'wx15141405894500aed6ff156f0607e30001',NULL,NULL,0,0,NULL,NULL,'2025-12-15 14:14:07','2025-12-15 14:14:07'),(2,60,'ORDER17657820546451200C3',12,0.01,1,0,NULL,'wx15150057165683ad4c7f0851f0baa50001',NULL,NULL,0,0,NULL,NULL,'2025-12-15 15:00:57','2025-12-15 15:00:57');
/*!40000 ALTER TABLE `payment_record` ENABLE KEYS */;
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
INSERT INTO `product` VALUES (1,1,'年卡会员','享受一年内无限次入园权限','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',1,0.01,399.00,99995,365,1,1,'2024-01-01 09:00:00','2025-12-15 13:23:51'),(2,1,'月卡会员','享受一月内无限次入园权限','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E6%9C%88%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',2,0.01,49.90,99999,30,1,1,'2024-01-01 09:00:00','2025-12-15 13:23:51'),(3,1,'次票(10次)','可入园10次的有效票证','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E6%9C%88%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',3,0.01,249.00,99999,NULL,0,1,'2024-01-01 09:00:00','2025-12-15 13:23:51'),(4,2,'基础蔬菜包*12','基础蔬菜包*12','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E9%A3%9F%E6%9D%90%E7%A4%BC%E5%8C%85%E5%95%86%E5%93%81%E5%9B%BE.png',4,0.01,129.00,99999,NULL,0,1,'2024-01-01 09:00:00','2025-12-15 13:23:51'),(5,3,'年卡+基础蔬菜包*12','年卡+基础蔬菜包*12超值套餐','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E5%B9%B4%E5%8D%A1%E5%95%86%E5%93%81%E5%9B%BE.png',5,0.01,528.00,99998,NULL,0,1,'2024-01-01 09:00:00','2025-12-15 13:23:51');
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
  `address_id` bigint DEFAULT NULL COMMENT '收货地址ID',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(11) DEFAULT NULL COMMENT '收货人手机号',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `detail_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态 0-待支付 1-已支付 2-已取消 3-已退款 4-已过期',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `expire_time` datetime NOT NULL COMMENT '订单过期时间（15分钟）',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `refund_no` varchar(64) DEFAULT NULL COMMENT '退款单号',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_pay_time` (`pay_time`),
  KEY `idx_address_id` (`address_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_user_status` (`user_id`,`order_status`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购买订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order`
--

LOCK TABLES `purchase_order` WRITE;
/*!40000 ALTER TABLE `purchase_order` DISABLE KEYS */;
INSERT INTO `purchase_order` VALUES (53,'ORDER176571299781797EAF7',11,15,'张三','18888888888','山西省','阳泉市','郊区','xxxx小区',150.00,150.00,1,'2025-12-14 19:49:59',NULL,NULL,'2025-12-14 20:04:58','WX1765712999492',NULL,NULL,NULL,'2025-12-14 19:49:58','2025-12-14 19:49:59'),(54,'ORDER1765713062034CF490D',11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,99.00,99.00,1,'2025-12-14 19:51:03',NULL,NULL,'2025-12-14 20:06:02','WX1765713063095',NULL,NULL,NULL,'2025-12-14 19:51:02','2025-12-14 19:51:03'),(55,'ORDER1765713245042AC784D',11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,99.00,99.00,1,'2025-12-14 19:54:06',NULL,NULL,'2025-12-14 20:09:05','WX1765713246495',NULL,NULL,NULL,'2025-12-14 19:54:05','2025-12-14 19:54:06'),(56,'ORDER17657132571100F7DEB',11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,39.90,39.90,2,NULL,'2025-12-14 19:54:35','用户主动取消','2025-12-14 20:09:17',NULL,NULL,NULL,NULL,'2025-12-14 19:54:17','2025-12-14 19:54:35'),(57,'ORDER1765713357374B0B49C',11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,99.00,99.00,2,NULL,'2025-12-14 20:15:00','订单已过期，系统自动取消','2025-12-14 20:10:57',NULL,NULL,NULL,NULL,'2025-12-14 19:55:57','2025-12-14 20:15:00'),(58,'ORDER1765778295026EE8110',12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0.01,0.01,1,'2025-12-15 13:58:19',NULL,NULL,'2025-12-15 14:13:15','WX1765778298864',NULL,NULL,NULL,'2025-12-15 13:58:15','2025-12-15 13:58:19'),(59,'ORDER1765779244669773C6F',12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0.01,0.01,4,NULL,'2025-12-15 14:30:00','订单已过期，系统自动取消','2025-12-15 14:29:05',NULL,NULL,NULL,NULL,'2025-12-15 14:14:05','2025-12-15 14:30:00'),(60,'ORDER17657820546451200C3',12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0.01,0.01,0,NULL,NULL,NULL,'2025-12-15 15:15:55',NULL,NULL,NULL,NULL,'2025-12-15 15:00:55','2025-12-15 15:00:55');
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
  `order_id` bigint DEFAULT NULL COMMENT '来源订单ID',
  `batch_no` varchar(32) NOT NULL COMMENT '批次号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0-未使用 1-已使用 2-已过期 3-已作废',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `void_reason` varchar(200) DEFAULT NULL COMMENT '作废原因（退款/管理员操作等）',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `use_user_id` bigint DEFAULT NULL COMMENT '使用用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_product` (`product_id`),
  KEY `idx_batch` (`batch_no`),
  KEY `idx_status` (`status`),
  KEY `idx_expire` (`expire_time`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status_expire` (`status`,`expire_time`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兑换码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redemption_code`
--

LOCK TABLES `redemption_code` WRITE;
/*!40000 ALTER TABLE `redemption_code` DISABLE KEYS */;
INSERT INTO `redemption_code` VALUES (39,'RED17657129994957A325C92',5,NULL,'ORDER53',1,'2026-12-14 19:49:59',NULL,'2025-12-14 19:50:14',11,'2025-12-14 19:49:59','2025-12-14 19:50:14'),(40,'RED176571306309844FBA578',1,NULL,'ORDER54',0,'2026-12-14 19:51:03',NULL,NULL,NULL,'2025-12-14 19:51:03','2025-12-14 19:51:03'),(41,'RED17657132464986CA790C6',1,NULL,'ORDER55',0,'2026-12-14 19:54:06',NULL,NULL,NULL,'2025-12-14 19:54:06','2025-12-14 19:54:06'),(42,'RED1765778298868F30AC536',1,NULL,'ORDER58',0,'2026-12-15 13:58:19',NULL,NULL,NULL,'2025-12-15 13:58:19','2025-12-15 13:58:19');
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
  `product_type` tinyint(1) NOT NULL COMMENT '商品类型 1-年卡 2-月卡 3-次票 4-实物商品',
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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兑换记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redemption_record`
--

LOCK TABLES `redemption_record` WRITE;
/*!40000 ALTER TABLE `redemption_record` DISABLE KEYS */;
INSERT INTO `redemption_record` VALUES (38,'REDEEM1765713013863E50B18',11,39,1,1,1,NULL,NULL,NULL,'2025-12-14 19:50:14','2025-12-14 19:50:14','2025-12-14 19:50:14'),(39,'REDEEM176571301387456A60E',11,39,4,4,1,NULL,NULL,NULL,NULL,'2025-12-14 19:50:14','2025-12-14 19:50:14');
/*!40000 ALTER TABLE `redemption_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund_record`
--

DROP TABLE IF EXISTS `refund_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款记录ID',
  `refund_no` varchar(64) NOT NULL COMMENT '退款单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_reason` varchar(200) DEFAULT NULL COMMENT '退款原因',
  `refund_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '退款状态 0-处理中 1-成功 2-失败 3-已关闭',
  `wx_refund_id` varchar(64) DEFAULT NULL COMMENT '微信退款单号',
  `refund_success_time` datetime DEFAULT NULL COMMENT '退款成功时间',
  `fail_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID（客服/管理员）',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`refund_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退款记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund_record`
--

LOCK TABLES `refund_record` WRITE;
/*!40000 ALTER TABLE `refund_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `refund_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repair_order_record`
--

DROP TABLE IF EXISTS `repair_order_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repair_order_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '补单记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
  `repair_type` tinyint(1) NOT NULL COMMENT '补单类型 1-手动查询微信补单 2-管理员强制补单',
  `repair_reason` varchar(500) NOT NULL COMMENT '补单原因',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `repair_result` tinyint(1) NOT NULL COMMENT '补单结果 1-成功 2-失败',
  `result_msg` varchar(500) DEFAULT NULL COMMENT '结果说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='补单记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repair_order_record`
--

LOCK TABLES `repair_order_record` WRITE;
/*!40000 ALTER TABLE `repair_order_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `repair_order_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_change_log`
--

DROP TABLE IF EXISTS `stock_change_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_change_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `change_type` tinyint(1) NOT NULL COMMENT '变更类型 1-下单扣减 2-取消恢复 3-退款恢复 4-管理员调整',
  `change_quantity` int NOT NULL COMMENT '变更数量（正数增加，负数减少）',
  `before_stock` int NOT NULL COMMENT '变更前库存',
  `after_stock` int NOT NULL COMMENT '变更后库存',
  `related_order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_order_id` (`related_order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存变更日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_change_log`
--

LOCK TABLES `stock_change_log` WRITE;
/*!40000 ALTER TABLE `stock_change_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_change_log` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (11,'oVdJy7TLYgGzJhAdLmz6VBXk6wdQ',NULL,'XMJD_22429',NULL,'莫桀_','https://rich-tams.oss-cn-beijing.aliyuncs.com/RichInterview/693e371975f1cdca7a5aa13c.jpeg',2,NULL,0,1,'2025-12-14 00:00:26','2025-12-14 14:05:14'),(12,'oTzFj1-6LfZnwDAZyEprnqKv789s',NULL,'XMJD_ec003',NULL,'莫桀_','https://panda-base-server.oss-cn-beijing.aliyuncs.com/pandaBase/693fb2149487797e39526a68.jpeg',1,NULL,0,1,'2025-12-15 13:20:31','2025-12-15 13:20:31');
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (9,6,'管理员','18888888888','北京市','北京市','东城区','xxx街道xxx小区东门快递驿站',1,'2025-12-11 16:02:45','2025-12-11 18:56:53'),(10,6,'管理员','18888888888','浙江省','宁波市','镇海区','xxx街道xx小区西门xx超市',0,'2025-12-11 16:03:29','2025-12-11 18:56:53'),(13,6,'xxx','18888888888','北京市','北京市','东城区','xxxxxx',0,'2025-12-11 18:52:23','2025-12-11 18:56:52'),(14,10,'杜瑞持','18888888888','北京市','北京市','东城区','1',1,'2025-12-13 18:36:58','2025-12-13 18:36:58'),(15,11,'张三','18888888888','山西省','阳泉市','郊区','xxxx小区',1,'2025-12-14 00:34:28','2025-12-14 00:34:28'),(16,12,'张三','18888888888','北京市','北京市','东城区','幸福小区东门',1,'2025-12-15 13:58:03','2025-12-15 13:58:03');
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_physical_product`
--

DROP TABLE IF EXISTS `user_physical_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_physical_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '实体商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0-未核销 1-已核销',
  `source_type` tinyint(1) NOT NULL COMMENT '来源类型 1-兑换码兑换 2-订单购买',
  `redemption_record_id` bigint DEFAULT NULL COMMENT '来源兑换记录ID',
  `order_id` bigint DEFAULT NULL COMMENT '来源订单ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_redemption` (`redemption_record_id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户实体商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_physical_product`
--

LOCK TABLES `user_physical_product` WRITE;
/*!40000 ALTER TABLE `user_physical_product` DISABLE KEYS */;
INSERT INTO `user_physical_product` VALUES (8,11,4,'基础蔬菜包*12','https://rich-tams.oss-cn-beijing.aliyuncs.com/%E9%A3%9F%E6%9D%90%E7%A4%BC%E5%8C%85%E5%95%86%E5%93%81%E5%9B%BE.png',1,0,1,39,NULL,NULL,'2025-12-14 19:50:14','2025-12-14 19:50:14');
/*!40000 ALTER TABLE `user_physical_product` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-15 15:04:09
