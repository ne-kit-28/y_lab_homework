package y_lab.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;


    @Bean
    public DataSource DataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5433/y_lab");
        dataSource.setUsername("y_lab_user");
        dataSource.setPassword("Y_lab_password");

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(3);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        return dataSource;
    }
}
