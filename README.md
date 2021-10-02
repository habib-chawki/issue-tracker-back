# its4adev

[Issue tracking system for Agile development with Scrum](https://github.com/habib-chawki/issue-tracker-front#its4adev)

# Setup guide

Create the MySQL **`trackerdb`** database

```sql
CREATE DATABASE trackerdb;
```

Create a MySQL user account

```sql
CREATE USER 'trackeruser'@'localhost' IDENTIFIED BY 'P@$$w0rd';
```

Grant all privileges to `trackeruser` on `trackerdb`

```sql
GRANT ALL ON trackerdb.* TO 'trackeruser'@'localhost';
```

Specify the following properties inside **`application.properties`**

Clone the repository

```git
git clone https://github.com/habib-chawki/issue-tracker-back.git
```

Change the current working directory

```bash
cd issue-tracker-back/
```

Create the application properties file inside src/main/resources

```bash
mkdir src/main/resources && touch src/main/resources/application.properties
```

```properties
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/trackerdb

spring.datasource.username=trackeruser
spring.datasource.password=P@$$w0rd

spring.jpa.properties.javax.persistence.validation.mode=none

secretKey=averysecretkey
```
