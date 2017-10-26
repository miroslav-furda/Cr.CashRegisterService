CREATE TABLE smena (
id int(11) UNSIGNED NOT NULL auto_increment,
id_uzivatel int(11) NOT NULL,
zaciatok timestamp NULL DEFAULT NULL,
koniec timestamp NULL DEFAULT NULL,
foreign key smena_uzivatel_fk (id_uzivatel) references uzivatel (id),
primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE pokladna_vklad (
id int(11) UNSIGNED NOT NULL auto_increment,
id_smena int(11) UNSIGNED NOT NULL,
hotovost_suma int(11) NOT NULL,
created_at timestamp NULL DEFAULT NULL,
foreign key(id_smena) references smena (id),
primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE pokladna_vyber (
id int(11) UNSIGNED NOT NULL auto_increment,
id_smena int(11) UNSIGNED NOT NULL,
hotovost_suma int(11) ,
stravne_listky_suma int(11) ,
terminal_suma int(11),
denna_uzavierka bit NOT NULL,
created_at timestamp NULL DEFAULT NULL,
foreign key(id_smena) references smena (id),
primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;