package y_lab.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import y_lab.audit_logging_spring_boot_starter.annotation.Auditable;
import y_lab.domain.Habit;
import y_lab.domain.Progress;
import y_lab.domain.enums.Frequency;
import y_lab.repository.HabitRepository;
import y_lab.repository.ProgressRepository;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.service.ProgressService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProgressServiceImpl implements ProgressService {
    private final HabitRepository habitRepository;
    private final ProgressRepository progressRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    @Autowired
    public ProgressServiceImpl(
            HabitRepositoryImpl habitRepository
            , ProgressRepositoryImpl progressRepository
            ) throws SQLException {
        this.habitRepository = habitRepository;
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
    @Transactional
    @Auditable
    public boolean createProgress(Long habitId) {
        try {
            Optional<Habit> habit = habitRepository.findById(habitId);
            if (habit.isEmpty())
                return false;

            Progress progress = Progress.builder()
                    .userId(habit.get().getUserId())
                    .habitId(habitId)
                    .date(LocalDate.now())
                    .build();
            progressRepository.save(progress);

            logger.info("The habit is complete");
            return true;
        } catch (SQLException e) {
            logger.info("SQL error in ProgressServiceImpl:createProgress");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @Auditable
    public String generateProgressStatistics(Long habitId, String period) {

        String returnStr = "";

        try {
            Habit habit = habitRepository.findById(habitId).orElseThrow(NoSuchElementException::new);
            Frequency frequency = habit.getFrequency();

            // Determine the period for statistics generation
            LocalDate startDate = calculateStartDate(period);
            LocalDate endDate = LocalDate.now();
            long totalDays = (Math.abs(ChronoUnit.DAYS.between(startDate, endDate)) + 1) / (frequency == Frequency.WEEKLY ? 7 : 1);

            ArrayList<Progress> progressList = progressRepository.findByHabitId(habitId);
            if (progressList.isEmpty()) {
                returnStr += "Habit: " + habit.getName();
                returnStr += "\nPeriod: " + period;
                returnStr += "\nCompleted: 0 out of " + totalDays + " times.";
                returnStr += "\nCompletion rate: 0%";
                return returnStr;
            }

            ArrayList<Progress> filteredProgresses = new ArrayList<>(progressList
                    .stream()
                    .filter(progress -> progress.getDate().isAfter(startDate))
                    .toList());

            long completedDays = filteredProgresses.size();

            returnStr += "Habit: " + habit.getName();
            returnStr += "\nPeriod: " + period;
            returnStr += "\nCompleted: " + completedDays + " out of " + totalDays + " times.";
            returnStr += "\nCompletion rate: " + (completedDays * 100 / (totalDays == 0 ? 1 : totalDays)) + "%";

            System.out.println(returnStr);
        } catch (SQLException e) {
            logger.info("SQL error in ProgressServiceImpl:generateProgressStatistics");
            returnStr = "Sql error in generateProgressStatistic";
        } catch (NoSuchElementException e) {
            logger.info("no habit with such id error in ProgressServiceImpl:generateProgressStatistics");
            returnStr = "no habit with such id";
        }
        return returnStr;
    }

    @Override
    @Auditable
    public String calculateStreak(Long habitId) {

        String returnStr = "";

        try {

            Habit habit = habitRepository.findById(habitId).orElseThrow(NoSuchElementException::new);

            ArrayList<Progress> progressList = progressRepository.findByHabitId(habitId);
            if (progressList.isEmpty()) {
                returnStr += "Current streak: 0 days.";
                returnStr += "\nMax streak: 0 days.";
                return returnStr;
            }

            progressList = new ArrayList<>(progressList.stream()
                    .sorted(Comparator.comparing(Progress::getDate))
                    .toList());

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

            returnStr += "Current streak: " + streak + " days.";
            returnStr += "\nMax streak: " + maxStreak + " days.";

            System.out.println(returnStr);
        } catch (SQLException e) {
            logger.info("SQL error in ProgressServiceImpl:calculateStreak");
            returnStr = "Sql error in calculateStreak";
        } catch (NoSuchElementException e) {
            logger.info("no habit with such id error in ProgressServiceImpl:calculateStreak");
            returnStr = "no habit with such id";
        }
        return  returnStr;
    }

    @Override
    @Auditable
    public String generateReport(Long habitId, String period) {

        String statistic = this.generateProgressStatistics(habitId, period);

        String streak = this.calculateStreak(habitId);

        System.out.println(statistic + '\n' + streak);
        return (statistic + '\n' + streak);
    }
}
