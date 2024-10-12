package y_lab;

import y_lab.in.Adapters.ConsoleUserInputAdapter;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;
import y_lab.out.repositories.UserRepositoryImpl;
import y_lab.service.ExecutorService;
import y_lab.service.NotificationService;
import y_lab.usecases.*;


public class Main {
    public static void main(String[] args) {


        UserRepositoryImpl userRepository = new UserRepositoryImpl("Users.ser", "Admins.ser");
        HabitRepositoryImpl habitRepository = new HabitRepositoryImpl("Habits.ser");
        ProgressRepositoryImpl progressRepository = new ProgressRepositoryImpl("Progress.ser");

        ExecutorService executorService = new ExecutorService(habitRepository, new NotificationService());

        CreateHabitUseCase createHabitUseCase = new CreateHabitUseCase(habitRepository, userRepository);
        CreateProgressUseCase createProgressUseCase = new CreateProgressUseCase(habitRepository, userRepository, progressRepository);
        DeleteHabitUseCase deleteHabitUseCase = new DeleteHabitUseCase(habitRepository,progressRepository);
        EditUserUseCase editUserUseCase = new EditUserUseCase(userRepository,habitRepository, progressRepository);
        GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase = new GenerateProgressStatisticsUseCase(progressRepository, habitRepository);
        GetHabitsUseCase getHabitsUseCase = new GetHabitsUseCase(habitRepository);
        LoginUseCase loginUseCase = new LoginUseCase(userRepository);
        PasswordResetUseCase passwordResetUseCase = new PasswordResetUseCase(userRepository);
        StreakCalculationUseCase streakCalculationUseCase = new StreakCalculationUseCase(progressRepository, habitRepository);
        ProgressReportUseCase progressReportUseCase = new ProgressReportUseCase(generateProgressStatisticsUseCase, streakCalculationUseCase);
        RegistrationUseCase registrationUseCase = new RegistrationUseCase(userRepository);
        UpdateHabitUseCase updateHabitUseCase = new UpdateHabitUseCase(habitRepository);
        GetUsersUseCase getUsersUseCase = new GetUsersUseCase(userRepository);

        ConsoleUserInputAdapter inputAdapter = new ConsoleUserInputAdapter(
                createHabitUseCase
                , createProgressUseCase
                , deleteHabitUseCase
                , editUserUseCase
                , generateProgressStatisticsUseCase
                , getHabitsUseCase
                , loginUseCase
                , passwordResetUseCase
                , progressReportUseCase
                , registrationUseCase
                , streakCalculationUseCase
                , updateHabitUseCase
                , getUsersUseCase
        );

        executorService.startScheduler();
        inputAdapter.start();
        userRepository.saveToFile("Users.ser");
        //userRepository.saveAdmins("Admins.ser");
        habitRepository.saveToFile("Habits.ser");
        progressRepository.saveToFile("Progress.ser");
        executorService.stopScheduler();
    }
}