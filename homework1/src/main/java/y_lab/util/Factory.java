package y_lab.util;

import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.serviceImpl.*;

public class Factory {
    UserRepositoryImpl userRepository;
    HabitRepositoryImpl habitRepository;
    ProgressRepositoryImpl progressRepository;

    public Factory() {
        this.userRepository = new UserRepositoryImpl("Users.ser", "Admins.ser");
        this.habitRepository = new HabitRepositoryImpl("Habits.ser");
        this.progressRepository = new ProgressRepositoryImpl("Progress.ser");
    }

    public  ExecutorServiceImpl createExecutorService() {
        return new ExecutorServiceImpl(habitRepository, new NotificationServiceImpl());
    }

    public HabitServiceImpl createHabitService() {
        return new HabitServiceImpl(habitRepository, userRepository, progressRepository);
    }

    public ProgressServiceImpl createProgressService() {
        return new ProgressServiceImpl(habitRepository, userRepository, progressRepository);
    }

    public UserServiceImpl createUserService() {
        return new UserServiceImpl(userRepository, habitRepository, progressRepository);
    }

    public LoginServiceImpl createLoginService() {
        return new LoginServiceImpl(userRepository);
    }

    public void saveData() {
        userRepository.saveToFile("Users.ser");
        habitRepository.saveToFile("Habits.ser");
        progressRepository.saveToFile("Progress.ser");
    }
}
