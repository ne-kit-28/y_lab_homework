package y_lab.service;

import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;

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
    @LogExecutionTime
    @AuditAction(action = "Отправка уведомления")
    void sendNotification(String email, String habitName);
}
