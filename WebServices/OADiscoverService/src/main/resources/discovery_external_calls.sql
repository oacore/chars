/**
 * Author:  lucas
 * Created: 28-Jan-2019
 */
CREATE TABLE `discovery_external_calls` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` varchar(100) DEFAULT NULL,
  `external_source` varchar(45) DEFAULT NULL,
  `external_link` varchar(255) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=latin1;