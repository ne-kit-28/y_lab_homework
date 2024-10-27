package y_lab.in.adapter;

import y_lab.service.serviceImpl.ProgressServiceImpl;
import y_lab.util.ConsoleMessages;

import java.util.Scanner;

public class ConsoleStatisticInputAdapter {

    private final ProgressServiceImpl progressService;

    public ConsoleStatisticInputAdapter(ProgressServiceImpl progressService) {

        this.progressService = progressService;
    }

    void operaions(Long habitId) {
        Scanner scanner = new Scanner(System.in);

        String option;
        String period;

        while (true) {
            System.out.println(ConsoleMessages.STATISTIC_MENU_OPTIONS);
            option = scanner.nextLine();
            switch (option) {
                case "1":
                    progressService.calculateStreak(habitId);
                    break;
                case "2":
                    System.out.println("Enter period: day, week or month");
                    period = scanner.nextLine();
                    if (period.equalsIgnoreCase("day") || period.equalsIgnoreCase("week")
                            || period.equalsIgnoreCase("month"))
                        progressService.generateProgressStatistics(habitId, period);
                    else
                        System.out.println("Incorrect input");
                    break;
                case "3":
                    System.out.println("Enter period: day, week or month");
                    period = scanner.nextLine();
                    if (period.equalsIgnoreCase("day") || period.equalsIgnoreCase("week")
                            || period.equalsIgnoreCase("month"))
                        progressService.generateReport(habitId, period);
                    else
                        System.out.println("Incorrect input");
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
