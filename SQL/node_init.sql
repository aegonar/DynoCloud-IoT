#CREATE SCHEMA `node`;

DROP TABLE IF EXISTS `Config`;
DROP TABLE IF EXISTS `OverrideHistory`;
DROP TABLE IF EXISTS `Telemetry`;
DROP TABLE IF EXISTS `Alerts`;
DROP TABLE IF EXISTS `EnclosureNode`;
DROP TABLE IF EXISTS `PetProfiles`;

CREATE TABLE `Config` (
	`UserID` INT,
	-- `UserName` VARCHAR(32),
	-- `Password` VARCHAR(64),
	`Token` VARCHAR(28),
	`CentralNodeID` INT ,
	`Minutes` INT NOT NULL , 
	-- `Threshold` INT NOT NULL ,
	`DynoCloud` BOOLEAN NOT NULL,
	`UserName` VARCHAR(32) ,
PRIMARY KEY (`UserID`)
);

CREATE TABLE `PetProfiles` (
	`PetProfileID` VARCHAR(32) NOT NULL ,
	-- `Name` VARCHAR(32) NOT NULL ,
	`Day_Temperature_SP` FLOAT NOT NULL ,
	`Day_Humidity_SP` FLOAT NOT NULL ,
	`Night_Temperature_SP` FLOAT NOT NULL ,
	`Night_Humidity_SP` FLOAT NOT NULL ,
	`Temperature_TH` FLOAT NOT NULL ,
	`Humidity_TH` FLOAT NOT NULL ,
	`DayTime` VARCHAR(5) NOT NULL,
	`NightTime` VARCHAR(5) NOT NULL ,
PRIMARY KEY (`PetProfileID`)
);

CREATE TABLE `EnclosureNode` (
	`EnclosureNodeID` INT NOT NULL AUTO_INCREMENT,
	`Name` VARCHAR(32) NOT NULL ,
	`OPTIONAL_LOAD` INT NOT NULL ,
	`PetProfileID` VARCHAR(32) NOT NULL ,
	`Online` BOOLEAN ,
	`Added` TIMESTAMP NOT NULL ,
PRIMARY KEY (`EnclosureNodeID`) ,
	UNIQUE KEY `Name` (`Name`) ,
CONSTRAINT fk_PetProfileID_EnclosureNode
	FOREIGN KEY (`PetProfileID`)
	REFERENCES PetProfiles (`PetProfileID`)
	-- ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE `Alerts` (
	`AlertID` INT NOT NULL AUTO_INCREMENT,
	`EnclosureNodeID` INT NOT NULL ,
	`DateTime` TIMESTAMP NOT NULL ,
	`Message` VARCHAR(256) ,
PRIMARY KEY (AlertID) ,
CONSTRAINT fk_EnclosureNodeID_Alerts
	FOREIGN KEY (EnclosureNodeID)
	REFERENCES EnclosureNode (EnclosureNodeID)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE `Telemetry` (
	`TelemetryID` INT NOT NULL AUTO_INCREMENT,
	
	`DateTime` TIMESTAMP NOT NULL ,
	`EnclosureNodeID` INT NOT NULL ,

	`TEMP` FLOAT NOT NULL ,
	`RH` FLOAT NOT NULL ,

	`OPTIONAL_LOAD` FLOAT NOT NULL ,
	`HEAT_LOAD` FLOAT NOT NULL ,

	`UV_STATUS` INT NOT NULL ,
	`HUM_STATUS` INT NOT NULL ,
	`HEAT_STATUS` INT NOT NULL ,
	`OPTIONAL_STATUS` INT NOT NULL ,

	`HUM_OR` INT NOT NULL ,
	`HEAT_OR` INT NOT NULL ,
	`UV_OR` INT NOT NULL ,
	`OPTIONAL_OR` INT NOT NULL ,
PRIMARY KEY (`TelemetryID`) ,
CONSTRAINT fk_EnclosureNodeID_Telemetry
	FOREIGN KEY (`EnclosureNodeID`)
	REFERENCES EnclosureNode (`EnclosureNodeID`)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE `OverrideHistory` (
	`OverrideHistoryID` INT NOT NULL AUTO_INCREMENT,
	`DateTime` TIMESTAMP NOT NULL ,
	`EnclosureNodeID` INT NOT NULL ,
	`HUM_OR` INT ,
	`HEAT_OR` INT ,
	`UV_OR` INT ,
	`OPTIONAL_OR` INT ,
	`HUM_STATUS` INT ,
	`HEAT_STATUS` INT ,
	`UV_STATUS` INT ,
	`OPTIONAL_STATUS` INT ,
PRIMARY KEY (`OverrideHistoryID`) ,
CONSTRAINT fk_EnclosureNodeID_OverrideHistory
	FOREIGN KEY (`EnclosureNodeID`)
	REFERENCES EnclosureNode (`EnclosureNodeID`)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

INSERT INTO Config (`UserID`,`DynoCloud`,`Minutes`)
VALUES ('0',FALSE,'1');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Gecko','85','50','80','50','5','20','06:30','18:30');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Python','90','50','80','50','5','20','06:30','16:30');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Toad','80','60','75','60','5','20','06:30','18:30');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Boa','90','50','80','50','5','20','06:30','16:30');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Iguana','90','60','80','60','5','20','06:30','18:30');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Scorpion','90','60','80','70','5','20','06:30','16:30')

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Turtle','85','75','75','75','5','20','06:30','18:30');

INSERT INTO PetProfiles (`PetProfileID`,`Day_Temperature_SP`,`Day_Humidity_SP`,`Night_Temperature_SP`,`Night_Humidity_SP`,`Temperature_TH`,`Humidity_TH`,`DayTime`,`NightTime`)
VALUES ('Tortoise','85','60','70','60','5','20','06:30','20:30');

