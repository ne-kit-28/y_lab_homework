package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.Progress;
import y_lab.repository.ProgressRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class ProgressRepositoryImpl implements ProgressRepository{
    private final Connection connection;

    public ProgressRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Progress progress) throws SQLException {
        String sql =
                "INSERT INTO domain.progresses (id, user_id, habit_id, date) " +
                        "VALUES (nextval('domain.progress_id_seq'), ?, ?, ?)"; //вызов nextval - явное использование Sequence

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, progress.getUserId());
            stmt.setLong(2, progress.getHabitId());
            stmt.setString(3, progress.getDate().toString());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllByHabitId(Long habitId) throws SQLException {
        String sql =
                "DELETE FROM domain.progresses WHERE habit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, habitId);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllByUserId(Long userId) throws SQLException {
        String sql =
                "DELETE FROM domain.progresses WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Progress> findById(Long progressId) throws SQLException {
        String sql =
                "SELECT * FROM domain.progresses " +
                        "WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, progressId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                        new Progress(
                                progressId
                                , resultSet.getLong(2)
                                , resultSet.getLong(3)
                                , LocalDate.parse(resultSet.getString(4))));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public ArrayList<Progress> findByHabitId(Long habitId) throws SQLException {
        ArrayList<Progress> progresses = new ArrayList<>();

        String sql =
                "SELECT * FROM domain.progresses " +
                        "WHERE habit_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, habitId);
            ResultSet resultSet = stmt.executeQuery();



            while (resultSet.next()) {
                Progress progress = new Progress();

                progress.setId(resultSet.getLong(1));
                progress.setUserId(resultSet.getLong(2));
                progress.setHabitId(resultSet.getLong(3));
                progress.setDate(LocalDate.parse(resultSet.getString(4)));

                progresses.add(progress);
            }
        }
        return progresses;
    }
}
