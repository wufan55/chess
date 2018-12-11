# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.24)
# Database: chess
# Generation Time: 2018-12-11 11:51:42 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table chess_chessboard
# ------------------------------------------------------------

DROP TABLE IF EXISTS `chess_chessboard`;

CREATE TABLE `chess_chessboard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `line_1` varchar(255) DEFAULT NULL,
  `line_2` varchar(255) DEFAULT NULL,
  `line_3` varchar(255) DEFAULT NULL,
  `line_4` varchar(255) DEFAULT NULL,
  `line_5` varchar(255) DEFAULT NULL,
  `line_6` varchar(255) DEFAULT NULL,
  `line_7` varchar(255) DEFAULT NULL,
  `line_8` varchar(255) DEFAULT NULL,
  `line_9` varchar(255) DEFAULT NULL,
  `line_10` varchar(255) DEFAULT NULL,
  `line_11` varchar(255) DEFAULT NULL,
  `line_12` varchar(255) DEFAULT NULL,
  `line_13` varchar(255) DEFAULT NULL,
  `line_14` varchar(255) DEFAULT NULL,
  `line_15` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
