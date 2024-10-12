package y_lab.out.repositories;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.entities.User;
import y_lab.domain.repositories.UserRepository;
import y_lab.service.DataService;

import java.io.*;
import java.util.*;

/**
 * Implementation of the {@link UserRepository} interface, responsible for
 * persisting and managing {@link User} objects using an in-memory {@link HashMap}.
 * This class also handles saving and loading user and admin email data to/from files.
 */
@Getter
@Setter
public class UserRepositoryImpl implements UserRepository, DataService {
    private HashMap<Long, User> users = new HashMap<>();
    private ArrayList<String> adminEmails = new ArrayList<>();
    private Long idGenerated = 0L;

    /**
     * Constructs a new {@code UserRepositoryImpl} and loads user data from a file.
     * It also loads admin emails from a separate file.
     *
     * @param fileName the file containing user data
     * @param admins   the file containing admin email data
     */
    public UserRepositoryImpl(String fileName, String admins) {
        this.loadFromFile(fileName);
        this.loadAdminsFromFile(admins);
    }

    /**
     * Checks if an email already exists in the user repository.
     *
     * @param email the email to check
     * @return {@code true} if the email exists, {@code false} otherwise
     */
    @Override
    public boolean isEmailExist(String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getValue().getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an email belongs to an admin user.
     *
     * @param email the email to check
     * @return {@code true} if the email belongs to an admin, {@code false} otherwise
     */
    @Override
    public boolean isAdminEmail(String email) {
        return adminEmails.contains(email);
    }

    /**
     * Saves a new user in the repository and automatically assigns an ID.
     *
     * @param user the user to save
     */
    @Override
    public void save(User user) {
        user.setId(idGenerated);
        users.put(idGenerated, user);
        ++idGenerated;
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     * @return an {@code Optional} containing the found user, or empty if not found
     */
    @Override
    public Optional<User> findByEmail(String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getValue().getEmail(), email)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return an {@code Optional} containing the found user, or empty if not found
     */
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Retrieves all users in the repository.
     *
     * @return an {@code ArrayList} containing all users
     */
    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    /**
     * Updates an existing user by their ID.
     *
     * @param id   the ID of the user to update
     * @param user the updated user object
     */
    @Override
    public void update(Long id, User user) {
        if (users.containsKey(id)) {
            users.replace(id, user);
        }
    }

    /**
     * Loads the list of admin emails from a file.
     *
     * @param adminsFile the file containing the admin email list
     */
    @Override
    public void loadAdminsFromFile(String adminsFile) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(adminsFile))) {
            this.setAdminEmails((ArrayList<String>) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the list of admin emails to a file.
     *
     * @param adminsFile the file to which admin email data will be saved
     */
    @Override
    public void saveAdmins(String adminsFile) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(adminsFile))) {
            out.writeObject(this.getAdminEmails());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current state of the user repository to a file.
     * This includes all user data and the ID generation counter.
     *
     * @param fileName the file to which user data will be saved
     */
    @Override
    public void saveToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this.getUsers());
            out.writeObject(this.getIdGenerated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the state of the user repository from a file.
     * This includes all user data and the ID generation counter.
     *
     * @param fileName the file from which user data will be loaded
     */
    @Override
    public void loadFromFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.setUsers((HashMap<Long, User>) in.readObject());
            this.setIdGenerated((Long) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
