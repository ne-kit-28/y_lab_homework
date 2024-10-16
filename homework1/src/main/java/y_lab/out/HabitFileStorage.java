package y_lab.out;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.Habit;

import java.io.*;
import java.util.HashMap;

@Getter
@Setter
public class HabitFileStorage {
    private HashMap<Long, Habit> habits;
    private Long idGenerated;

    public HabitFileStorage() {
        this.habits = new HashMap<>();
        this.idGenerated = 0L;
    }

    public void saveToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(habits);
            out.writeObject(idGenerated);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.habits = (HashMap<Long, Habit>) in.readObject();
            this.idGenerated = (Long) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
