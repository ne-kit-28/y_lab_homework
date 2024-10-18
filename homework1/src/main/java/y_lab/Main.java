package y_lab;

import y_lab.in.adapter.ConsoleUserInputAdapter;
import y_lab.service.serviceImpl.*;
import y_lab.repository.DbConnectionFactory;
import y_lab.service.StartApplicationFactory;

public class Main {
    public static void main(String[] args) {

        try {
            StartApplicationFactory startApplicationFactory = new StartApplicationFactory();

            startApplicationFactory.executeMigration();

            HabitServiceImpl habitService = startApplicationFactory.createHabitService();
            ProgressServiceImpl progressService = startApplicationFactory.createProgressService();
            UserServiceImpl userService = startApplicationFactory.createUserService();
            LoginServiceImpl loginService = startApplicationFactory.createLoginService();
            ExecutorServiceImpl executorService = startApplicationFactory.createExecutorService();

            ConsoleUserInputAdapter inputAdapter = new ConsoleUserInputAdapter(
                    habitService
                    , progressService
                    , userService
                    , loginService
            );

            executorService.startScheduler();
            inputAdapter.start();
            executorService.stopScheduler();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}