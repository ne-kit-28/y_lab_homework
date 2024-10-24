package y_lab.in.adapter;

import y_lab.domain.User;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;
import y_lab.service.serviceImpl.UserServiceImpl;
import y_lab.util.ConsoleMessages;
import y_lab.util.HashFunction;

import java.util.Scanner;

public class ConsoleLoginInputAdapter {
    private final UserServiceImpl userService;
    private final HabitServiceImpl habitService;
    private final ProgressServiceImpl progressService;

    ConsoleLoginInputAdapter(
            UserServiceImpl userService
            , HabitServiceImpl habitService
            , ProgressServiceImpl progressService) {
        this.userService = userService;
        this.habitService = habitService;
        this.progressService = progressService;
    }

    void operations(Long userId) {
        Scanner scanner = new Scanner(System.in);
        ConsoleHabitManagementInputAdapter consoleHabitManagementInputAdapter;

        String option;
        String buffer;

        while (true) {
            System.out.println(ConsoleMessages.LOGIN_MENU_OPTIONS);
            option = scanner.nextLine();
            switch (option) {
                case "1": {
                    consoleHabitManagementInputAdapter = new ConsoleHabitManagementInputAdapter(
                            habitService
                            , progressService
                    );
                    consoleHabitManagementInputAdapter.operations(userId);
                    break;
                }
                case "2":
                    System.out.println("Enter new name (or press Enter to skip): ");
                    String newName = scanner.nextLine();

                    System.out.println("Enter new email (or press Enter to skip): ");
                    String newEmail = scanner.nextLine();

                    System.out.println("Enter new password (or press Enter to skip): ");
                    String newPassword = scanner.nextLine();
                    User newUser = new User();
                    newUser.setEmail(newEmail);
                    newUser.setPasswordHash(HashFunction.hashPassword(newPassword));
                    newUser.setName(newName);
                    userService.editUser(userId, newUser);
                    break;
                case "3":
                    System.out.println("are you sure you want to delete your account?");
                    System.out.println("enter \"YES\" if you want to delete the account");
                    buffer = scanner.nextLine();
                    if (buffer.equals("YES")) {
                        userService.deleteUser(userId);
                        return;
                    }
                    System.out.println("not confirmed");
                    break;
                case "4":
                    System.out.println("log out succeed");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
