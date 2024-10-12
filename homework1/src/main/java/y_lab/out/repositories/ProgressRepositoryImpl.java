package y_lab.out.repositories;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.entities.Progress;
import y_lab.domain.repositories.ProgressRepository;
import y_lab.service.DataService;

import java.io.*;
import java.util.*;

/**
 * Implementation of the {@link ProgressRepository} interface, handling
 * the persistence and retrieval of {@link Progress} objects using an
 * in-memory {@link HashMap}. It also provides functionality to save and
 * load progress data from a file.
 */
@Getter
@Setter
public class ProgressRepositoryImpl implements ProgressRepository, DataService {
    private HashMap<Long, Progress> progresses = new HashMap<>();
    private Long idGenerated = 0L;

    /**
     * Constructs a new {@code ProgressRepositoryImpl} and loads progress
     * data from a file.
     *
     * @param fileName the file from which progress data will be loaded
     */
    public ProgressRepositoryImpl(String fileName) {
        this.loadFromFile(fileName);
    }

    /**
     * Saves the specified progress object in the repository.
     * Automatically assigns an ID to the progress.
     *
     * @param progress the progress object to save
     */
    @Override
    public void save(Progress progress) {
        progress.setId(idGenerated);
        progresses.put(idGenerated, progress);
        ++idGenerated;
    }

    /**
     * Deletes all progress records associated with a given habit ID.
     *
     * @param habitId the ID of the habit for which to delete progress records
     */
    @Override
    public void deleteAllByHabitId(Long habitId) {
        List<Long> keysToDelete = new ArrayList<>();

        for (Map.Entry<Long, Progress> entry : progresses.entrySet()) {
            if (entry.getValue().getHabit().getId().equals(habitId)) {
                keysToDelete.add(entry.getKey());
            }
        }

        for (Long key : keysToDelete) {
            progresses.remove(key);
        }
    }

    /**
     * Deletes all progress records associated with a given user ID.
     *
     * @param userId the ID of the user for which to delete progress records
     */
    @Override
    public void deleteAllByUserId(Long userId) {
        List<Long> keysToDelete = new ArrayList<>();

        for (Map.Entry<Long, Progress> entry : progresses.entrySet()) {
            if (entry.getValue().getUser().getId().equals(userId)) {
                keysToDelete.add(entry.getKey());
            }
        }

        for (Long key : keysToDelete) {
            progresses.remove(key);
        }
    }

    /**
     * Finds a progress record by its ID.
     *
     * @param progressId the ID of the progress record to find
     * @return an {@code Optional} containing the found progress, or empty if not found
     */
    @Override
    public Optional<Progress> findById(Long progressId) {
        return Optional.ofNullable(progresses.get(progressId));
    }

    /**
     * Finds all progress records associated with a given habit ID.
     *
     * @param habitId the ID of the habit for which to find progress records
     * @return an {@code ArrayList} containing all matching progress records
     */
    @Override
    public ArrayList<Progress> findByHabitId(Long habitId) {
        ArrayList<Progress> arrayList = new ArrayList<>();
        for (Map.Entry<Long, Progress> entry : progresses.entrySet()) {
            if (entry.getValue().getHabit().getId().equals(habitId)) {
                arrayList.add(entry.getValue());
            }
        }
        return arrayList;
    }

    /**
     * Saves the current state of the progress repository to a file.
     * This includes all progress records and the ID generation counter.
     *
     * @param fileName the file to which data will be saved
     */
    @Override
    public void saveToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this.getProgresses());
            out.writeObject(this.getIdGenerated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the state of the progress repository from a file.
     * This includes all progress records and the ID generation counter.
     *
     * @param fileName the file from which data will be loaded
     */
    @Override
    public void loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.setProgresses((HashMap<Long, Progress>) in.readObject());
            this.setIdGenerated((Long) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
