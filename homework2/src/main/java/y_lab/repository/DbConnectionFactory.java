package y_lab.repository;

import y_lab.util.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionFactory {
    private final String url;
    private final String user;
    private final String password;

    public DbConnectionFactory() {
        DbConfig dbConfig = new DbConfig();
        this.url = dbConfig.getUrl();
        this.user = dbConfig.getUser();
        this.password = dbConfig.getPassword();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
