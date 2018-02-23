PRAGMA auto_vacuum = 1;
PRAGMA automatic_index = 1;
PRAGMA case_sensitive_like = 0;
PRAGMA defer_foreign_keys = 0;
PRAGMA encoding = 'UTF-8';
PRAGMA foreign_keys = 1;
PRAGMA ignore_check_constraints = 0;
PRAGMA journal_mode = WAL;
PRAGMA query_only = 0;
PRAGMA recursive_triggers = 1;
PRAGMA reverse_unordered_selects = 0;
PRAGMA secure_delete = 0;
PRAGMA synchronous = NORMAL;

CREATE TABLE "Adresse" ( `ID` INTEGER, `plz` TEXT NOT NULL CHECK(plz GLOB '[0-9][0-9][0-9][0-9][0-9]'), `ort` TEXT NOT NULL CHECK(ort GLOB '*[a-zA-Z]' AND LENGTH(ort)>0), `straße` TEXT NOT NULL CHECK(straße GLOB '*[a-zA-Z]' AND LENGTH(ort)>0), `hausnummer` INTEGER NOT NULL CHECK(hausnummer > 0), PRIMARY KEY(`ID`) );

CREATE TABLE "Anbieter" ( `bezeichnung` TEXT CHECK(LENGTH(bezeichnung)>0), PRIMARY KEY(`bezeichnung`) );

CREATE TABLE "Angebot" ( `ID` INTEGER, `artikelID` INTEGER NOT NULL, `preis` REAL NOT NULL CHECK(preis >= 0), PRIMARY KEY(`ID`), FOREIGN KEY(`artikelID`) REFERENCES `Artikel`(`ID`) );

CREATE TABLE "Angestellter" ( `kundeEmail` TEXT, `gehalt` REAL NOT NULL CHECK(gehalt >= 0), `jobbezeichnung` TEXT NOT NULL CHECK(jobbezeichnung LIKE '_%'), FOREIGN KEY(`kundeEmail`) REFERENCES `Kunde`(`email`), PRIMARY KEY(`kundeEmail`) );

CREATE TABLE "Artikel" ( `ID` INTEGER, `bezeichnung` TEXT NOT NULL CHECK(bezeichnung LIKE '_%'), `beschreibung` TEXT NOT NULL CHECK(beschreibung LIKE '_%'), PRIMARY KEY(`ID`) );

CREATE TABLE "Bild" ( `ID` INTEGER, `artikelID` INTEGER NOT NULL, `name` TEXT NOT NULL CHECK(name LIKE '_%'), `bild` BLOB NOT NULL, FOREIGN KEY(`artikelID`) REFERENCES `Artikel`(`ID`), PRIMARY KEY(`ID`) );

CREATE TABLE "Kunde" ( `email` TEXT CHECK(email LIKE '%_@_%.__%'), `adresseID` INTEGER NOT NULL, `vorname` TEXT NOT NULL CHECK(vorname GLOB '*[a-zA-Z]' AND LENGTH ( vorname ) > 0), `nachname` TEXT NOT NULL CHECK(nachname GLOB '*[a-zA-Z]' AND LENGTH ( nachname ) > 0), `passwort` TEXT NOT NULL CHECK(passwort LIKE '%______%'), PRIMARY KEY(`email`), FOREIGN KEY(`adresseID`) REFERENCES `Adresse`(`ID`) );

CREATE TABLE "Lieferabo" ( `warenkorbID` INTEGER, `beginn` DATE NOT NULL CHECK(date ( beginn ) IS NOT NULL), `ende` DATE NOT NULL CHECK(date ( ende ) IS NOT NULL AND beginn<=ende), `lieferintervall` INTEGER NOT NULL CHECK(lieferintervall > 0), FOREIGN KEY(`warenkorbID`) REFERENCES `Warenkorb`(`ID`), PRIMARY KEY(`warenkorbID`) );

CREATE TABLE "Lieferdienst" ( `bezeichnung` TEXT CHECK(bezeichnung LIKE '_%'), `tarif` REAL NOT NULL CHECK(tarif >= 0), PRIMARY KEY(`bezeichnung`) );

CREATE TABLE "Newsletter" ( `ID` INTEGER PRIMARY KEY AUTOINCREMENT, `angestellterKundeEmail` TEXT NOT NULL, `betreff` TEXT NOT NULL CHECK(betreff LIKE '_%'), `text` TEXT NOT NULL CHECK(text LIKE '_%'), `datum` DATE NOT NULL CHECK(date(datum) IS NOT NULL), FOREIGN KEY(`angestellterKundeEmail`) REFERENCES `Angestellter`(`kundeEmail`) );

CREATE TABLE "Premiumkunde" ( `kundeEmail` TEXT, `studentenausweis` BLOB CHECK(studentenausweis IS NOT NULL OR gebuehr = 1), `gebuehr` INTEGER NOT NULL CHECK(gebuehr = 0 || gebuehr = 1), `ablaufdatum` DATE NOT NULL CHECK(date(ablaufdatum) IS NOT NULL), FOREIGN KEY(`kundeEmail`) REFERENCES `Kunde`(`email`), PRIMARY KEY(`kundeEmail`) );

CREATE TABLE "Schlagwort" ( `wort` TEXT collate nocase CHECK(wort GLOB '*[a-zA-Z]' AND LENGTH(wort) > 0), PRIMARY KEY(`wort`) );

CREATE TABLE "Warenkorb" ( `ID` INTEGER, `kundeEmail` TEXT NOT NULL, `bestelldatum` DATE NOT NULL CHECK(date(bestelldatum) IS NOT NULL), `bestellstatus` TEXT NOT NULL CHECK(bestellstatus = 'versendet' OR bestellstatus = 'abgeschlossen' OR bestellstatus = 'versandfertig' OR bestellstatus = 'in Bearbeitung' OR bestellstatus = 'storniert'), FOREIGN KEY(`kundeEmail`) REFERENCES `Kunde`(`email`), PRIMARY KEY(`ID`) );

CREATE TABLE `angemeldet` ( `kundeEmail` TEXT, `newsletterID` INTEGER, FOREIGN KEY(`kundeEmail`) REFERENCES "Kunde"(`email`), PRIMARY KEY(`kundeEmail`,`newsletterID`), FOREIGN KEY(`newsletterID`) REFERENCES `Newsletter`(`ID`) );

CREATE TABLE `besitzt` ( `artikelID` INTEGER, `schlagwortWort` TEXT, FOREIGN KEY(`schlagwortWort`) REFERENCES `Schlagwort`(`wort`), FOREIGN KEY(`artikelID`) REFERENCES `Artikel`(`ID`), PRIMARY KEY(`artikelID`,`schlagwortWort`) );

CREATE TABLE "bietet" ( `anbieterBezeichnung` TEXT, `angebotID` INTEGER, `bestand` INTEGER NOT NULL CHECK(bestand > 0), PRIMARY KEY(`anbieterBezeichnung`,`angebotID`), FOREIGN KEY(`angebotID`) REFERENCES `Angebot`(`ID`), FOREIGN KEY(`anbieterBezeichnung`) REFERENCES `Anbieter`(`bezeichnung`) );

CREATE TABLE "empfiehlt" ( `artikel1ID` INTEGER, `artikel2ID` INTEGER CHECK(artikel1ID != artikel2ID), FOREIGN KEY(`artikel2ID`) REFERENCES `Artikel`(`ID`), PRIMARY KEY(`artikel1ID`,`artikel2ID`), FOREIGN KEY(`artikel1ID`) REFERENCES `Artikel`(`ID`) );

CREATE TABLE `enthaelt` ( `newsletterID` INTEGER, `artikelID` INTEGER, FOREIGN KEY(`artikelID`) REFERENCES `Artikel`(`ID`), FOREIGN KEY(`newsletterID`) REFERENCES `Newsletter`(`ID`), PRIMARY KEY(`newsletterID`,`artikelID`) );

CREATE TABLE "inw" ( `warenkorbID` INTEGER, `angebotID` INTEGER, `anbieterBezeichnung` TEXT, `anzahl` INTEGER NOT NULL CHECK(anzahl > 0), FOREIGN KEY(`angebotID`) REFERENCES `Angebot`(`ID`), FOREIGN KEY(`warenkorbID`) REFERENCES `Warenkorb`(`ID`), PRIMARY KEY(`warenkorbID`,`angebotID`,`anbieterBezeichnung`), FOREIGN KEY(`anbieterBezeichnung`) REFERENCES `Anbieter`(`bezeichnung`) );

CREATE TABLE "zustellen" ( `warenkorbID` INTEGER, `lieferdienstBezeichnung` TEXT, `datum` DATE NOT NULL CHECK(date ( datum ) IS NOT NULL AND datum>date('now')), FOREIGN KEY(`lieferdienstBezeichnung`) REFERENCES `Lieferdienst`(`bezeichnung`), PRIMARY KEY(`warenkorbID`,`lieferdienstBezeichnung`), FOREIGN KEY(`warenkorbID`) REFERENCES `Warenkorb`(`ID`) );

CREATE TRIGGER update_existing_newsletter_date AFTER UPDATE ON Newsletter FOR EACH ROW WHEN NEW.datum != OLD.datum
BEGIN
UPDATE Newsletter SET datum=date('now')
WHERE ROWID=NEW.ROWID;
END;

CREATE TRIGGER update_new_newsletter_date AFTER INSERT ON Newsletter FOR EACH ROW
BEGIN
UPDATE Newsletter SET datum=date('now')
WHERE ROWID=NEW.ROWID;
END;

INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (1,'20513', 'Bergheim', 'Friedrichstraße', 12);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (2,'13028', 'Elnau', 'Hauptstraße', 132);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (3,'40213', 'Holm', 'Götheallee', 34);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (4,'21302', 'Neustadt', 'Sonnenstraße', 8);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (5,'61221', 'Meldorf', 'Franzstraße', 32);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (6,'44212', 'Vrink', 'Waldweg', 16);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (7,'55211', 'Echterdingen', 'Münchner Straße', 45);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (8,'22000', 'Olkberg', 'Ludwigstraße', 39);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (9,'19271', 'Drungen', 'Lessingstraße', 51);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (10,'37012', 'Salzheim', 'Kesselgasse', 1);
INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES (11,'39112', 'Neusee', 'Schafweg', 38);

INSERT INTO Anbieter(bezeichnung) VALUES ('Lebensmittel GmbH');
INSERT INTO Anbieter(bezeichnung) VALUES ('MINUS');
INSERT INTO Anbieter(bezeichnung) VALUES ('DRA');
INSERT INTO Anbieter(bezeichnung) VALUES ('Bauer Stefan');
INSERT INTO Anbieter(bezeichnung) VALUES ('marketeer');

INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (1,'Banane', 'reif');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (2,'Apfel', 'sauer und mehlig');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (3,'Salami','Rind, scharf');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (4,'Camembert','milder Weichkäse');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (5,'Mehl','Typ 405');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (6,'Essig','weiß, 600ml');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (7,'Spitzkohl','frisch');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (8,'Kartoffel','festkochend');	
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (9,'Milch','1% Fett');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (10,'Eier','Freilandhaltung');
INSERT INTO Artikel(ID,bezeichnung,beschreibung) VALUES (11,'Pinsel','Malerpinsel');

INSERT INTO Angebot(ID,artikelID,preis) VALUES (1,1, '3.00');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (2,1, '2.50');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (3,2, '2.00');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (4,2, '2.29');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (5,3, '2.00');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (6,3, '1.90');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (7,5, '2.00');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (8,5, '2.00');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (9,5, '1.00');
INSERT INTO Angebot(ID,artikelID,preis) VALUES (10,10, '2.00');

INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('hemü000@gmail.com',1,'Heinz','Müller','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('thsc000@aol.com',2,'Thomas','Schmidt','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('klsc000@t-online.de',3,'Klaus','Schneider','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('isfi000@web.de',4,'Isabella','Fischer','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('chwe000@hhh.de',5,'Charlotte','Weber','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('mama000@gmail.com',6,'Marta','Mayer','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('tabe000@gmx.de',7,'Tanja','Becker','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('mosc000@yahoo.com',8,'Moritz','Schulz','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('viho00@xaj.org',9,'Viola','Hoffmann','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('roba000@ldl.nl',10,'Rolf','Bauer','password');
INSERT INTO Kunde(email,adresseID,vorname,nachname,passwort) VALUES ('javo000@gmail.com',11,'Jana','Vogt','password');

INSERT INTO Angestellter(kundeEmail,gehalt,jobbezeichnung) VALUES ('hemü000@gmail.com','100000.00','Systemadministrator');
INSERT INTO Angestellter(kundeEmail,gehalt,jobbezeichnung) VALUES ('tabe000@gmx.de','75000.00','Systemadministrator');
INSERT INTO Angestellter(kundeEmail,gehalt,jobbezeichnung) VALUES ('viho00@xaj.org','60000.00','Webdesigner');
INSERT INTO Angestellter(kundeEmail,gehalt,jobbezeichnung) VALUES ('roba000@ldl.nl','0.00','Praktikant');
INSERT INTO Angestellter(kundeEmail,gehalt,jobbezeichnung) VALUES ('isfi000@web.de','50000.00','Manager');

INSERT INTO Lieferdienst(bezeichnung,tarif) VALUES('Packet Ltd.','5.00');
INSERT INTO Lieferdienst(bezeichnung,tarif) VALUES('Deutsche Post','6.00');
INSERT INTO Lieferdienst(bezeichnung,tarif) VALUES('DHD','7.00');
INSERT INTO Lieferdienst(bezeichnung,tarif) VALUES('Lloydd','4.99');
INSERT INTO Lieferdienst(bezeichnung,tarif) VALUES('PPPOST','5.50');

INSERT INTO Newsletter(ID,angestellterKundeEmail,betreff,text,datum) VALUES(1,'roba000@ldl.nl','Wöchentliche Angebote','text here','2018-10-08');
INSERT INTO Newsletter(ID,angestellterKundeEmail,betreff,text,datum) VALUES(2,'roba000@ldl.nl','Festtags-Angebote','text here','2017-12-20');

INSERT INTO Schlagwort(wort) VALUES('Lebensmittel');
INSERT INTO Schlagwort(wort) VALUES('Obst');
INSERT INTO Schlagwort(wort) VALUES('Möbel');
INSERT INTO Schlagwort(wort) VALUES('Elektronik');
INSERT INTO Schlagwort(wort) VALUES('Eisenwaren');
INSERT INTO Schlagwort(wort) VALUES('Malerzubehör');
INSERT INTO Schlagwort(wort) VALUES('Werkzeug');

INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(1,'thsc000@aol.com','2014-12-01','storniert');
INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(2,'thsc000@aol.com','2018-02-05','in Bearbeitung');
INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(3,'chwe000@hhh.de','2018-02-07','versandfertig');
INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(4,'mama000@gmail.com','2016-12-24','abgeschlossen');
INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(5,'tabe000@gmx.de','2018-01-26','versendet');
INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(6,'javo000@gmail.com','2018-02-09','abgeschlossen');
INSERT INTO Warenkorb(ID,kundeEmail,bestelldatum,bestellstatus) VALUES(7,'viho00@xaj.org','2018-02-15','in Bearbeitung');

INSERT INTO Lieferabo(warenkorbID,beginn,ende,lieferintervall) VALUES(1,'2018-01-01','2018-02-01',2);
INSERT INTO Lieferabo(warenkorbID,beginn,ende,lieferintervall) VALUES(3,'2016-05-13','2017-05-13',3);
INSERT INTO Lieferabo(warenkorbID,beginn,ende,lieferintervall) VALUES(6,'2018-01-27','2018-01-28',24);

INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('chwe000@hhh.de',1);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('chwe000@hhh.de',2);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('javo000@gmail.com',1);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('javo000@gmail.com',2);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('hemü000@gmail.com',1);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('hemü000@gmail.com',2);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('mama000@gmail.com',1);
INSERT INTO angemeldet(kundeEmail,newsletterID) VALUES ('isfi000@web.de',1);

INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(1,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(2,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(3,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(4,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(5,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(6,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(7,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(8,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(9,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(10,'Lebensmittel');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(1,'Obst');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(2,'Obst');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(11,'Werkzeug');
INSERT INTO besitzt(artikelID,schlagwortWort) VALUES(11,'Malerzubehör');

INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Bauer Stefan',8,10000);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Bauer Stefan',9,300);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Bauer Stefan',10,167);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Bauer Stefan',1,2000);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('marketeer',4,100);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('marketeer',5,10);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('marketeer',6,24);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('marketeer',7,39);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Lebensmittel GmbH',1,90);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Lebensmittel GmbH',2,45);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Lebensmittel GmbH',4,211);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Lebensmittel GmbH',5,302);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Lebensmittel GmbH',6,9);
INSERT INTO bietet(anbieterBezeichnung,angebotID,bestand) VALUES('Lebensmittel GmbH',7,2);

INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(1,2);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(1,3);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(5,1);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(6,1);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(7,1);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(2,6);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(3,9);
INSERT INTO empfiehlt(artikel1ID,artikel2ID) VALUES(10,2);

INSERT INTO enthaelt(newsletterID,artikelID) VALUES(1,1);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(1,2);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(1,5);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(1,9);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(1,8);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(2,6);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(2,7);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(2,8);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(1,11);
INSERT INTO enthaelt(newsletterID,artikelID) VALUES(2,11);

INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(1,1,'Lebensmittel GmbH',10);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(1,4,'Lebensmittel GmbH',5);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(1,2,'Lebensmittel GmbH',21);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(2,6,'marketeer',2);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(3,7,'marketeer',1);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(5,8,'Bauer Stefan',5000);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(3,9,'Bauer Stefan',100);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(6,10,'Bauer Stefan',100);
INSERT INTO inw(warenkorbID,angebotID,anbieterBezeichnung,anzahl) VALUES(4,4,'Lebensmittel GmbH',10);

INSERT INTO zustellen(warenkorbID,lieferdienstBezeichnung,datum) VALUES(1,'Deutsche Post','2018-12-12');
INSERT INTO zustellen(warenkorbID,lieferdienstBezeichnung,datum) VALUES(2,'Deutsche Post','2018-06-15');
INSERT INTO zustellen(warenkorbID,lieferdienstBezeichnung,datum) VALUES(3,'PPPOST','2018-07-14');
INSERT INTO zustellen(warenkorbID,lieferdienstBezeichnung,datum) VALUES(4,'Lloydd','2019-09-05');
INSERT INTO zustellen(warenkorbID,lieferdienstBezeichnung,datum) VALUES(5,'DHD','2018-05-07');
INSERT INTO zustellen(warenkorbID,lieferdienstBezeichnung,datum) VALUES(6,'Packet Ltd.','2018-03-23');

INSERT INTO Bild(id,artikelID,name,bild) VALUES(1,1,'banana',readfile('banana-icon.png'));
INSERT INTO Bild(id,artikelID,name,bild) VALUES(2,8,'kartoffel',readfile('kartoffel-icon.png'));

INSERT INTO Premiumkunde(kundeEmail,studentenausweis,gebuehr,ablaufdatum) VALUES ('viho00@xaj.org',readfile('ausweis-icon.png'),0,'2020-10-20');
INSERT INTO Premiumkunde(kundeEmail,studentenausweis,gebuehr,ablaufdatum) VALUES ('isfi000@web.de',NULL,1,'2019-01-21');
