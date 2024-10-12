package y_lab.domain.repositories;

import y_lab.domain.entities.User;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository {
    boolean isEmailExist(String email);
    boolean isAdminEmail(String email);
    void save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    ArrayList<User> getAll();
    void deleteById(Long id);
    void update(Long id, User user);
    void loadAdminsFromFile(String adminsFile);
    void saveAdmins(String adminsFile);
}
