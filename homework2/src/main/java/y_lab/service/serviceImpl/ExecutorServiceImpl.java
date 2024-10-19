package y_lab.service.serviceImpl;

import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.domain.Habit;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.ExecutorService;
import y_lab.service.NotificationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceImpl implements ExecutorService {

    private final HabitRepositoryImpl habitRepository;
    private final UserRepositoryImpl userRepository;
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ExecutorServiceImpl(HabitRepositoryImpl habitRepository, UserRepositoryImpl userRepository, NotificationService notificationService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            Frequency frequency;
            User user;
            try {
                for (Habit habit : habitRepository.getAll()) {
                    frequency = habit.getFrequency();
                    if (ChronoUnit.DAYS.between(LocalDate.now(), habit.getCreatedAt()) % (frequency.equals(Frequency.WEEKLY) ? 7 : 1) == 0) {
                        user = userRepository.findById(habit.getUserId()).orElseThrow(NoSuchElementException::new);
                        notificationService.sendNotification(user.getEmail(), habit.getName());
                    }
                }
            } catch (SQLException e) {
                System.out.println("Sql error in startScheduler");
            }
        }, 0, 1, TimeUnit.DAYS); // Check reminders every day
    }

    public void stopScheduler() {
        scheduler.shutdown(); // Stops accepting new tasks
        try {
            // Wait for currently running tasks to finish (up to 10 seconds)
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Force shutdown if tasks didn't finish in time
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow(); // Immediate shutdown on interruption
            Thread.currentThread().interrupt(); // Restore interruption flag
        }
    }
}