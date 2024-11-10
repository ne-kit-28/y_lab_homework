package y_lab.service.serviceImpl;

import org.springframework.stereotype.Service;
import y_lab.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    public void sendNotification(String email, String habitName) {
        System.out.println("Reminder: " + email + ", it's time to complete your habit: " + habitName);
    }
}
