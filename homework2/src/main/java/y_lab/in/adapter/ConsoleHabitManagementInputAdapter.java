package y_lab.in.adapter;

import y_lab.domain.enums.Frequency;
import y_lab.domain.Habit;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;
import y_lab.util.ConsoleMessages;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleHabitManagementInputAdapter {
    private final HabitServiceImpl habitService;
    private final ProgressServiceImpl progressService;


    public ConsoleHabitManagementInputAdapter(HabitServiceImpl habitService, ProgressServiceImpl progressService) {
        this.habitService = habitService;
        this.progressService = progressService;
    }

    void operations(Long userId) {
        Scanner scanner = new Scanner(System.in);
        ConsoleStatisticInputAdapter consoleStatisticInputAdapter;

        String option;
        String name;
        String habitName;
        String description;
        String frequency;
        Habit myHabit;
        Optional<Habit> habit;
        Object filter;
        ArrayList<Habit> habits;

        while (true) {
            System.out.println(ConsoleMessages.HABIT_MENU_OPTIONS);
            option = scanner.nextLine();
            switch (option) {
                case "1":
                    System.out.println("Enter your habit's name:");
                    name = scanner.nextLine();
                    System.out.println("Enter your habit's description:");
                    description = scanner.nextLine();
                    System.out.println("Enter your habit's frequency: daily or weekly");

                    myHabit = Habit.builder()
                            .name(name)
                            .description(description)
                            .build();
                    frequency = scanner.nextLine();
                    if (frequency.equalsIgnoreCase("daily")) {
                        myHabit.setFrequency(Frequency.DAILY);
                        habitService.createHabit(userId, myHabit);
                    }
                    else if (frequency.equalsIgnoreCase("weekly")) {
                        myHabit.setFrequency(Frequency.DAILY);
                        habitService.createHabit(userId, myHabit);
                    }
                    else
                        System.out.println("Incorrect input");
                    break;
                case "2":
                    System.out.println("Enter your habit's name if you know (or press Enter to skip):");
                    habitName = scanner.nextLine();
                    if (!habitName.isEmpty()) {
                        habit = habitService.getHabit(habitName, userId);
                        if (habit.isEmpty()) {
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

                        habits = habitService.getHabits(userId, filter); //печать реализована в сервисе
                        if (habits.isEmpty())
                            break;
                        System.out.println("Enter your habit's name if you know (or press Enter to return back):");
                        habitName = scanner.nextLine();
                        if (!habitName.isEmpty()) {
                            habit = habitService.getHabit(habitName, userId);
                            if (habit.isEmpty()) {
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
                                progressService.createProgress(userId, habit.get().getId());
                                break;
                            case "2":
                                System.out.println("Enter new name (or press Enter to skip): ");
                                name = scanner.nextLine();
                                System.out.println("Enter new description (or press Enter to skip): ");
                                description = scanner.nextLine();

                                myHabit = Habit.builder()
                                        .name(name)
                                        .description(description)
                                        .build();

                                System.out.println("Enter new frequency: daily or weekly (or press Enter to skip): ");
                                frequency = scanner.nextLine();
                                if (frequency.equalsIgnoreCase("daily")) {
                                    myHabit.setFrequency(Frequency.DAILY);
                                    habitService.updateHabit(habit.get().getId(), myHabit);
                                }
                                else if (frequency.equalsIgnoreCase("weekly")) {
                                    myHabit.setFrequency(Frequency.WEEKLY);
                                    habitService.updateHabit(habit.get().getId(), myHabit);
                                }
                                else
                                    System.out.println("Incorrect input");

                                break;
                            case "3":
                                habitService.deleteHabit(habit.get().getId());
                                return;
                            case "4":
                                consoleStatisticInputAdapter = new ConsoleStatisticInputAdapter(
                                        progressService
                                );
                                consoleStatisticInputAdapter.operaions(habit.get().getId());
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
