package y_lab.in.Adapters;

import y_lab.domain.entities.User;
import y_lab.usecases.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleAdministratorInputAdapter {
    private final EditUserUseCase editUserUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final GetHabitsUseCase getHabitsUseCase;
    private final CreateHabitUseCase createHabitUseCase;
    private final UpdateHabitUseCase updateHabitUseCase;
    private final DeleteHabitUseCase deleteHabitUseCase;
    private final CreateProgressUseCase createProgressUseCase;
    private final GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase;
    private final ProgressReportUseCase progressReportUseCase;
    private final StreakCalculationUseCase streakCalculationUseCase;

    public ConsoleAdministratorInputAdapter(EditUserUseCase editUserUseCase, GetUsersUseCase getUsersUseCase, GetHabitsUseCase getHabitsUseCase, CreateHabitUseCase createHabitUseCase, UpdateHabitUseCase updateHabitUseCase, DeleteHabitUseCase deleteHabitUseCase, CreateProgressUseCase createProgressUseCase, GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase, ProgressReportUseCase progressReportUseCase, StreakCalculationUseCase streakCalculationUseCase) {
        this.editUserUseCase = editUserUseCase;
        this.getUsersUseCase = getUsersUseCase;
        this.getHabitsUseCase = getHabitsUseCase;
        this.createHabitUseCase = createHabitUseCase;
        this.updateHabitUseCase = updateHabitUseCase;
        this.deleteHabitUseCase = deleteHabitUseCase;
        this.createProgressUseCase = createProgressUseCase;
        this.generateProgressStatisticsUseCase = generateProgressStatisticsUseCase;
        this.progressReportUseCase = progressReportUseCase;
        this.streakCalculationUseCase = streakCalculationUseCase;
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
            System.out.println("Select option:");
            System.out.println("1. Get users");
            System.out.println("2. Edit profile");
            System.out.println("3. Delete account");
            System.out.println("4. Log out");
            option = scanner.nextLine();
            switch (option) {
                case "1": {
                    System.out.println("Enter your user's email if you know (or press Enter to skip):");
                    userEmail = scanner.nextLine();
                    if (!userEmail.isEmpty()) {
                        user = getUsersUseCase.getUser(userEmail);
                        if (user.isEmpty()) {
                            System.out.println("No such user!");
                            break;
                        }
                    } else {
                        users = getUsersUseCase.getUsers(); //print
                        if (users.isEmpty())
                            break;
                        System.out.println("Enter your user's email if you know (or press Enter to return back):");
                        userEmail = scanner.nextLine();
                        if (!userEmail.isEmpty()) {
                            user = getUsersUseCase.getUser(userEmail);
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
                                        getHabitsUseCase
                                        , createHabitUseCase
                                        , updateHabitUseCase
                                        , deleteHabitUseCase
                                        , createProgressUseCase
                                        , generateProgressStatisticsUseCase
                                        , progressReportUseCase
                                        , streakCalculationUseCase
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
                                editUserUseCase.editUser(otherUserId, newName, newEmail, newPassword);
                                break;
                            case "3":
                                System.out.println("are you sure you want to delete this account?");
                                System.out.println("enter \"YES\" if you want to delete the account");
                                buffer = scanner.nextLine();
                                if (buffer.equals("YES")) {
                                    editUserUseCase.deleteUser(otherUserId);
                                    break USER;
                                }
                                System.out.println("not confirmed");
                                break;
                            case "4":
                                System.out.println("What status should be set? \n1. block \n2. unblock?");
                                status = scanner.nextLine();
                                if (status.equalsIgnoreCase("1"))
                                    editUserUseCase.blockUser(otherUserId, true);
                                else if (status.equalsIgnoreCase("2"))
                                    editUserUseCase.blockUser(otherUserId, false);
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
                    editUserUseCase.editUser(userId, newName, "", newPassword);
                    break;
                case "3":
                    System.out.println("are you sure you want to delete your account?");
                    System.out.println("enter \"YES\" if you want to delete the account");
                    buffer = scanner.nextLine();
                    if (buffer.equals("YES")) {
                        editUserUseCase.deleteUser(userId);
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
