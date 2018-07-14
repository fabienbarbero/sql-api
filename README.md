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


## Use of DAO
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
        runner.execute( new SQLQueryBuilder( "insert into USERS (UUID, EMAIL, NAME) values (?,?,?)",
                                             entity.getUuid(), entity.getEmail(), entity.getName() ) );
    }

    @Override
    public List<User> findAll()
            throws SQLFaultException {
        return runner.query( this, new SQLQueryBuilder( "select * from USERS" ) );
    }

    @Override
    public Optional<User> find( String key )
            throws SQLFaultException {
        return runner.querySingle( this, new SQLQueryBuilder( "select * from USERS where UUID=?", key ) );
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
    exec.execute( new SQLQueryBuilder( "create table USERS (UUID char(36) primary key, NAME varchar(128) not null, EMAIL varchar(128) not null)" ) );

    // Insert new entity
    User user = User.newInstance( "john doe", "john@doe.com" );
    userDAO.addEntity( user );
}
```


## Database migrations

You can also migrate your database. To do this, you must use the MigrationManager class. The migration can be executed using specific modes:
* LIVE_BEFORE: when the server is running, but before stopping it for a normal migration.
               This can be useful for migrating tables without locking them or "pre-heating" the database
* NORMAL: when the server is stopped. The operation which locks the database should be done here
* LIVE_AFTER: when the server is running, but after the normal migration is done

Migrators classes must be registered in the manager. There are easy to implements. It contains three methods to implements if needed:
* migrateLiveBefore: when executing the LIVE_BEFORE migration
* migrateNormal : when executing the NORMAL migration
* migrateLiveAfter : when executing the LIVE_AFTER migration

Each of these method has a "context" parameter. It only centralize the useful objects to process the migration

Here is a simple usage:
```java
private static class CreateTableMigrator extends Migrator {

    public CreateTableMigrator() {
        super( "create-table" );
    }

    @Override
    protected void migrateNormal( MigrationContext context ) throws Exception {
        SQLRunner runner = context.getRunner();
        runner.execute( new SQLQueryBuilder(
                "create table USERS (UUID varchar(36) primary key, NAME varchar(128) not null)") );
    }
}
```

```java
MigrationManager manager = new MigrationManager( dataSource );

// Register the migrators
manager.register(new CreateTableMigrator());
manager.register(new FillTableMigrator());

// Execute the migration in "normal" mode
manager.execute( MigrationManager.Mode.NORMAL );
```