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

Clone the repository

```git
git clone https://github.com/habib-chawki/issue-tracker-back.git
```

Change the current working directory

```bash
cd issue-tracker-back/
```

Create the application properties resource file

```bash
mkdir src/main/resources && touch src/main/resources/application.properties
```

Specify the following properties inside `application.properties`

```properties
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/trackerdb

spring.datasource.username=trackeruser
spring.datasource.password=P@$$w0rd

spring.jpa.properties.javax.persistence.validation.mode=none

secretKey=averysecretkey
```

Launch the application

```bash
./mvnw spring-boot:run
```

# Run tests

Create the application properties resource file for testing

```bash
mkdir src/test/resources && touch src/test/resources/application.properties
```

Specify the following properties to enable testing with the H2 in memory database

```properties
spring.jpa.properties.hibernate.hbm2ddl.auto=create-drop
spring.datasource.url=jdbc:h2:mem:testDb;DB_CLOSE_DELAY=-1;MODE=MYSQL;DATABASE_TO_UPPER=false
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

spring.jpa.properties.javax.persistence.validation.mode=none

secretKey=averysecretkeyfortesting
```

Run tests

```bash
./mvnw test
```
