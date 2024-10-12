package y_lab.service;

/**
 * Service for sending notifications to users.
 * This class handles the logic for sending reminders related to habits.
 */
public class NotificationService {

    /**
     * Sends a notification to a user reminding them to complete a specific habit.
     *
     * @param email the email address of the user to whom the notification is sent
     * @param habitName the name of the habit that the user is being reminded to complete
     */
    public void sendNotification(String email, String habitName) {
        System.out.println("Reminder: " + email + ", it's time to complete your habit: " + habitName);
    }
}
