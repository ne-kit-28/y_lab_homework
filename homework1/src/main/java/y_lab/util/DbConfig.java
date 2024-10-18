package y_lab.util;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class DbConfig {
    private String url;
    private String user;
    private String password;

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
