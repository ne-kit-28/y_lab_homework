package y_lab.util;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for database connection parameters.
 * It loads database configuration properties from the 'application.properties' file.
 */
@Getter
public class DbConfig {
    private String url;      // Database URL
    private String user;     // Database user
    private String password; // Database password

    /**
     * Constructor that loads the database configuration from the properties file.
     */
    public DbConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("properties not found");
                return;
            }

            properties.load(input);

            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");

        } catch (IOException ex) {
            ex.printStackTrace(); //TODO logs
        }
    }
}
