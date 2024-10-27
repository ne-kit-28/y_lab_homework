package y_lab.util;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

/**
 * Class responsible for managing database migrations using Liquibase.
 */
public class DatabaseMigrator {
    private final Connection connection; // Database connection

    /**
     * Constructs a DatabaseMigrator with the specified database connection.
     *
     * @param connection the database connection to be used for migrations
     */
    public DatabaseMigrator(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes the migration process using Liquibase.
     * This method updates the database schema based on the changes defined in the
     * Liquibase changelog file.
     */
    public void migrate() {
        Database database;
        try {
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException e) {
            e.printStackTrace();
        }
    }
}
