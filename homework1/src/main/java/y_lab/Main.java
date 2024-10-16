package y_lab;

import y_lab.in.adapter.ConsoleUserInputAdapter;
import y_lab.service.serviceImpl.*;
import y_lab.util.Factory;

public class Main {
    public static void main(String[] args) {

        Factory factory = new Factory();

        HabitServiceImpl habitService = factory.createHabitService();
        ProgressServiceImpl progressService = factory.createProgressService();
        UserServiceImpl userService = factory.createUserService();
        LoginServiceImpl loginService = factory.createLoginService();
        ExecutorServiceImpl executorService = factory.createExecutorService();

        ConsoleUserInputAdapter inputAdapter = new ConsoleUserInputAdapter(
                habitService
                , progressService
                , userService
                , loginService
        );

        executorService.startScheduler();
        inputAdapter.start();
        executorService.stopScheduler();

        factory.saveData();
    }
}