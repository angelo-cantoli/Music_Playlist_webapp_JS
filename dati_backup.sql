/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: PlaylistDB
-- ------------------------------------------------------
-- Server version	11.8.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `album` VALUES
(1,'Giustizia','1_1753017243577_img_pexels-gasparzaldo-32822356.jpg','Rolling Stones',2000),
(2,'Giustizia','1_1753017522123_img_pexels-leo-gilmant-1144880343-20993168.jpg','Rolling Stones',2005),
(3,'Roma mia','1_1753017603668_img_pexels-lexi-lauwers-1431940-32756683.jpg','DJ John',2010),
(4,'Cielo azzurro','1_1753017735247_img_pexels-midori-344476677-16768648.jpg','Lola',1995),
(5,'Strada','1_1753017934116_img_pexels-mike-c-2151163165-32297187.jpg','Gazzelle',2018),
(6,'Piano piano','1_1753017997134_img_pexels-nihat-kucuk-2152425147-32948308.jpg','Mozart',1864),
(7,'Festone','1_1753018062274_img_pexels-paolo-sanchez-2149881372-32515532.jpg','Freddie',2023),
(8,'Giallo','1_1753018160408_img_pexels-pham-ngoc-anh-170983008-12148608.jpg','Brad Pitt',2006),
(9,'Divina Commedia','1_1753018225203_img_pexels-sara-kazemi-2148049458-31495882.jpg','Dante',2021),
(10,'Andrea','1_1753018273002_img_pexels-snapsbyclark-14790230.jpg','Morgan',2010),
(11,'Wow','1_1753018345150_img_pexels-sofiia-asmi-3378970-32485048.jpg','Paolo Brusa',2019),
(12,'Koala','1_1753018416596_img_pexels-suedadilli-18960521.jpg','Calcutta',2021),
(13,'Vittoria','2_1753019389043_img_pexels-thomas-balabaud-735585-30093555.jpg','Achille',2018),
(14,'Vero','2_1753019447824_img_pexels-ufuk-gerekli-441801306-32938218.jpg','Micheal Kors',2010),
(15,'Indecente','2_1753019491906_img_pexels-lexi-lauwers-1431940-32756683.jpg','Goku',1999),
(16,'Iguana','2_1753019534994_img_pexels-sara-kazemi-2148049458-31495882.jpg','Radish',2008),
(17,'Location','2_1753019594768_img_pexels-ulkar-batista-3827543-32939632.jpg','Kevin',2008);
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Dumping data for table `contains`
--

LOCK TABLES `contains` WRITE;
/*!40000 ALTER TABLE `contains` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `contains` VALUES
(1,2),
(1,4),
(2,4),
(1,5),
(1,6),
(1,7),
(1,8),
(1,9),
(2,9),
(1,10),
(2,10),
(1,11),
(1,13),
(1,14),
(1,15),
(3,16),
(3,17),
(3,18),
(4,18),
(3,19),
(3,20),
(4,20);
/*!40000 ALTER TABLE `contains` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `playlist` VALUES
(1,'Divertimento','2025-07-20',1,1,'[11,15,6,10,8,7,13,14,2,5,4,9]'),
(2,'Rilassante','2025-07-20',1,0,NULL),
(3,'Notte','2025-07-20',2,0,NULL),
(4,'Nuovo','2025-07-20',2,0,NULL);
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Dumping data for table `song`
--

LOCK TABLES `song` WRITE;
/*!40000 ALTER TABLE `song` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `song` VALUES
(1,'Neve','Rolling Stones',1,'Dance','1_1753017243651_song_ABBA_-_Gimme__Gimme__Gimme___A_Man_After_Midnight___Lyrics_.mp3','1_1753017243577_img_pexels-gasparzaldo-32822356.jpg',1,2000,'52bafc7bfd3375da14e1b1f20fdc80625a0828589c688b386990b7fc4c09046f'),
(2,'Uomini','Rolling Stones',1,'Dance','1_1753017287812_song_afterlight.mp3','1_1753017287796_img_pexels-jessie-garcia-2152809153-32539049.jpg',1,2000,'90f4a95ea42e5cdd90674e34ac6105e8265dcbd1c33b62c5931cbcb527091110'),
(3,'Gatto','Rolling Stones',1,'Dance','1_1753017358146_song_april-walks-and-tuna-onigiri.mp3','1_1753017358114_img_pexels-jordicosta-32006759.jpg',1,2000,'4d47ff33fe71c817bf41ab780c2002767c80990ee92d9e5a3313e5ae956c0f5b'),
(4,'Castelli di sabbia','Rolling Stones',1,'Rock','1_1753017438179_song_Badman_Freestyle_master_v_0.1.mp3','1_1753017438147_img_pexels-kristina-bekher-1944658582-32801288.jpg',1,2000,'322a691a8caa21b2bb3760f8e49b07a8c2a2312b25cf2282e9fca503c287e5d8'),
(5,'Pesce fritto','Rolling Stones',1,'Dance','1_1753017522155_song_Coscienza_freestyle_master_v_0.1.mp3','1_1753017522123_img_pexels-leo-gilmant-1144880343-20993168.jpg',2,2005,'169d4967f46d32a10e2cbaf71a7a7a8a27071de97e4021f708e59e2ac305bb40'),
(6,'Bella giornata','DJ John',1,'Jazz','1_1753017603684_song_funky-groovin-70s.mp3','1_1753017603668_img_pexels-lexi-lauwers-1431940-32756683.jpg',3,2010,'90058cbc65aab24e478323f403fdaaed1ad68feaea9b1caf59d36ba6ba396247'),
(7,'Milano','Lola',1,'Blues','1_1753017735253_song_halloween-is-coming.mp3','1_1753017735247_img_pexels-midori-344476677-16768648.jpg',4,1995,'cd33b0df2447956cd74afe312650d48822447934e6593ebe70fa0fc144df6bae'),
(8,'Bolognese','Gazzelle',1,'Rap','1_1753017934123_song_happpiness-summer-motivational-music.mp3','1_1753017934116_img_pexels-mike-c-2151163165-32297187.jpg',5,2018,'a41b9d33ecda0923f0bffab7c0456d2415cdcb5edd27a7bd798a3ba9f6feebe6'),
(9,'Melodia','Mozart',1,'Classical','1_1753017997147_song_hip_pop_export_1.mp3','1_1753017997134_img_pexels-nihat-kucuk-2152425147-32948308.jpg',6,1864,'a27147ac8692fd5972e1eef1516298a78b66dd78db22fc2e153e93ff3e85ad6c'),
(10,'Milionario','Freddie',1,'Pop','1_1753018062283_song_maestro-from-vienna.mp3','1_1753018062274_img_pexels-paolo-sanchez-2149881372-32515532.jpg',7,2023,'072246b85784fbe103ed3b65f0459814a81454e9a78167c22b7997bd03d678bc'),
(11,'Tecnico','Brad Pitt',1,'Dance','1_1753018160420_song_making-choices.mp3','1_1753018160408_img_pexels-pham-ngoc-anh-170983008-12148608.jpg',8,2006,'ba6519b297a3b4eb8bf1be6a945835a3f4e18adef5629b04af7ae27caa38fcd4'),
(12,'Polvere','Dante',1,'Jazz','1_1753018225220_song_Siamo_Noi_master_v_0.1.mp3','1_1753018225203_img_pexels-sara-kazemi-2148049458-31495882.jpg',9,2021,'5e0753084ad5256193d715e8441d5cc7b3ce03852a9453d0da9a180ccc205966'),
(13,'Andrea','Morgan',1,'Rock','1_1753018273014_song_song_loop_test_-_09_09_24__01.09.m4a','1_1753018273002_img_pexels-snapsbyclark-14790230.jpg',10,2010,'7f94020affbd273fbd69f5b6bb33ae6e7c7ba0bbbfc084af67772629375daf7a'),
(14,'Skateboard','Paolo Brusa',1,'Blues','1_1753018345161_song_stay-with-you-feat-maliander.mp3','1_1753018345150_img_pexels-sofiia-asmi-3378970-32485048.jpg',11,2019,'1ae8e24af19b36bb8be21322841bf69b008023a99e9ea73407864807fb516a85'),
(15,'Totale','Calcutta',1,'Dance','1_1753018416601_song_tolerance.mp3','1_1753018416596_img_pexels-suedadilli-18960521.jpg',12,2021,'75c5f92ecec3c5b5dc0e3a9c419e526151bc8191e214180f5b44134114604d16'),
(16,'Mare chiaro','Achille',2,'Rap','2_1753019389064_song_happpiness-summer-motivational-music.mp3','2_1753019389043_img_pexels-thomas-balabaud-735585-30093555.jpg',13,2018,'a41b9d33ecda0923f0bffab7c0456d2415cdcb5edd27a7bd798a3ba9f6feebe6'),
(17,'Sacro','Micheal Kors',2,'Jazz','2_1753019447845_song_stay-with-you-feat-maliander.mp3','2_1753019447824_img_pexels-ufuk-gerekli-441801306-32938218.jpg',14,2010,'1ae8e24af19b36bb8be21322841bf69b008023a99e9ea73407864807fb516a85'),
(18,'Forza','Goku',2,'Dance','2_1753019491926_song_afterlight.mp3','2_1753019491906_img_pexels-lexi-lauwers-1431940-32756683.jpg',15,1999,'90f4a95ea42e5cdd90674e34ac6105e8265dcbd1c33b62c5931cbcb527091110'),
(19,'Sushi','Radish',2,'Blues','2_1753019535012_song_Badman_Freestyle_master_v_0.1.mp3','2_1753019534994_img_pexels-sara-kazemi-2148049458-31495882.jpg',16,2008,'322a691a8caa21b2bb3760f8e49b07a8c2a2312b25cf2282e9fca503c287e5d8'),
(20,'Baby','Kevin',2,'Pop','2_1753019594787_song_tolerance.mp3','2_1753019594768_img_pexels-ulkar-batista-3827543-32939632.jpg',17,2008,'75c5f92ecec3c5b5dc0e3a9c419e526151bc8191e214180f5b44134114604d16');
/*!40000 ALTER TABLE `song` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `user` VALUES
(1,'LucaBVV','$2a$10$wiKrA921TgyaimKbZls6Re17EJ4Et1PgL.WpQrlMq11yDUcCeaavO','Luca','Bevivino'),
(2,'angeloCNT','$2a$10$AL/3.mLHXfysrwwhdpIj8.CwwS.7RfdGEt/1Y15N2ZbfiZUXb6nly','Angelo','Cantoli');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
commit;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-07-20 17:18:21
