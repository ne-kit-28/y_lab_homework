package y_lab.in.Adapters;

import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.usecases.*;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleHabitManagementInputAdapter {
    CreateHabitUseCase createHabitUseCase;
    GetHabitsUseCase getHabitsUseCase;
    UpdateHabitUseCase updateHabitUseCase;
    DeleteHabitUseCase deleteHabitUseCase;
    CreateProgressUseCase createProgressUseCase;
    GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase;
    ProgressReportUseCase progressReportUseCase;
    StreakCalculationUseCase streakCalculationUseCase;

    public ConsoleHabitManagementInputAdapter(GetHabitsUseCase getHabitsUseCase, CreateHabitUseCase createHabitUseCase
    , UpdateHabitUseCase updateHabitUseCase, DeleteHabitUseCase deleteHabitUseCase, CreateProgressUseCase createProgressUseCase
    , GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase, ProgressReportUseCase progressReportUseCase
    , StreakCalculationUseCase streakCalculationUseCase) {
        this.getHabitsUseCase = getHabitsUseCase;
        this.createHabitUseCase = createHabitUseCase;
        this.updateHabitUseCase = updateHabitUseCase;
        this.deleteHabitUseCase = deleteHabitUseCase;
        this.createProgressUseCase = createProgressUseCase;
        this.generateProgressStatisticsUseCase = generateProgressStatisticsUseCase;
        this.progressReportUseCase = progressReportUseCase;
        this.streakCalculationUseCase = streakCalculationUseCase;
    }

    void operations(Long userId) {
        Scanner scanner = new Scanner(System.in);
        ConsoleStatisticInputAdapter consoleStatisticInputAdapter;

        String option;
        String name;
        String habitName;
        String description;
        String frequency;
        Long habitId;
        Object filter;
        ArrayList<Habit> habits;

        while (true) {
            System.out.println("Select option:");
            System.out.println("1. Create habit");
            System.out.println("2. See habits"); // введите имя если знаете + сортровка
            System.out.println("3. previous menu");
            option = scanner.nextLine();
            switch (option) {
                case "1":
                    System.out.println("Enter your habit's name:");
                    name = scanner.nextLine();
                    System.out.println("Enter your habit's description:");
                    description = scanner.nextLine();
                    System.out.println("Enter your habit's frequency: daily or weekly");

                    frequency = scanner.nextLine();
                    if (frequency.equalsIgnoreCase("daily"))
                        createHabitUseCase.createHabit(userId, name, description, Frequency.DAILY);
                    else if (frequency.equalsIgnoreCase("weekly"))
                        createHabitUseCase.createHabit(userId, name, description, Frequency.WEEKLY);
                    else
                        System.out.println("Incorrect input");
                    break;
                case "2":
                    System.out.println("Enter your habit's name if you know (or press Enter to skip):");
                    habitName = scanner.nextLine();
                    if (!habitName.isEmpty()) {
                        habitId = getHabitsUseCase.getHabit(habitName, userId);
                        if (habitId == -1L) {
                            System.out.println("No such habit!");
                            break;
                        }
                    } else {
                        System.out.println("Enter type of sorting");
                        System.out.println("1. date of creating \n2. Daily first \n3. Weekly first");
                        option = scanner.nextLine();
                        switch (option){
                            case "1":
                                filter = "_";
                                break;
                            case "2":
                                filter = Frequency.DAILY;
                                break;
                            case "3":
                                filter = Frequency.WEEKLY;
                                break;
                            default:
                                System.out.println("Incorrect input");
                                System.out.println("The sorting will be by date");
                                filter = " ";
                        }

                        habits = getHabitsUseCase.getHabits(userId, filter);
                        if (habits.isEmpty())
                            break;
                        System.out.println("Enter your habit's name if you know (or press Enter to return back):");
                        habitName = scanner.nextLine();
                        if (!habitName.isEmpty()) {
                            habitId = getHabitsUseCase.getHabit(habitName, userId);
                            if (habitId == -1L) {
                                System.out.println("No such habit!");
                                break;
                            }
                        } else
                            break;
                    }
                    while (true) {
                        System.out.println("Select option:");
                        System.out.println("1. Mark the completion");
                        System.out.println("2. Edit habit");
                        System.out.println("3. Delete habit");
                        System.out.println("4. Get statistic");
                        System.out.println("5. previous menu");
                        option = scanner.nextLine();

                        switch (option) {
                            case "1":
                                createProgressUseCase.createProgress(userId, habitId);
                                break;
                            case "2":
                                System.out.println("Enter new name (or press Enter to skip): ");
                                name = scanner.nextLine();

                                System.out.println("Enter new description (or press Enter to skip): ");
                                description = scanner.nextLine();

                                System.out.println("Enter new frequency: daily or weekly (or press Enter to skip): ");
                                frequency = scanner.nextLine();
                                if (frequency.equalsIgnoreCase("daily"))
                                    updateHabitUseCase.updateHabit(habitId, name, description, Frequency.DAILY);
                                else if (frequency.equalsIgnoreCase("weekly"))
                                    updateHabitUseCase.updateHabit(habitId, name, description, Frequency.WEEKLY);
                                else
                                    System.out.println("Incorrect input");

                                break;
                            case "3":
                                deleteHabitUseCase.deleteHabit(habitId);
                                return;
                            case "4":
                                consoleStatisticInputAdapter = new ConsoleStatisticInputAdapter(
                                        generateProgressStatisticsUseCase
                                        , progressReportUseCase
                                        , streakCalculationUseCase
                                );
                                consoleStatisticInputAdapter.operaions(habitId);
                                break;
                            case "5":
                                return;
                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                    }
                case "3":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
