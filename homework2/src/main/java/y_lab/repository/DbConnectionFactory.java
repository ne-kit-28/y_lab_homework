package y_lab.repository;

import y_lab.util.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A factory for creating database connections.
 */
public class DbConnectionFactory {
    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructs a DbConnectionFactory and initializes the database connection
     * parameters based on the configuration provided by the {@link DbConfig} class.
     */
    public DbConnectionFactory() {
        DbConfig dbConfig = new DbConfig();
        this.url = dbConfig.getUrl();
        this.user = dbConfig.getUser();
        this.password = dbConfig.getPassword();
    }

    /**
     * Obtains a new connection to the database.
     *
     * @return a new {@link Connection} to the database.
     * @throws SQLException if an error occurs while creating the connection.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
