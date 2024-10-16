package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.Habit;
import y_lab.out.HabitFileStorage;
import y_lab.repository.HabitRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Setter
@Getter
public class HabitRepositoryImpl implements HabitRepository {
    HashMap<Long, Habit> habits = new HashMap<>();
    Long idGenerated = 0L;
    private HabitFileStorage habitFileStorage;

    public HabitRepositoryImpl(String fileName) {
        this.habitFileStorage = new HabitFileStorage();
        this.habitFileStorage.loadFromFile(fileName);
        this.habits = habitFileStorage.getHabits();
        this.idGenerated = habitFileStorage.getIdGenerated();
    }

    @Override
    public Optional<Habit> findById(Long id) {
        return Optional.ofNullable(habits.get(id));
    }

    @Override
    public Optional<Habit> findByName(String name, Long userId) {
        for (Map.Entry<Long, Habit> entry : habits.entrySet()) {
            if (entry.getValue().getName().equals(name) && entry.getValue().getUser().getId().equals(userId)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(Habit habit) {
        habit.setId(idGenerated);
        habits.put(idGenerated, habit);
        ++idGenerated;
    }

    @Override
    public void delete(Long id) {
        habits.remove(id);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        ArrayList<Long> keysToDelete = new ArrayList<>();
        for (Map.Entry<Long, Habit> entry : habits.entrySet()) {
            if (entry.getValue().getUser().getId().equals(userId)) {
                keysToDelete.add(entry.getKey());
            }
        }
        for (Long key : keysToDelete) {
            this.delete(key);
        }
    }

    @Override
    public Optional<ArrayList<Habit>> findHabitsByUserId(Long userId) {
        ArrayList<Habit> habitArrayList = new ArrayList<>();
        for (Map.Entry<Long, Habit> entry : habits.entrySet()) {
            if (entry.getValue().getUser().getId().equals(userId)) {
                habitArrayList.add(entry.getValue());
            }
        }
        return Optional.of(habitArrayList);
    }

    @Override
    public ArrayList<Habit> getAll() {
        return new ArrayList<>(habits.values());
    }

    public void saveToFile(String fileName) {
        habitFileStorage.setHabits(habits);
        habitFileStorage.setIdGenerated(idGenerated);
        habitFileStorage.saveToFile(fileName);
    }
}
