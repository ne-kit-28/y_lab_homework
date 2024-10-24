package y_lab.in.adapter;

import y_lab.domain.User;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;
import y_lab.service.serviceImpl.UserServiceImpl;
import y_lab.util.ConsoleMessages;
import y_lab.util.HashFunction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleAdministratorInputAdapter {
    private final UserServiceImpl userService;
    private final HabitServiceImpl habitService;
    private final ProgressServiceImpl progressService;

    public ConsoleAdministratorInputAdapter(
            UserServiceImpl userService
            , HabitServiceImpl habitService
            , ProgressServiceImpl progressService) {
        this.userService = userService;
        this.habitService = habitService;
        this.progressService = progressService;
    }


    void options(Long userId) {
        Scanner scanner = new Scanner(System.in);
        ConsoleHabitManagementInputAdapter consoleHabitManagementInputAdapter;

        String status;
        String option;
        String buffer;
        String userEmail;
        ArrayList<User> users;
        Optional<User> user;
        Long otherUserId;

        while (true) {
            System.out.println(ConsoleMessages.ADMINISTRATION_MENU_OPTIONS);
            option = scanner.nextLine();
            switch (option) {
                case "1": {
                    System.out.println("Enter your user's email if you know (or press Enter to skip):");
                    userEmail = scanner.nextLine();
                    if (!userEmail.isEmpty()) {
                        user = userService.getUser(userEmail);
                        if (user.isEmpty()) {
                            System.out.println("No such user!");
                            break;
                        }
                    } else {
                        users = userService.getUsers(); //print
                        if (users.isEmpty())
                            break;
                        System.out.println("Enter your user's email if you know (or press Enter to return back):");
                        userEmail = scanner.nextLine();
                        if (!userEmail.isEmpty()) {
                            user = userService.getUser(userEmail);
                            if (user.isEmpty()) {
                                System.out.println("No such user!");
                                break;
                            }
                        } else
                            break;
                    }
                    otherUserId = user.get().getId();
                    USER:
                    while (true) {
                        System.out.println("Select option:");
                        System.out.println("1. Habits management");
                        System.out.println("2. Edit user");
                        System.out.println("3. Delete user");
                        System.out.println("4. Block user");
                        System.out.println("5. previous menu");
                        option = scanner.nextLine();

                        switch (option) {
                            case "1":
                                consoleHabitManagementInputAdapter = new ConsoleHabitManagementInputAdapter(
                                        habitService
                                        , progressService
                                );
                                consoleHabitManagementInputAdapter.operations(otherUserId);
                                break;
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
                                userService.editUser(otherUserId, newUser);
                                break;
                            case "3":
                                System.out.println("are you sure you want to delete this account?");
                                System.out.println("enter \"YES\" if you want to delete the account");
                                buffer = scanner.nextLine();
                                if (buffer.equals("YES")) {
                                    userService.deleteUser(otherUserId);
                                    break USER;
                                }
                                System.out.println("not confirmed");
                                break;
                            case "4":
                                System.out.println("What status should be set? \n1. block \n2. unblock?");
                                status = scanner.nextLine();
                                if (status.equalsIgnoreCase("1"))
                                    userService.blockUser(otherUserId, true);
                                else if (status.equalsIgnoreCase("2"))
                                    userService.blockUser(otherUserId, false);
                                else
                                    System.out.println("incorrect input");
                                break;
                            case "5":
                                break USER;
                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                    }
                    break;
                }
                case "2":
                    System.out.println("Enter new name (or press Enter to skip): ");
                    String newName = scanner.nextLine();

                    System.out.println("Enter new password (or press Enter to skip): ");
                    String newPassword = scanner.nextLine();
                    User newUser = new User();
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
