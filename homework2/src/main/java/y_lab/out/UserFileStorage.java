package y_lab.out;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class UserFileStorage {
    private HashMap<Long, User> users;
    private ArrayList<String> adminEmails;
    private Long idGenerated;

    public  UserFileStorage() {
        this.users = new HashMap<>();
        this.adminEmails = new ArrayList<>();
        this.idGenerated = 0L;
    }

    public void saveToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this.getUsers());
            out.writeObject(this.getIdGenerated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.setUsers((HashMap<Long, User>) in.readObject());
            this.setIdGenerated((Long) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadAdminsFromFile(String adminsFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(adminsFile))) {
            this.setAdminEmails((ArrayList<String>) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveAdmins(String adminsFile) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(adminsFile))) {
            out.writeObject(this.getAdminEmails());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
