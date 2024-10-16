package y_lab.service;

import y_lab.domain.User;

import java.util.ArrayList;
import java.util.Optional;

public interface UserService {

    public void editUser(Long id, String newName, String newEmail, String newPassword);

    public void blockUser(Long id, boolean block);

    public void deleteUser(Long id);

    public Optional<User> getUser(String email);

    public ArrayList<User> getUsers();
}
