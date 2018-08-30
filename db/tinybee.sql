CREATE DATABASE IF NOT EXISTS `tinybee`;

USE `tinybee`;

CREATE TABLE `test`
(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '編號',
  `context` varchar(64) NOT NULL DEFAULT '' COMMENT '內容',
  PRIMARY KEY (`id`)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='測試';

CREATE TABLE `platform_account`
(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '編號',
  `account` varchar(64) NOT NULL DEFAULT '' COMMENT '帳號',
  `token` varchar(2000) NOT NULL DEFAULT '' COMMENT '授權碼',
  `userId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '玩家編號',
  `createTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '建立時間',
  `updateTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '更新時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` USING BTREE (`account`),
  KEY `userId` (`userId`)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='平台帳號';

CREATE TABLE `facebook_account`
(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '編號',
  `account` varchar(64) NOT NULL DEFAULT '' COMMENT '帳號',
  `token` varchar(2000) NOT NULL DEFAULT '' COMMENT '授權碼',
  `userId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '玩家編號',
  `createTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '建立時間',
  `updateTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '更新時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` USING BTREE (`account`),
  KEY `userId` (`userId`)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='facebook帳號';

CREATE TABLE `goolge_account`
(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '編號',
  `account` varchar(64) NOT NULL DEFAULT '' COMMENT '帳號',
  `token` varchar(2000) NOT NULL DEFAULT '' COMMENT '授權碼',
  `userId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '玩家編號',
  `createTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '建立時間',
  `updateTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '更新時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` USING BTREE (`account`),
  KEY `userId` (`userId`)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='goolge帳號';

CREATE TABLE `ios_account`
(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '編號',
  `account` varchar(64) NOT NULL DEFAULT '' COMMENT '帳號',
  `token` varchar(2000) NOT NULL DEFAULT '' COMMENT '授權碼',
  `userId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '玩家編號',
  `createTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '建立時間',
  `updateTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '更新時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` USING BTREE (`account`),
  KEY `userId` (`userId`)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='ios帳號';

CREATE TABLE `user`
(
  `userId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '玩家編號',
  `createTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '建立時間',
  `updateTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '更新時間',
  `lockTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '鎖定時間',
  `monthTime` datetime NOT NULL DEFAULT '1899-12-30 00:00:00' COMMENT '包月時間',
  `fbid` varchar(64) DEFAULT NULL COMMENT 'facebook編號',
  `vip` smallint(6) unsigned NOT NULL DEFAULT '0' COMMENT 'VIP等級',
  `realPoint` int(7) NOT NULL DEFAULT '0' COMMENT '實點',
  `fakePoint` int(7) NOT NULL DEFAULT '0' COMMENT '虛點',
  `nickName` varchar(16) DEFAULT NULL COMMENT '名稱',
  `aboutme` varchar(100) DEFAULT NULL COMMENT '自我介紹',
  `avatarId` smallint(6) NOT NULL DEFAULT '0' COMMENT '角色編號',
  `lv` smallint(6) NOT NULL DEFAULT '0' COMMENT '等級',
  `money` int(11) NOT NULL DEFAULT '0' COMMENT '金錢',
  PRIMARY KEY (`userId`)
)
ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8 COMMENT='玩家資訊';