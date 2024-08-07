CREATE TABLE `grobid_citation` (
 `id_document` int(11) NOT NULL,
 `xml_id` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
 `title` text COLLATE utf8_unicode_ci,
 `title_tr` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
 `authors` text COLLATE utf8_unicode_ci,
 `date` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
 `doi` varchar(512) CHARACTER SET utf8 DEFAULT NULL,
 PRIMARY KEY (`id_document`,`xml_id`),
 KEY `title_tr` (`title_tr`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci