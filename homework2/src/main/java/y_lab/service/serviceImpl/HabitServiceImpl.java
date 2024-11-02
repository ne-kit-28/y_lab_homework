package y_lab.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Service;
import y_lab.domain.Habit;
import y_lab.domain.User;
import y_lab.domain.enums.Frequency;
import y_lab.repository.HabitRepository;
import y_lab.repository.ProgressRepository;
import y_lab.repository.repositoryImpl.HabitRepositoryImpl;
import y_lab.repository.repositoryImpl.ProgressRepositoryImpl;
import y_lab.repository.repositoryImpl.UserRepositoryImpl;
import y_lab.service.HabitService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final ProgressRepository progressRepository;
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(HabitServiceImpl.class);

    @Autowired
    public HabitServiceImpl(HabitRepositoryImpl habitRepository
            , ProgressRepositoryImpl progressRepository
            , DataSource dataSource) throws SQLException {
        this.habitRepository = habitRepository;
        this.progressRepository = progressRepository;
        this.connection = dataSource.getConnection();
    }

    @Override
    public Long createHabit(Long userId, Habit habit) {

        Long habitId = -1L;

        try {
            connection.setAutoCommit(false);

            if (habitRepository.findByName(habit.getName(), userId).isPresent()) {
                logger.info("Habit with such name exists");
                logger.info("Habit is not created");
            } else {
                habit.setCreatedAt(LocalDate.now());
                habit.setUserId(userId);
                habitRepository.save(habit);
                habitId = habitRepository.findByName(habit.getName(), userId).get().getId();
                logger.info("Habit " + habit.getName() + " is created!");
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
            logger.info("Habit with id: " + id + " was deleted!");

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
                logger.info("No habits");
            } else {
                for (Habit habit : habits) {
                    logger.info("Name: " + habit.getName());
                    logger.info("Description: " + habit.getDescription());
                    logger.info("Created at: " + habit.getCreatedAt());
                    logger.info("Frequency: " + habit.getFrequency().toString() + '\n');
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
                logger.info("Name: " + habit.get().getName());
                logger.info("Description: " + habit.get().getDescription());
                logger.info("Created at: " + habit.get().getCreatedAt());
                logger.info("Frequency: " + habit.get().getFrequency().toString() + '\n');
            } else
                logger.info("No such habit");

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
    public Optional<Habit> getHabit(Long habitId) {
        try {
            connection.setAutoCommit(false);

            Optional<Habit> habit = habitRepository.findById(habitId);
            if (habit.isPresent()) {
                logger.info("Name: " + habit.get().getName());
                logger.info("Description: " + habit.get().getDescription());
                logger.info("Created at: " + habit.get().getCreatedAt());
                logger.info("Frequency: " + habit.get().getFrequency().toString() + '\n');
            } else
                logger.info("No such habit");

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
                logger.info("Habit with this id does not exist!");
                return false;
            }

            if (newHabit.getName() != null && !newHabit.getName().isEmpty() && habitRepository.findByName(newHabit.getName(), habit.get().getUserId()).isPresent()) {
                logger.info("Name already in use by another account!");
                return false;
            }

            habit.get().setName(newHabit.getName());
            habit.get().setDescription(newHabit.getDescription());
            habit.get().setFrequency(newHabit.getFrequency());

            habitRepository.update(id, habit.get());
            logger.info("Habit updated successfully!");

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
