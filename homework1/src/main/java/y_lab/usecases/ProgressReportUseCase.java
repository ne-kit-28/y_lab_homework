package y_lab.usecases;

/**
 * Use case for generating progress reports for habits.
 * This class combines the functionality of generating progress statistics
 * and calculating streaks for a given habit over a specified period.
 */
public class ProgressReportUseCase {
    private final GenerateProgressStatisticsUseCase generateStatisticsUseCase;
    private final StreakCalculationUseCase streakCalculationUseCase;

    /**
     * Constructs a new {@code ProgressReportUseCase} instance with the specified use cases.
     *
     * @param generateStatisticsUseCase the use case for generating progress statistics
     * @param streakCalculationUseCase   the use case for calculating streaks
     */
    public ProgressReportUseCase(GenerateProgressStatisticsUseCase generateStatisticsUseCase,
                                 StreakCalculationUseCase streakCalculationUseCase) {
        this.generateStatisticsUseCase = generateStatisticsUseCase;
        this.streakCalculationUseCase = streakCalculationUseCase;
    }

    /**
     * Generates a progress report for a specified habit over a given period.
     * This includes generating progress statistics and calculating the streak for the habit.
     *
     * @param habitId the ID of the habit for which the report is generated
     * @param period  the period for generating statistics, which can be "day", "week", or "month"
     */
    public void generateReport(Long habitId, String period) { // String {"day", "week", "month"}
        // Generate progress statistics
        generateStatisticsUseCase.generateProgressStatistics(habitId, period);

        // Calculate streak
        streakCalculationUseCase.calculateStreak(habitId);

        System.out.println("Report generated successfully.");
    }
}
