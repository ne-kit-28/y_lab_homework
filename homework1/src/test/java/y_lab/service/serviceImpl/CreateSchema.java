package y_lab.service.serviceImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateSchema {
    public static void createSchema(Connection connection) throws SQLException {
        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS domain;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.progress_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.user_id_seq;" +
                        "CREATE SEQUENCE IF NOT EXISTS domain.habit_id_seq;"
        ).execute();

        String createUserTable = "CREATE SCHEMA IF NOT EXISTS domain;" +
                "CREATE SEQUENCE IF NOT EXISTS domain.user_id_seq;" +
                "CREATE TABLE IF NOT EXISTS domain.users (" +
                "id BIGINT PRIMARY KEY DEFAULT nextval('domain.user_id_seq'), " +
                "email VARCHAR(255), " +
                "password_hash VARCHAR(255), " +
                "name VARCHAR(255), " +
                "is_block BOOLEAN, " +
                "role VARCHAR(50), " +
                "reset_token VARCHAR(255));" +

                "CREATE TABLE IF NOT EXISTS domain.habits (" +
                "id BIGINT PRIMARY KEY DEFAULT nextval('domain.habit_id_seq'), " +
                "user_id BIGINT, " +
                "name VARCHAR(64), " +
                "description VARCHAR(128), " +
                "frequency VARCHAR(16), " +
                "created_at VARCHAR(32), " +
                "FOREIGN KEY (user_id) REFERENCES domain.users(id));" +

                "CREATE TABLE IF NOT EXISTS domain.progresses (" +
                "id BIGINT PRIMARY KEY DEFAULT nextval('domain.progress_id_seq'), " +
                "user_id BIGINT, " +
                "habit_id BIGINT, " +
                "date VARCHAR(32), " +
                "FOREIGN KEY (habit_id) REFERENCES domain.habits(id), " +
                "FOREIGN KEY (user_id) REFERENCES domain.users(id));";
        connection.createStatement().execute(createUserTable);

        connection.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS service;" +
                        "CREATE TABLE IF NOT EXISTS service.admins (" +
                        "id BIGINT PRIMARY KEY, " +
                        "email VARCHAR(255));"
        ).execute();
    }
}
