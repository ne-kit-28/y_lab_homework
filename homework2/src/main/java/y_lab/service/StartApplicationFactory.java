package y_lab.service;

import y_lab.repository.DbConnectionFactory;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.serviceImpl.*;
import y_lab.util.DatabaseMigrator;

import java.sql.Connection;
import java.sql.SQLException;

public class StartApplicationFactory implements AutoCloseable{
    UserRepositoryImpl userRepository;
    HabitRepositoryImpl habitRepository;
    ProgressRepositoryImpl progressRepository;
    Connection connection;
    DatabaseMigrator databaseMigrator;

    public StartApplicationFactory() throws SQLException {
        DbConnectionFactory dbConnectionFactory = new DbConnectionFactory();
        connection = dbConnectionFactory.getConnection();
        databaseMigrator = new DatabaseMigrator(connection);
        this.userRepository = new UserRepositoryImpl(dbConnectionFactory.getConnection());
        this.habitRepository = new HabitRepositoryImpl(dbConnectionFactory.getConnection());
        this.progressRepository = new ProgressRepositoryImpl(dbConnectionFactory.getConnection());
    }

    public  ExecutorServiceImpl createExecutorService() {
        return new ExecutorServiceImpl(habitRepository, userRepository, new NotificationServiceImpl());
    }

    public HabitServiceImpl createHabitService() {
        return new HabitServiceImpl(habitRepository, progressRepository, connection);
    }

    public ProgressServiceImpl createProgressService() {
        return new ProgressServiceImpl(habitRepository, progressRepository, connection);
    }

    public UserServiceImpl createUserService() {
        return new UserServiceImpl(userRepository, habitRepository, progressRepository, connection);
    }

    public LoginServiceImpl createLoginService() {
        return new LoginServiceImpl(userRepository, connection);
    }

    public void executeMigration() {
        databaseMigrator.migrate();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
