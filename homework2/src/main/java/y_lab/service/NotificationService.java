package y_lab.service;

/**
 * Service for sending notifications to users.
 * This class handles the logic for sending reminders related to habits.
 */
public interface NotificationService {

    /**
     * Sends a notification to a user reminding them to complete a specific habit.
     *
     * @param email the email address of the user to whom the notification is sent
     * @param habitName the name of the habit that the user is being reminded to complete
     */
    void sendNotification(String email, String habitName);
}
