# its4adev

[Issue tracking system for Agile development with Scrum](https://github.com/habib-chawki/issue-tracker-front#its4adev)

# Setup guide

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

Specify the following properties inside **`application.properties`**

```properties
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/trackerdb

spring.datasource.username=[your_username]
spring.datasource.password=[your_password]

spring.jpa.properties.javax.persistence.validation.mode=none

secretKey=[your_secretKey]
```

Create the **`trackerdb`** database

```SQL
CREATE DATABASE trackerdb;
```
