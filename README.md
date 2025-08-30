# JPA + PostgreSQL Spring Boot Example

This project demonstrates a simple Spring Boot application using JPA with a PostgreSQL database. It provides a REST API
to manage users.

## Features

- Spring Boot 3
- JPA/Hibernate
- PostgreSQL integration
- REST API for users

## Project Structure

- `Users.java`: JPA entity for users
- `UsersRepository.java`: Spring Data JPA repository
- `UsersController.java`: REST controller for `/users` endpoint
- `application.properties`: Database and JPA configuration
- `data.sql`: Initial data for the users table
- `commpose.yml`: Docker Compose for PostgreSQL

## Dependencies (pom.xml)

<span style="color: red;">**Add Spring Boot JPA dependencies to connect to Postgres Database**</span>

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
<groupId>org.postgresql</groupId>
<artifactId>postgresql</artifactId>
<scope>runtime</scope>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## Configuration (src/main/resources/application.properties)

<span style="color: red;">**Configure Authentication to Database**</span>

```
spring.datasource.url=jdbc:postgresql://localhost:5432/albert-db
spring.datasource.username=admin
spring.datasource.password=password

spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.jpa.hibernate.ddl-auto=update
```

### Hibernate DDL Auto Configuration

`spring.jpa.hibernate.ddl-auto=update`  
This setting tells Hibernate to automatically update the database schema to match your JPA entities each time the
application starts.  
**Note:** Use `update` for development only. For production, consider using `validate` or managing schema changes
manually.

## Entity Example (src/main/java/org/example/jpapotgresintegration/Users.java)

<span style="color: red;">**Create Model of Tables in Database**</span>

- Getters and Setters are mandatory to interact with data using JPA
- `User` table is used by Postgre and it's not working - at least for me - I neeeded to rename table to Users.

```java

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;

    /*
     * Getters and setters are MANDATORY.
     * Without them, the REST endpoints will return only [{}].
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

## Repository Example (src/main/java/org/example/jpapostgresintegration/UsersRepository.java)

<span style="color: red;">**`UserRepository` interface defines the repository for the `Users` entity, extending Spring
Data JPA's `JpaRepository` to provide **CRUD** operations and database interaction for user data.**</span>

```java
public interface UsersRepository extends JpaRepository<Users, Long> {
}
```

## REST Controller (src/main/java/org/example/jpapostgresintegration/UsersController.java)

<span style="color: red;">**REST Endpoints Definition**</span>

```java

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @PostMapping
    public Users createUser(@RequestBody Users users) {
        return usersRepository.save(users);
    }
}
```

## Initial Data Ingestion (src/main/resources/data.sql)

<span style="color: red;">**Spring Boot will automatiically ingest the data into the DB from `data.sql` places
in `resources` folder?**</span>

If you place a file named <code>data.sql</code> in <code>src/main/resources</code>, Spring Boot will automatically
execute its SQL statements on application startup. This is handled by Spring Boot's DataSource initializer, which looks
for <code>schema.sql</code> (for DDL) and <code>data.sql</code> (for DML) by default. The statements in <code>
data.sql</code> are run after the schema is created (if JPA DDL is enabled), so your tables are pre-populated with data
before your application code or REST endpoints access the database.

This makes it easy to provide initial data for development and testing, without any extra configuration. If you want to
disable this behavior, you can set <code>spring.sql.init.mode=never</code> in <code>application.properties</code>.

<span style="color: orange;">**PROBLEM**: I needed to crate table in `data.sql` even, even though I had
`spring.jpa.hibernate.ddl-auto=update` & `spring.sql.init.mode=always` configured in `application.proerties`.
</span>

<span style="color: orange;">**SOLUTION**: Add to SQL querry which you are using for ingesting the users
`ON CONFLICT (id) DO NOTHING;`. With this line update will work like charm. In my case schema and data was set in
theasam sql file, but I know that you might use separate file `schema.sql` to manage it. </span>

<span style="color: orange;"> **NOTE**: After the tests schema could be deleted from SQL file, JPA Hibernate created it for me 
based on @Entity tag in `Users.java` </span>

```sql
CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO users (id, name, email)
VALUES (1, 'user1', 'user1@example.com'),
       (2, 'user2', 'user2@example.com'),
       (3, 'user3', 'user3@example.com')
ON CONFLICT (id) DO NOTHING;
```
xp
## Running with Docker Compose

<span style="color: red;">Start PostgresSQL using Docker Compose:<span style="color: red;">

 - [ ] Implement Docker Compose for the project to have it containerized.  

```sh
docker compose -f commpose.yml up -d
```

## Running the Spring Boot App

```sh
./mvnw spring-boot:run
```

## API Usage

- `GET /users` — List all users
- `POST /users` — Create a new user (JSON: `{ "name": "Name", "email": "email@example.com" }`)

## Example curl

```sh
# GET
curl http://localhost:8080/users


# POST
curl -X POST -H "Content-Type: application/json" -d '{"name":"Charlie","email":"charlie@example.com"}' http://localhost:8080/users
```
