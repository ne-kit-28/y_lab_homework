package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.User;
import y_lab.out.UserFileStorage;
import y_lab.repository.UserRepository;

import java.util.*;

@Getter
@Setter
public class UserRepositoryImpl implements UserRepository {
    private HashMap<Long, User> users = new HashMap<>();
    private ArrayList<String> adminEmails = new ArrayList<>();
    private Long idGenerated = 0L;
    private UserFileStorage userFileStorage;

    public UserRepositoryImpl(String fileName, String admins) {
        this.userFileStorage = new UserFileStorage();
        this.userFileStorage.loadFromFile(fileName);
        this.users = userFileStorage.getUsers();
        this.idGenerated = userFileStorage.getIdGenerated();

        this.userFileStorage.loadAdminsFromFile(admins);
        this.adminEmails = userFileStorage.getAdminEmails();
    }

    @Override
    public boolean isEmailExist(String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getValue().getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAdminEmail(String email) {
        return adminEmails.contains(email);
    }

    @Override
    public void save(User user) {
        user.setId(idGenerated);
        users.put(idGenerated, user);
        ++idGenerated;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getValue().getEmail(), email)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public void update(Long id, User user) {
        if (users.containsKey(id)) {
            users.replace(id, user);
        }
    }

    public void saveAdmins(String adminsFile) {
        this.userFileStorage.setAdminEmails(adminEmails);
        this.userFileStorage.saveAdmins(adminsFile);
    }

    public void saveToFile(String fileName) {
        this.userFileStorage.setUsers(users);
        this.userFileStorage.setIdGenerated(idGenerated);
        this.userFileStorage.saveToFile(fileName);
    }
}
