# CryptoFile
An application to store files on a secure and smooth way.

# Get started
Each point will be explain in it's seperate paragraph.
* Install localhost
* Create database
* Create table
* Insert data
* Change Java-files
* Run application

## Install localhost
In order to run this version of the application you need to have localhost installed on your computer (A good chice is XAMPP, https://www.apachefriends.org/index.html). After installing a localhost you need to start the MySQL server on port 3306.

## Create database
Below is SQL-code to do this. Change "database_name" to a database name of your choice.

`CREATE IF NOT EXISTS DATABASE database_name;`

## Create table
Below is SQL-code to do this. Change "table_name" to a table name of your choice and "database_name" to the name of the database just created.

```
USE database_name;

CREATE TABLE table_name (
    id int(11) PRIMARY KEY
    username varchar(250),
    email varchar(250),
);
```

## Insert data
You can do this in two ways. First, and easiest way is to run Registration.java which will insert a username of "Username", a password of a hashed "password" and an email address of "email@emails.se".

The other way is to insert the data by yourself. Below is SQL-code to do this. Change "table_name" to your table name. You also need to change "value1" to a username, "value2" to a password, which has been hashed using BCrypt, and value3 to a email. All within apostrophes(').

The hash algotithem used has to be BCrypt.
```
INSERT INTO table_name (username, password, email)
VALUES (value1, value2, value3);
```

## Change Java-files
You need to change the value of some rows in both Authentication.java and Registration.java. These are marked by "Edit this" and are in regards to information about your database such as username and password. 

## Run application
You run the application by running the class UI.java in the folder src/design. You will be promted with a window where you can enter your username or password. If you enter a correct username and password you will be moved to another page, else a text saying "Wrong username/password".
