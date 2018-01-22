# SQL API
SQL API is a lightweight library to handle SQL operations. **It is only compatible with Java8+**.

The use is simple as you will see. By default in Java SQLException must be caught ; here we use a RuntimeException (SQLFaultException).

You can use the following dependency in your Maven projects :

```xml
<dependency>
    <groupId>com.github.fabienbarbero</groupId>
    <artifactId>sql-api</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Use of DAO
Data access objects are useful when using SQL queries. For instance if we have to store users in a table we can create a class names UserDAO and some implementations for various SQL engine (MySQL, Oracle ...)

#### Example using SQLite
```java
public class User
        implements Entity
{

    public static User newInstance( String name, String email ) {
        User user = new User();
        user.uuid = UUID.randomUUID().toString();
        user.name = name;
        user.email = email;
        return user;
    }

    private String uuid;
    private String name;
    private String email;

    // Use getters and setters

}
```

```java
public class UserDAOImpl
        implements UserDAO, SQLRecordMapper<User>
{

    private final SQLRunner runner;

    public UserDAOImpl( SQLTransaction tx ) {
        runner = new SQLRunner( tx );
    }

    @Override
    public User buildEntity( SQLRecord record ) {
        // Map the SQL record to a new Java entity
        User user = new User();
        user.setUuid( record.getString( "UUID" ).get() );
        user.setEmail( record.getString( "EMAIL" ).get() );
        user.setName( record.getString( "NAME" ).get() );
        return user;
    }

    @Override
    public void addEntity( User entity )
            throws SQLFaultException {
        runner.execute( SQLQuery.of( "insert into USERS (UUID, EMAIL, NAME) values (?,?,?)",
                                       entity.getUuid(), entity.getEmail(), entity.getName() ) );
    }

    @Override
    public List<User> findAll()
            throws SQLFaultException {
        return runner.query( this, SQLQuery.of( "select * from USERS" ) );
    }

    @Override
    public Optional<User> find( String key )
            throws SQLFaultException {
        return runner.querySingle( this, SQLQuery.of( "select * from USERS where UUID=?", key ) );
    }

    // Other methods

}
```

```java
SQLiteDataSource ds  = new SQLiteDataSource();
ds.setEncoding( "UTF-8" );
ds.setUrl( "jdbc:sqlite:test.db" );

try (SQLTransaction tx = SQLTransaction.begin( ds )) {
    SQLRunner exec = new SQLRunner( tx );
    UserDAO userDAO = new UserDAOImpl( tx );

    // Create new table
    exec.execute( SQLQuery.of( "create table USERS (UUID char(36) primary key, NAME varchar(128) not null, EMAIL varchar(128) not null)" ) );

    // Insert new entity
    User user = User.newInstance( "john doe", "john@doe.com" );
    userDAO.addEntity( user );
}
```
