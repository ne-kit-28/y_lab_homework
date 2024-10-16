package y_lab.service;

/**
 * Service for scheduling notifications for habits.
 * This class manages the scheduling of notifications based on the frequency of habits.
 */
public interface ExecutorService {

    /**
     * Starts the scheduler to send notifications for habits based on their frequency.
     * Notifications are sent daily based on the habit's frequency.
     */
    public void startScheduler();

    /**
     * Stops the scheduler, preventing new tasks from being scheduled.
     * Waits for currently running tasks to complete or forces shutdown if they exceed a timeout.
     */
    public void stopScheduler();
}
