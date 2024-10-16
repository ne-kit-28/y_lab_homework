package y_lab.service.serviceImpl;

import y_lab.domain.Habit;
import y_lab.domain.Progress;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.ProgressService;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ProgressServiceImpl implements ProgressService {
    private final HabitRepositoryImpl habitRepository;
    private final UserRepositoryImpl userRepository;
    private final ProgressRepositoryImpl progressRepository;

    public ProgressServiceImpl(HabitRepositoryImpl habitRepository,
                                 UserRepositoryImpl userRepository,
                                 ProgressRepositoryImpl progressRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
    }

    private LocalDate calculateStartDate(String period) {
        LocalDate today = LocalDate.now();
        return switch (period.toLowerCase()) {
            case "day" -> today.minusDays(1);
            case "week" -> today.minusWeeks(1);
            case "month" -> today.minusMonths(1);
            default -> throw new IllegalArgumentException("Unsupported period: " + period);
        };
    }

    @Override
    public void createProgress(Long userId, Long habitId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        Habit habit = habitRepository.findById(habitId).orElseThrow(NoSuchElementException::new);
        Progress progress = Progress.builder()
                .user(user)
                .habit(habit)
                .date(LocalDate.now())
                .build();
        progressRepository.save(progress);
        System.out.println("The habit is complete");
    }

    @Override
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

    @Override
    public void calculateStreak(Long habitId) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(NoSuchElementException::new);

        ArrayList<Progress> progressList = progressRepository.findByHabitId(habitId);
        if (progressList.isEmpty()) {
            System.out.println("Current streak: 0 days.");
            System.out.println("Max streak: 0 days.");
            return;
        }

        progressList = new ArrayList<>(progressList.stream()
                .sorted(Comparator.comparing(Progress::getDate))
                .toList());

        // Calculate current streak
        int streak = 1;
        int maxStreak = 1;
        for (int i = 1; i < progressList.size(); ++i) {
            if (Period.between(progressList.get(i).getDate(), progressList.get(i - 1).getDate()).getDays()
                    <= (habit.getFrequency().equals(Frequency.WEEKLY) ? 7 : 1)) {
                ++streak;
            } else if (streak > maxStreak) {
                maxStreak = streak;
                streak = 1;
            }
        }
        if (streak > maxStreak) {
            maxStreak = streak;
        }

        System.out.println("Current streak: " + streak + " days.");
        System.out.println("Max streak: " + maxStreak + " days.");
    }

    @Override
    public void generateReport(Long habitId, String period) { // String {"day", "week", "month"}
        // Generate progress statistics
        this.generateProgressStatistics(habitId, period);

        // Calculate streak
        this.calculateStreak(habitId);

        System.out.println("Report generated successfully.");
    }
}
