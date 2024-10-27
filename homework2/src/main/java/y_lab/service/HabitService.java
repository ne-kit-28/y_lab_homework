package y_lab.service;

import y_lab.domain.Habit;
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface representing the service for managing habits.
 */
public interface HabitService {

    /**
     * Creates a new habit for a user.
     *
     * @param userId      the ID of the user creating the habit
     * @param habit       the Habit
     * @return            id of habit
     */
    @LogExecutionTime
    @AuditAction(action = "Создание привычки")
    Long createHabit(Long userId, Habit habit);

    /**
     * Deletes a habit by its ID.
     *
     * @param id the ID of the habit to be deleted
     * @return               update or not(true/false)
     */
    @LogExecutionTime
    @AuditAction(action = "Удаление привычки")
    boolean deleteHabit(Long id);

    /**
     * Retrieves a list of habits associated with a user, filtered by the specified criteria.
     *
     * @param userId the ID of the user whose habits are to be retrieved
     * @param filter  the filtering criteria, which can be a String (for sorting) or Frequency
     * @return a list of habits associated with the user
     */
    @LogExecutionTime
    @AuditAction(action = "Получение всех привычек пользователя")
    ArrayList<Habit> getHabits(Long userId, String filter);

    /**
     * Retrieves a habit by its name for a specific user.
     *
     * @param habitName the name of the habit to retrieve
     * @param userId    the ID of the user
     * @return the ID of the habit if found, otherwise -1
     */
    @LogExecutionTime
    @AuditAction(action = "Получение привычки по имени")
    Optional<Habit> getHabit(String habitName, Long userId);

    @LogExecutionTime
    @AuditAction(action = "Получение привычки по id")
    Optional<Habit> getHabit(Long habitId);
    /**
     * Updates the details of an existing habit.
     *
     * @param id             the ID of the habit to update
     * @param habit       the Habit
     * @return               update or not(true/false)
     */
    @LogExecutionTime
    @AuditAction(action = "Обновление информаци о привычке")
    boolean updateHabit(Long id, Habit habit);
}
