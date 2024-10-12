package y_lab.out.repositories;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.entities.Habit;
import y_lab.domain.repositories.HabitRepository;
import y_lab.service.DataService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The HabitRepositoryImpl class is an implementation of the HabitRepository and DataService interfaces,
 * managing Habit entities and handling persistence operations.
 * This class is responsible for saving, retrieving, deleting, and loading habits from a file.
 */
@Setter
@Getter
public class HabitRepositoryImpl implements HabitRepository, DataService {

    /**
     * Stores habit entities using their ID as the key.
     */
    HashMap<Long, Habit> habits = new HashMap<>();

    /**
     * ID generator to assign unique IDs to each habit.
     */
    Long idGenerated = 0L;

    /**
     * Constructor that loads habits from the given file.
     *
     * @param fileName the name of the file from which to load the data
     */
    public HabitRepositoryImpl(String fileName) {
        this.loadFromFile(fileName);
    }

    /**
     * Finds a habit by its ID.
     *
     * @param id the ID of the habit to find
     * @return an Optional containing the habit if found, or empty if not found
     */
    @Override
    public Optional<Habit> findById(Long id) {
        return Optional.ofNullable(habits.get(id));
    }

    /**
     * Finds a habit by its name and the user ID it belongs to.
     *
     * @param name the name of the habit
     * @param userId the ID of the user who owns the habit
     * @return an Optional containing the habit if found, or empty if not found
     */
    @Override
    public Optional<Habit> findByName(String name, Long userId) {
        for (Map.Entry<Long, Habit> entry : habits.entrySet()) {
            if (entry.getValue().getName().equals(name) && entry.getValue().getUser().getId().equals(userId)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Saves a habit, assigning a new ID to it if necessary.
     *
     * @param habit the habit to save
     */
    @Override
    public void save(Habit habit) {
        habit.setId(idGenerated);
        habits.put(idGenerated, habit);
        ++idGenerated;
    }

    /**
     * Deletes a habit by its ID.
     *
     * @param id the ID of the habit to delete
     */
    @Override
    public void delete(Long id) {
        habits.remove(id);
    }

    /**
     * Deletes all habits associated with a specific user ID.
     *
     * @param userId the ID of the user whose habits will be deleted
     */
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

    /**
     * Finds all habits associated with a specific user ID.
     *
     * @param userId the ID of the user whose habits to find
     * @return an Optional containing an ArrayList of habits, or an empty list if no habits are found
     */
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

    /**
     * Retrieves all habits in the repository.
     *
     * @return an ArrayList of all habits
     */
    @Override
    public ArrayList<Habit> getAll() {
        return new ArrayList<>(habits.values());
    }

    /**
     * Saves the current habits and ID generator to a file for persistence.
     *
     * @param fileName the name of the file to save the data to
     */
    @Override
    public void saveToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this.getHabits());
            out.writeObject(this.getIdGenerated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the habits and ID generator from a file.
     *
     * @param fileName the name of the file to load the data from
     */
    @Override
    public void loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.setHabits((HashMap<Long, Habit>) in.readObject());
            this.setIdGenerated((Long) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
