package y_lab.service;

/**
 * Interface representing the service for managing user progress related to habits.
 */
public interface ProgressService {

    /**
     * Creates a new progress entry for a user and a specific habit.
     *
     * @param userId  the ID of the user who completed the habit
     * @param habitId the ID of the habit that was completed
     */
    void createProgress(Long userId, Long habitId);

    /**
     * Generates and prints progress statistics for a specific habit over a given period.
     *
     * @param habitId the ID of the habit for which to generate statistics
     * @param period  the period over which to calculate statistics (e.g., "day", "week", "month")
     */
    void generateProgressStatistics(Long habitId, String period);

    /**
     * Calculates and prints the current and maximum streak of habit completions for a specific habit.
     *
     * @param habitId the ID of the habit for which to calculate the streak
     */
    void calculateStreak(Long habitId);

    /**
     * Generates a report that includes progress statistics and streak information for a specific habit over a given period.
     *
     * @param habitId the ID of the habit for which to generate the report
     * @param period  the period over which to generate the report (e.g., "day", "week", "month")
     */
    void generateReport(Long habitId, String period);
}
