package y_lab.service.serviceImpl;

import y_lab.domain.Habit;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.HabitService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HabitServiceImpl implements HabitService {
    private final HabitRepositoryImpl habitRepository;
    private final ProgressRepositoryImpl progressRepository;
    private final Connection connection;

    public HabitServiceImpl(HabitRepositoryImpl habitRepository
            , ProgressRepositoryImpl progressRepository
            , Connection connection) {
        this.habitRepository = habitRepository;
        this.progressRepository = progressRepository;
        this.connection = connection;
    }

    @Override
    public void createHabit(Long userId, String name, String description, Frequency frequency) {

        try {
            connection.setAutoCommit(false);

            if (habitRepository.findByName(name, userId).isPresent()) {
                System.out.println("Habit with such name exists");
                System.out.println("Habit is not created");
            } else {
                Habit habit = new Habit(name, description, frequency, LocalDate.now());
                habit.setUserId(userId);
                habitRepository.save(habit);
                System.out.println("Habit " + name + " is created!");
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void deleteHabit(Long id) {
        try {
            connection.setAutoCommit(false);

            habitRepository.delete(id);
            progressRepository.deleteAllByHabitId(id);
            System.out.println("Habit with id: " + id + " was deleted!");

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Habit> getHabits(Long userId, Object filter) {

        ArrayList<Habit> habits = new ArrayList<>();

        try {
            connection.setAutoCommit(false);

            habits = habitRepository.findHabitsByUserId(userId).orElseThrow(NoSuchElementException::new);

            if (filter instanceof String) {
                habits = new ArrayList<>(habits.stream()
                        .sorted(Comparator.comparing(Habit::getCreatedAt))
                        .toList());
            } else if (filter instanceof Frequency instanceFilter) {
                habits = new ArrayList<>(habits.stream()
                        .filter(habit -> habit.getFrequency().equals(instanceFilter))
                        .toList());
            }

            if (habits.isEmpty()) {
                System.out.println("No habits");
            } else {
                for (Habit habit : habits) {
                    System.out.println("Name: " + habit.getName());
                    System.out.println("Description: " + habit.getDescription());
                    System.out.println("Created at: " + habit.getCreatedAt());
                    System.out.println("Frequency: " + habit.getFrequency().toString());
                    System.out.println();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return  habits;
    }

    @Override
    public Long getHabit(String habitName, Long userId) {

        Long habitId = -1L;

        try {
            connection.setAutoCommit(false);

            Optional<Habit> habit = habitRepository.findByName(habitName, userId);
            if (habit.isPresent()) {
                System.out.println("Name: " + habit.get().getName());
                System.out.println("Description: " + habit.get().getDescription());
                System.out.println("Created at: " + habit.get().getCreatedAt());
                System.out.println("Frequency: " + habit.get().getFrequency().toString());
                habitId =  habit.get().getId();
            } else
                System.out.println("No such habit");

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return habitId;
    }

    @Override
    public void updateHabit(Long id, String newName, String newDescription, Frequency newFrequency) {

        try {
            connection.setAutoCommit(false);

            Optional<Habit> habit = habitRepository.findById(id);

            if (habit.isEmpty()) {
                System.out.println("Habit with this id does not exist!");
                throw new SQLException();
            }

            // Check for the uniqueness of the new name
            if (newName != null && !newName.isEmpty() && habitRepository.findByName(newName, habit.get().getUserId()).isPresent()) {
                System.out.println("Name already in use by another account!");
                throw new SQLException();
            }

            if (newName != null && !newName.isEmpty()) {
                habit.get().setName(newName);
            }
            if (newDescription != null && !newDescription.isEmpty()) {
                habit.get().setDescription(newDescription);
            }
            if (newFrequency != null) {
                habit.get().setFrequency(newFrequency);
            }

            habitRepository.update(id, habit.get());
            System.out.println("Habit updated successfully!");

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
