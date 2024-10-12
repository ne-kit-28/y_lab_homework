package y_lab.service;

import y_lab.domain.entities.Frequency;
import y_lab.domain.entities.Habit;
import y_lab.out.repositories.HabitRepositoryImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for scheduling notifications for habits.
 * This class manages the scheduling of notifications based on the frequency of habits.
 */
public class ExecutorService {
    private final HabitRepositoryImpl habitRepository;
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructs an ExecutorService with the specified habit repository and notification service.
     *
     * @param habitRepository the repository for managing habits
     * @param notificationService the service for sending notifications
     */
    public ExecutorService(HabitRepositoryImpl habitRepository, NotificationService notificationService) {
        this.habitRepository = habitRepository;
        this.notificationService = notificationService;
    }

    /**
     * Starts the scheduler to send notifications for habits based on their frequency.
     * Notifications are sent daily based on the habit's frequency.
     */
    public void startScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            Frequency frequency;
            for (Habit habit : habitRepository.getAll()) {
                frequency = habit.getFrequency();
                if (ChronoUnit.DAYS.between(LocalDate.now(), habit.getCreatedAt()) % (frequency.equals(Frequency.WEEKLY) ? 7 : 1) == 0) {
                    notificationService.sendNotification(habit.getUser().getEmail(), habit.getName());
                }
            }
        }, 0, 1, TimeUnit.DAYS); // Check reminders every day
    }

    /**
     * Stops the scheduler, preventing new tasks from being scheduled.
     * Waits for currently running tasks to complete or forces shutdown if they exceed a timeout.
     */
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
