package y_lab.in.Adapters;

import y_lab.domain.entities.Role;
import y_lab.domain.entities.User;
import y_lab.service.ExecutorService;
import y_lab.usecases.*;

import java.util.Scanner;

public class ConsoleUserInputAdapter {
    private final CreateHabitUseCase createHabitUseCase;
    private final CreateProgressUseCase createProgressUseCase;
    private final DeleteHabitUseCase deleteHabitUseCase;
    private final EditUserUseCase editUserUseCase;
    private final GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase;
    private final GetHabitsUseCase getHabitsUseCase;
    private final LoginUseCase loginUseCase;
    private final PasswordResetUseCase passwordResetUseCase;
    private final ProgressReportUseCase progressReportUseCase;
    private final RegistrationUseCase registrationUseCase;
    private final StreakCalculationUseCase streakCalculationUseCase;
    private final UpdateHabitUseCase updateHabitUseCase;
    private final GetUsersUseCase getUsersUseCase;

    public ConsoleUserInputAdapter(CreateHabitUseCase createHabitUseCase, CreateProgressUseCase createProgressUseCase
            , DeleteHabitUseCase deleteHabitUseCase, EditUserUseCase editUserUseCase
            , GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase, GetHabitsUseCase getHabitsUseCase
            , LoginUseCase loginUseCase, PasswordResetUseCase passwordResetUseCase
            , ProgressReportUseCase progressReportUseCase, RegistrationUseCase registrationUseCase
            , StreakCalculationUseCase streakCalculationUseCase, UpdateHabitUseCase updateHabitUseCase, GetUsersUseCase getUsersUseCase) {
        this.createHabitUseCase = createHabitUseCase;
        this.createProgressUseCase = createProgressUseCase;
        this.deleteHabitUseCase = deleteHabitUseCase;
        this.editUserUseCase = editUserUseCase;
        this.generateProgressStatisticsUseCase = generateProgressStatisticsUseCase;
        this.getHabitsUseCase = getHabitsUseCase;
        this.loginUseCase = loginUseCase;
        this.passwordResetUseCase = passwordResetUseCase;
        this.progressReportUseCase = progressReportUseCase;
        this.registrationUseCase = registrationUseCase;
        this.streakCalculationUseCase = streakCalculationUseCase;
        this.updateHabitUseCase = updateHabitUseCase;
        this.getUsersUseCase = getUsersUseCase;
    }

    public void start(){

        Scanner scanner = new Scanner(System.in);
        ConsoleLoginInputAdapter consoleLoginInputAdapter;
        ConsoleAdministratorInputAdapter consoleAdministratorInputAdapter;

        User user;
        String option;
        String name;
        String email;
        String password;
        Long userId;

        // Основное меню
        while (true) {
            System.out.println("Select option:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Request Password Reset");
            System.out.println("4. Exit");

            option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.println("Enter your username:");
                    name = scanner.nextLine();
                    System.out.println("Enter your email:");
                    email = scanner.nextLine();
                    System.out.println("Enter your password:");
                    password = scanner.nextLine();
                    registrationUseCase.register(name, email, password);
                    break;
                case "2":
                    System.out.println("Enter your email:");
                    email = scanner.nextLine();
                    System.out.println("Enter your password:");
                    password = scanner.nextLine();
                    user = loginUseCase.login(email, password);
                    userId = user.getId();
                    if (userId != -1 && user.getRole() == Role.REGULAR) {
                        consoleLoginInputAdapter = new ConsoleLoginInputAdapter(
                                editUserUseCase
                                , getHabitsUseCase
                                , createHabitUseCase
                                , updateHabitUseCase
                                , deleteHabitUseCase
                                , createProgressUseCase
                                , generateProgressStatisticsUseCase
                                , progressReportUseCase
                                , streakCalculationUseCase);
                        consoleLoginInputAdapter.operations(userId);
                    } else if (userId != -1 && user.getRole() == Role.ADMINISTRATOR) {
                        consoleAdministratorInputAdapter = new ConsoleAdministratorInputAdapter(
                                editUserUseCase
                                , getUsersUseCase
                                , getHabitsUseCase
                                , createHabitUseCase
                                , updateHabitUseCase
                                , deleteHabitUseCase
                                , createProgressUseCase
                                , generateProgressStatisticsUseCase
                                , progressReportUseCase
                                ,streakCalculationUseCase
                        );
                        consoleAdministratorInputAdapter.options(userId);
                    }
                    break;
                case "3":
                    System.out.println("Enter your email:");
                    email = scanner.nextLine();
                    passwordResetUseCase.requestPasswordReset(email);
                    break;
                case "4":
                    System.out.println("Exiting the program...");

                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
