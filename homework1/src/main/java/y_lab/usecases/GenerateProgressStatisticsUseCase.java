package y_lab.usecases;

import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.domain.entities.Progress;
import y_lab.out.repositories.HabitRepositoryImpl;
import y_lab.out.repositories.ProgressRepositoryImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Use case for generating progress statistics for a specific habit.
 * This class calculates and displays the completion rate and other statistics
 * for a habit over a specified time period.
 */
public class GenerateProgressStatisticsUseCase {
    private final ProgressRepositoryImpl progressRepository;
    private final HabitRepositoryImpl habitRepository;

    /**
     * Constructs a new {@code GenerateProgressStatisticsUseCase} instance with the specified
     * progress and habit repositories.
     *
     * @param progressRepository the repository for managing progress records
     * @param habitRepository    the repository for managing habits
     */
    public GenerateProgressStatisticsUseCase(ProgressRepositoryImpl progressRepository, HabitRepositoryImpl habitRepository) {
        this.progressRepository = progressRepository;
        this.habitRepository = habitRepository;
    }

    /**
     * Generates and displays progress statistics for a specified habit over a given time period.
     * The statistics include the number of completed days and the completion rate.
     *
     * @param habitId the ID of the habit for which to generate statistics
     * @param period  the time period over which to calculate statistics; can be "day", "week", or "month"
     * @throws NoSuchElementException if the habit with the specified ID does not exist
     * @throws IllegalArgumentException if the specified period is not supported
     */
    public void generateProgressStatistics(Long habitId, String period) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(NoSuchElementException::new);
        Frequency frequency = habit.getFrequency();

        // Determine the period for statistics generation
        LocalDate startDate = calculateStartDate(period);
        LocalDate endDate = LocalDate.now();
        long totalDays = (ChronoUnit.DAYS.between(startDate, endDate) + 1) / (frequency == Frequency.WEEKLY ? 7 : 1);

        // Retrieve progress for the specified period
        ArrayList<Progress> progressList = progressRepository.findByHabitId(habitId);
        if (progressList.isEmpty()) {
            System.out.println("Habit: " + habit.getName());
            System.out.println("Period: " + period);
            System.out.println("Completed: 0 out of " + totalDays + " days.");
            System.out.println("Completion rate: 0%");
            return;
        }

        ArrayList<Progress> filteredProgresses = new ArrayList<>(progressList
                .stream()
                .filter(progress -> progress.getDate().isAfter(startDate))
                .toList());

        // Generate statistics
        long completedDays = filteredProgresses.size();

        System.out.println("Habit: " + habit.getName());
        System.out.println("Period: " + period);
        System.out.println("Completed: " + completedDays + " out of " + totalDays + " days.");
        System.out.println("Completion rate: " + (completedDays * 100 / totalDays) + "%");
    }

    /**
     * Calculates the start date for the statistics generation based on the specified period.
     *
     * @param period the time period to calculate the start date for
     * @return the calculated start date
     * @throws IllegalArgumentException if the specified period is not supported
     */
    private LocalDate calculateStartDate(String period) {
        LocalDate today = LocalDate.now();
        return switch (period.toLowerCase()) {
            case "day" -> today.minusDays(1);
            case "week" -> today.minusWeeks(1);
            case "month" -> today.minusMonths(1);
            default -> throw new IllegalArgumentException("Unsupported period: " + period);
        };
    }
}
