/*
Navicat MySQL Data Transfer

Source Server         : 阿里云
Source Server Version : 50721
Source Host           : 120.77.247.132:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2020-12-03 21:20:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `item`
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '商品名',
  `price` double(10,0) NOT NULL DEFAULT '0' COMMENT '商品价格',
  `description` varchar(500) NOT NULL DEFAULT '' COMMENT '商品描述',
  `sales` int(11) NOT NULL DEFAULT '0' COMMENT '商品销量',
  `img_url` varchar(300) NOT NULL DEFAULT '' COMMENT '商品详情图地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES ('1', 'MacBook Pro', '18999', '苹果笔记本电脑', '11', 'https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4252253317,1902604470&fm=26&gp=0.jpg');
INSERT INTO `item` VALUES ('3', 'Apple Watch', '2300', '苹果手表', '26046', 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1976735686,2155744316&fm=26&gp=0.jpg');

-- ----------------------------
-- Table structure for `item_stock`
-- ----------------------------
DROP TABLE IF EXISTS `item_stock`;
CREATE TABLE `item_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `item_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_stock_idx` (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item_stock
-- ----------------------------
INSERT INTO `item_stock` VALUES ('1', '200000', '1');
INSERT INTO `item_stock` VALUES ('3', '9999', '3');

-- ----------------------------
-- Table structure for `order_info`
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` varchar(32) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `item_price` double NOT NULL DEFAULT '0' COMMENT '商品单价',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `order_price` double NOT NULL DEFAULT '0' COMMENT '订单总价',
  `promo_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2020120301000000', '1', '3', '999', '1', '999', '1');

-- ----------------------------
-- Table structure for `promo`
-- ----------------------------
DROP TABLE IF EXISTS `promo`;
CREATE TABLE `promo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(32) NOT NULL DEFAULT '' COMMENT '秒杀活动名称',
  `start_date` datetime NOT NULL COMMENT '秒杀活动开始时间',
  `end_date` datetime NOT NULL COMMENT '秒杀活动结束时间',
  `item_id` int(11) NOT NULL DEFAULT '0' COMMENT '秒杀商品id',
  `promo_item_price` double NOT NULL DEFAULT '0' COMMENT '秒杀商品价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of promo
-- ----------------------------
INSERT INTO `promo` VALUES ('1', 'AppleWatch限时抢购', '2020-03-12 21:08:30', '2021-02-01 22:08:35', '3', '999');

-- ----------------------------
-- Table structure for `rocketmq_transaction_log`
-- ----------------------------
DROP TABLE IF EXISTS `rocketmq_transaction_log`;
CREATE TABLE `rocketmq_transaction_log` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `transaction_id` varchar(64) NOT NULL COMMENT ' 事务id',
  `item_id` int(32) NOT NULL COMMENT '商品id',
  `amount` int(32) NOT NULL COMMENT '数量',
  `status` int(2) NOT NULL COMMENT '库存流水状态 1.初始化 2.下单成功且事务提交 3.事务回滚',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rocketmq_transaction_log
-- ----------------------------
INSERT INTO `rocketmq_transaction_log` VALUES ('33', '667a561e4c2d4bb98f05b3b174bdcaef', '3', '1', '2');

-- ----------------------------
-- Table structure for `sequence_info`
-- ----------------------------
DROP TABLE IF EXISTS `sequence_info`;
CREATE TABLE `sequence_info` (
  `name` varchar(255) NOT NULL COMMENT '序列名',
  `current_value` int(11) NOT NULL DEFAULT '0' COMMENT '当前值',
  `step` int(11) NOT NULL DEFAULT '0' COMMENT '步长',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sequence_info
-- ----------------------------
INSERT INTO `sequence_info` VALUES ('order_info', '10010', '10');

-- ----------------------------
-- Table structure for `user_info`
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '性别 1.男 2.女',
  `age` int(11) NOT NULL DEFAULT '0' COMMENT '年龄',
  `telphone` varchar(64) NOT NULL DEFAULT '',
  `register_mode` varchar(128) NOT NULL DEFAULT '',
  `third_party_id` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', 'World', '1', '23', '111', 'byPhone', '');
INSERT INTO `user_info` VALUES ('2', 'world6', '1', '23', '123456789', 'byPhone', '');

-- ----------------------------
-- Table structure for `user_password`
-- ----------------------------
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrpt_password` varchar(128) NOT NULL DEFAULT '' COMMENT '加密后的密码',
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_password
-- ----------------------------
INSERT INTO `user_password` VALUES ('1', 'xMpCOKC5I4INzFCab3WEmw==', '1');
INSERT INTO `user_password` VALUES ('2', 'xMpCOKC5I4INzFCab3WEmw==', '2');
