package y_lab.domain.repositories;

import y_lab.domain.entities.Habit;

import java.util.ArrayList;
import java.util.Optional;

public interface HabitRepository {
    Optional<Habit> findById(Long id);
    Optional<Habit> findByName(String name, Long userId);
    void save(Habit habit);
    void delete(Long id);
    void deleteAllByUserId(Long userId);
    Optional<ArrayList<Habit>> findHabitsByUserId(Long userId);
    ArrayList<Habit> getAll();
}
