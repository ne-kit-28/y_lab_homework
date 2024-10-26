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
    public Long createHabit(Long userId, Habit habit) {

        Long habitId = -1L;

        try {
            connection.setAutoCommit(false);

            if (habitRepository.findByName(habit.getName(), userId).isPresent()) {
                System.out.println("Habit with such name exists");
                System.out.println("Habit is not created");
            } else {
                habit.setCreatedAt(LocalDate.now());
                habit.setUserId(userId);
                habitRepository.save(habit);
                habitId = habitRepository.findByName(habit.getName(), userId).get().getId();
                System.out.println("Habit " + habit.getName() + " is created!");
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
        return habitId;
    }

    @Override
    public boolean deleteHabit(Long id) {

        boolean del = false;

        try {
            connection.setAutoCommit(false);

            habitRepository.delete(id);
            progressRepository.deleteAllByHabitId(id);
            System.out.println("Habit with id: " + id + " was deleted!");

            connection.commit();
            del = true;
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
        return del;
    }

    @Override
    public ArrayList<Habit> getHabits(Long userId, String filter) {

        ArrayList<Habit> habits = new ArrayList<>();

        try {
            connection.setAutoCommit(false);

            habits = habitRepository.findHabitsByUserId(userId).orElseThrow(NoSuchElementException::new);

            if (filter.equalsIgnoreCase("weekly")) {
                habits = new ArrayList<>(habits.stream()
                        .filter(habit -> habit.getFrequency().equals(Frequency.WEEKLY))
                        .toList());
            } else if (filter.equalsIgnoreCase("daily")) {
                habits = new ArrayList<>(habits.stream()
                        .filter(habit -> habit.getFrequency().equals(Frequency.DAILY))
                        .toList());
            } else
                habits = new ArrayList<>(habits.stream()
                        .sorted(Comparator.comparing(Habit::getCreatedAt))
                        .toList());

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
    public Optional<Habit> getHabit(String habitName, Long userId) {

        try {
            connection.setAutoCommit(false);

            Optional<Habit> habit = habitRepository.findByName(habitName, userId);
            if (habit.isPresent()) {
                System.out.println("Name: " + habit.get().getName());
                System.out.println("Description: " + habit.get().getDescription());
                System.out.println("Created at: " + habit.get().getCreatedAt());
                System.out.println("Frequency: " + habit.get().getFrequency().toString());
            } else
                System.out.println("No such habit");

            connection.commit();
            return habit;
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
        return Optional.empty();
    }

    @Override
    public boolean updateHabit(Long id, Habit newHabit) {

        boolean upd = false;

        try {
            connection.setAutoCommit(false);

            Optional<Habit> habit = habitRepository.findById(id);

            if (habit.isEmpty()) {
                System.out.println("Habit with this id does not exist!");
                return false;
            }

            // Check for the uniqueness of the new name
            if (newHabit.getName() != null && !newHabit.getName().isEmpty() && habitRepository.findByName(newHabit.getName(), habit.get().getUserId()).isPresent()) {
                System.out.println("Name already in use by another account!");
                return false;
            }

            if (newHabit.getName() != null && !newHabit.getName().isEmpty()) {
                habit.get().setName(newHabit.getName());
            }
            if (newHabit.getDescription() != null && !newHabit.getDescription().isEmpty()) {
                habit.get().setDescription(newHabit.getDescription());
            }
            if (newHabit.getFrequency() != null) {
                habit.get().setFrequency(newHabit.getFrequency());
            }

            habitRepository.update(id, habit.get());
            System.out.println("Habit updated successfully!");

            connection.commit();
            upd = true;
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
        return upd;
    }
}
