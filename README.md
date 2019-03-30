# CryptoFile

För att köra startar du klassen UI om ligger i mappen src/design. Det kommer då upp ett fönster där man kan skriva in ett användarnamn och ett lösenord. 
Användarnamnet är "Mattias" och lösenordet är "hejsan".

# CryptoFile

## How to run
In order to run this version of the application you need to have localhost installed on your computer (A good chice is XAMPP, https://www.apachefriends.org/index.html). You also need to make a database on your localhost. Below is SQL-code to do this. Change "database_name" to a database name of your choice.

`CREATE IF NOT EXISTS DATABASE database_name;`

After you've created the database you need to create a table. Below is SQL-code to do this. Change "table_name" to a table name of your choice.

```
CREATE TABLE table_name (
    id int(11) PRIMARY KEY
    username varchar(250),
    email varchar(250),
);
```

After successfully created a database and a table you need to insert data to the table. You can do this in two ways. First, and easiest way is to run Registration.java which will insert a username of "Username" and a password of a hashed "password". The hash algotithem used is BCrypt. The other way is to insert the data by yourself. Below is SQL-code to do this. Change table_name to your table name. You also need to change value1 to a username and a password, which has been hased using BCrypt. Both within apostrophes(').
```
INSERT INTO table_name (username, email)
VALUES (value1, value2);
```

After this step you can run the application by running the class UI.java in the folder src/design. You will be promted with a window where you can enter your username or password. 
