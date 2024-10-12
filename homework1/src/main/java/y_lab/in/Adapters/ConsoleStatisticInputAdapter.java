package y_lab.in.Adapters;

import y_lab.usecases.*;

import java.util.Scanner;

public class ConsoleStatisticInputAdapter {

    GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase;
    ProgressReportUseCase progressReportUseCase;
    StreakCalculationUseCase streakCalculationUseCase;

    public ConsoleStatisticInputAdapter(GenerateProgressStatisticsUseCase generateProgressStatisticsUseCase
            , ProgressReportUseCase progressReportUseCase
            , StreakCalculationUseCase streakCalculationUseCase) {

        this.generateProgressStatisticsUseCase = generateProgressStatisticsUseCase;
        this.progressReportUseCase = progressReportUseCase;
        this.streakCalculationUseCase = streakCalculationUseCase;
    }

    void operaions(Long habitId) {
        Scanner scanner = new Scanner(System.in);

        String option;
        String period;

        while (true) {
            System.out.println("Select option:");
            System.out.println("1. See streaks");
            System.out.println("2. See statistic");
            System.out.println("3. Generate full report");
            System.out.println("4. previous menu");
            option = scanner.nextLine();
            switch (option) {
                case "1":
                    streakCalculationUseCase.calculateStreak(habitId);
                    break;
                case "2":
                    System.out.println("Enter period: day, week or month");
                    period = scanner.nextLine();
                    if (period.equalsIgnoreCase("day") || period.equalsIgnoreCase("week")
                            || period.equalsIgnoreCase("month"))
                        generateProgressStatisticsUseCase.generateProgressStatistics(habitId, period);
                    else
                        System.out.println("Incorrect input");
                    break;
                case "3":
                    System.out.println("Enter period: day, week or month");
                    period = scanner.nextLine();
                    if (period.equalsIgnoreCase("day") || period.equalsIgnoreCase("week")
                            || period.equalsIgnoreCase("month"))
                        progressReportUseCase.generateReport(habitId, period);
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
