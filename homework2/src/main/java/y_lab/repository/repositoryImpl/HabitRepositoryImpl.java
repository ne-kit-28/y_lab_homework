package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;
import y_lab.repository.HabitRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
public class HabitRepositoryImpl implements HabitRepository {

    private final Connection connection;

    public HabitRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Habit> findById(Long id) throws SQLException {

        String sql =
                "SELECT * FROM domain.habits " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                        new Habit(
                                id
                                , resultSet.getLong("user_id")
                                , resultSet.getString("name")
                                , resultSet.getString("description")
                                , Frequency.fromString(resultSet.getString("frequency"))
                                , LocalDate.parse(resultSet.getString("created_at"))));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Habit> findByName(String name, Long userId) throws SQLException{
        String sql =
                "SELECT * FROM domain.habits " +
                        "WHERE name = ? AND user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(2, userId);
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                        new Habit(
                                resultSet.getLong("id")
                                , userId
                                , name
                                , resultSet.getString("description")
                                , Frequency.fromString(resultSet.getString("frequency"))
                                , LocalDate.parse(resultSet.getString("created_at"))));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void save(Habit habit) throws SQLException{
        String sql =
                "INSERT INTO domain.habits (id, user_id, name, description, frequency, created_at) " +
                        "VALUES (nextval('domain.habit_id_seq'), ?, ?, ?, ?, ?)"; //вызов nextval - явное использование Sequence

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, habit.getUserId());
            stmt.setString(2, habit.getName());
            stmt.setString(3, habit.getDescription());
            stmt.setString(4, habit.getFrequency().getValue());
            stmt.setString(5, habit.getCreatedAt().toString());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException{
        String sql =
                "DELETE FROM domain.habits WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllByUserId(Long userId) throws SQLException{
        String sql =
                "DELETE FROM domain.habits WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<ArrayList<Habit>> findHabitsByUserId(Long userId) throws SQLException{
        ArrayList<Habit> habits = new ArrayList<>();

        String sql =
                "SELECT * FROM domain.habits " +
                        "WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Habit habit = new Habit();

                habit.setId(resultSet.getLong(1));
                habit.setUserId(resultSet.getLong(2));
                habit.setName(resultSet.getString(3));
                habit.setDescription(resultSet.getString(4));
                habit.setFrequency(Frequency.fromString(resultSet.getString(5)));
                habit.setCreatedAt(LocalDate.parse(resultSet.getString(6)));

                habits.add(habit);
            }
        }
        return Optional.of(habits);
    }

    @Override
    public ArrayList<Habit> getAll() throws SQLException{
        ArrayList<Habit> habits = new ArrayList<>();

        String sql =
                "SELECT * FROM domain.habits";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Habit habit = new Habit();

                habit.setId(resultSet.getLong(1));
                habit.setUserId(resultSet.getLong(2));
                habit.setName(resultSet.getString(3));
                habit.setDescription(resultSet.getString(4));
                habit.setFrequency(Frequency.fromString(resultSet.getString(5)));
                habit.setCreatedAt(LocalDate.parse(resultSet.getString(6)));

                habits.add(habit);
            }
        }
        return habits;
    }

    @Override
    public void update(Long id, Habit habit) throws SQLException{
        String sql = "UPDATE domain.habits SET user_id = ?, name = ?, description = ?, frequency = ?, created_at = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(6, habit.getId());
            stmt.setLong(1, habit.getUserId());
            stmt.setString(2, habit.getName());
            stmt.setString(3, habit.getDescription());
            stmt.setString(4, habit.getFrequency().getValue());
            stmt.setString(5, habit.getCreatedAt().toString());

            stmt.executeUpdate();
        }
    }
}
