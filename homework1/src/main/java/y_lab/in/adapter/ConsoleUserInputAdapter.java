package y_lab.in.adapter;

import y_lab.domain.enums.Role;
import y_lab.domain.User;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.LoginServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;
import y_lab.service.serviceImpl.UserServiceImpl;
import y_lab.util.ConsoleMessages;

import java.util.Scanner;

public class ConsoleUserInputAdapter {
    private final HabitServiceImpl habitService;
    private final ProgressServiceImpl progressService;
    private final UserServiceImpl userService;
    private final LoginServiceImpl loginService;


    public ConsoleUserInputAdapter(
            HabitServiceImpl habitService
            , ProgressServiceImpl progressService
            , UserServiceImpl userService
            , LoginServiceImpl loginService) {
        this.habitService = habitService;
        this.loginService = loginService;
        this.userService = userService;
        this.progressService = progressService;
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
            System.out.println(ConsoleMessages.INPUT_MENU_OPTIONS);

            option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.println("Enter your username:");
                    name = scanner.nextLine();
                    System.out.println("Enter your email:");
                    email = scanner.nextLine();
                    System.out.println("Enter your password:");
                    password = scanner.nextLine();
                    loginService.register(name, email, password);
                    break;
                case "2":
                    System.out.println("Enter your email:");
                    email = scanner.nextLine();
                    System.out.println("Enter your password:");
                    password = scanner.nextLine();
                    user = loginService.login(email, password);
                    userId = user.getId();
                    if (userId != -1 && user.getRole() == Role.REGULAR) {
                        consoleLoginInputAdapter = new ConsoleLoginInputAdapter(
                                userService
                                , habitService
                                , progressService
                        );
                        consoleLoginInputAdapter.operations(userId);
                    } else if (userId != -1 && user.getRole() == Role.ADMINISTRATOR) {
                        consoleAdministratorInputAdapter = new ConsoleAdministratorInputAdapter(
                                userService
                                , habitService
                                , progressService
                        );
                        consoleAdministratorInputAdapter.options(userId);
                    }
                    break;
                case "3":
                    System.out.println("Enter your email:");
                    email = scanner.nextLine();
                    loginService.requestPasswordReset(email);
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
