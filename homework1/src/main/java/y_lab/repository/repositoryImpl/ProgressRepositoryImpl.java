package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.Progress;
import y_lab.out.ProgressFileStorage;
import y_lab.repository.ProgressRepository;

import java.util.*;

@Getter
@Setter
public class ProgressRepositoryImpl implements ProgressRepository{
    private HashMap<Long, Progress> progresses = new HashMap<>();
    private Long idGenerated = 0L;
    private ProgressFileStorage progressFileStorage;

    public ProgressRepositoryImpl(String fileName) {
        this.progressFileStorage = new ProgressFileStorage();
        this.progressFileStorage.loadFromFile(fileName);
        this.idGenerated = progressFileStorage.getIdGenerated();
        this.progresses = progressFileStorage.getProgresses();
    }

    @Override
    public void save(Progress progress) {
        progress.setId(idGenerated);
        progresses.put(idGenerated, progress);
        ++idGenerated;
    }

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

    @Override
    public Optional<Progress> findById(Long progressId) {
        return Optional.ofNullable(progresses.get(progressId));
    }

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

    public void saveToFile(String fileName) {
        progressFileStorage.setProgresses(progresses);
        progressFileStorage.setIdGenerated(idGenerated);
        progressFileStorage.saveToFile(fileName);
    }


}
