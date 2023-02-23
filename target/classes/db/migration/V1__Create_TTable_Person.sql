
CREATE TABLE IF NOT EXISTS `person` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `first_name` varchar(80) NOT NULL,
    `last_name` varchar(80) NOT NULL,
    `address` varchar(100) DEFAULT NULL,
    `gender` varchar(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    );
