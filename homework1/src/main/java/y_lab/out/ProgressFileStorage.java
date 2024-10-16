package y_lab.out;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.Progress;

import java.io.*;
import java.util.HashMap;

@Setter
@Getter
public class ProgressFileStorage {
    private HashMap<Long, Progress> progresses;
    private Long idGenerated;

    public ProgressFileStorage() {
        this.progresses = new HashMap<>();
        this.idGenerated = 0L;
    }

    public void saveToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this.getProgresses());
            out.writeObject(this.getIdGenerated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.setProgresses((HashMap<Long, Progress>) in.readObject());
            this.setIdGenerated((Long) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
