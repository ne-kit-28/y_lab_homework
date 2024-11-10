package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import y_lab.domain.Progress;
import y_lab.repository.ProgressRepository;
import y_lab.repository.SqlScripts;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Repository
public class ProgressRepositoryImpl implements ProgressRepository{
    private final DataSource dataSource;

    @Autowired
    public ProgressRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Progress progress) throws SQLException {
        String sql = SqlScripts.PROGRESS_SAVE;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, progress.getUserId());
            stmt.setLong(2, progress.getHabitId());
            stmt.setString(3, progress.getDate().toString());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllByHabitId(Long habitId) throws SQLException {
        String sql = SqlScripts.PROGRESS_DELETE_ALL_BY_HABIT_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, habitId);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAllByUserId(Long userId) throws SQLException {
        String sql = SqlScripts.PROGRESS_DELETE_ALL_BY_USER_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Progress> findById(Long progressId) throws SQLException {
        String sql = SqlScripts.PROGRESS_FIND_BY_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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

        String sql = SqlScripts.PROGRESS_FIND_BY_HABIT_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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
