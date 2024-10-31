package y_lab.util;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import y_lab.service.serviceImpl.HabitServiceImpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class responsible for managing database migrations using Liquibase.
 */
@Component
public class DatabaseMigrator implements ApplicationListener<ContextRefreshedEvent> {
    private final DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(HabitServiceImpl.class);

    /**
     * Constructs a DatabaseMigrator with the specified database connection.
     *
     * @param dataSource the dataSource to be used for migrations
     */
    @Autowired
    public DatabaseMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes the migration process using Liquibase.
     * This method updates the database schema based on the changes defined in the
     * Liquibase changelog file.
     */
    public void migrate() {
        Database database;
        try (Connection connection = dataSource.getConnection()) {
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            logger.info("Migrations failed");
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        migrate();
    }
}
