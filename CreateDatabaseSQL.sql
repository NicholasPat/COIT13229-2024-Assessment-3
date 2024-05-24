-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mdhsDB
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mdhsDB
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mdhsDB` DEFAULT CHARACTER SET utf8 ;
USE `mdhsDB` ;

-- -----------------------------------------------------
-- Table `mdhsDB`.`DeliverySchedule`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mdhsDB`.`DeliverySchedule` (
  `postcode` INT NOT NULL,
  `deliveryDay` VARCHAR(12) NOT NULL,
  `deliveryCost` DOUBLE NOT NULL,
  PRIMARY KEY (`postcode`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mdhsDB`.`Account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mdhsDB`.`Account` (
  `accountId` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(30) NOT NULL,
  `lastName` VARCHAR(30) NOT NULL,
  `emailAddress` VARCHAR(100) NOT NULL,
  `password` VARCHAR(30) NOT NULL,
  `phoneNumber` VARCHAR(15) NULL,
  `deliveryAddress` VARCHAR(100) NULL,
  `postcode` INT NULL,
  `isAdmin` TINYINT NULL,
  PRIMARY KEY (`accountId`),
  INDEX `fk_Account_DeliverySchedule1_idx` (`postcode` ASC) VISIBLE,
  CONSTRAINT `fk_Account_DeliverySchedule1`
    FOREIGN KEY (`postcode`)
    REFERENCES `mdhsDB`.`DeliverySchedule` (`postcode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mdhsDB`.`Order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mdhsDB`.`Order` (
  `orderId` INT NOT NULL AUTO_INCREMENT,
  `accountId` INT NOT NULL,
  `deliveryTime` TIME NOT NULL,
  `totalCost` DOUBLE NOT NULL,
  PRIMARY KEY (`orderId`, `accountId`),
  INDEX `fk_Order_Account1_idx` (`accountId` ASC) VISIBLE,
  CONSTRAINT `fk_Order_Account1`
    FOREIGN KEY (`accountId`)
    REFERENCES `mdhsDB`.`Account` (`accountId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mdhsDB`.`Product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mdhsDB`.`Product` (
  `productId` INT NOT NULL AUTO_INCREMENT,
  `productName` VARCHAR(30) NOT NULL,
  `quantity` INT NOT NULL,
  `unit` VARCHAR(10) NOT NULL,
  `price` VARCHAR(45) NOT NULL,
  `ingredients` VARCHAR(200) NULL,
  PRIMARY KEY (`productId`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mdhsDB`.`OrderItems`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mdhsDB`.`OrderItems` (
  `orderId` INT NOT NULL,
  `productId` INT NOT NULL,
  `quantity` INT NOT NULL,
  `cost` DOUBLE NOT NULL,
  PRIMARY KEY (`orderId`, `productId`),
  INDEX `fk_OrderItems_Product1_idx` (`productId` ASC) VISIBLE,
  CONSTRAINT `fk_OrderItems_Order1`
    FOREIGN KEY (`orderId`)
    REFERENCES `mdhsDB`.`Order` (`orderId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_OrderItems_Product1`
    FOREIGN KEY (`productId`)
    REFERENCES `mdhsDB`.`Product` (`productId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
