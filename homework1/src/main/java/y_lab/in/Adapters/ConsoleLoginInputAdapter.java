package y_lab.in.Adapters;

import y_lab.usecases.*;

import java.util.Scanner;

public class ConsoleLoginInputAdapter {
    private final EditUserUseCase editUserUseCase;
    private final GetHabitsUseCase getHabitsUseCase;
    private final CreateHabitUseCase createHabitUseCase;
    private final UpdateHabitUseCase updateHabitUseCase;
    private final DeleteHabitUseCase deleteHabitUseCase;
    private final CreateProgressUseCase createProgressUseCase;
    private final GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase;
    private final ProgressReportUseCase progressReportUseCase;
    private final StreakCalculationUseCase streakCalculationUseCase;

    ConsoleLoginInputAdapter(EditUserUseCase editUserUseCase,
                             GetHabitsUseCase getHabitsUseCase,
                             CreateHabitUseCase createHabitUseCase,
                             UpdateHabitUseCase updateHabitUseCase,
                             DeleteHabitUseCase deleteHabitUseCase,
                             CreateProgressUseCase createProgressUseCase,
                             GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase,
                             ProgressReportUseCase progressReportUseCase,
                             StreakCalculationUseCase streakCalculationUseCase) {
        this.editUserUseCase = editUserUseCase;
        this.getHabitsUseCase = getHabitsUseCase;
        this.updateHabitUseCase = updateHabitUseCase;
        this.createHabitUseCase = createHabitUseCase;
        this.deleteHabitUseCase = deleteHabitUseCase;
        this.createProgressUseCase = createProgressUseCase;
        this.generateProgressStatisticsUseCase = generateProgressStatisticsUseCase;
        this.progressReportUseCase = progressReportUseCase;
        this.streakCalculationUseCase = streakCalculationUseCase;
    }

    void operations(Long userId) {
        Scanner scanner = new Scanner(System.in);
        ConsoleHabitManagementInputAdapter consoleHabitManagementInputAdapter;

        String option;
        String buffer;

        while (true) {
            System.out.println("Select option:");
            System.out.println("1. Habits management");
            System.out.println("2. Edit profile");
            System.out.println("3. Delete account");
            System.out.println("4. Log out");
            option = scanner.nextLine();
            switch (option) {
                case "1": {
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
                    editUserUseCase.editUser(userId, newName, newEmail, newPassword);
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
