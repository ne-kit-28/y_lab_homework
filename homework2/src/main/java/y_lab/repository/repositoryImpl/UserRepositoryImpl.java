package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.SqlScripts;
import y_lab.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Getter
@Setter
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final DataSource dataSource;

    @Autowired
    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean isEmailExist(String email) throws SQLException {
        String sql = SqlScripts.USER_IS_EMAIL_EXIST;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        }
        return false;
    }

    @Override
    public boolean isAdminEmail(String email) throws SQLException{
        String sql = SqlScripts.USER_IS_ADMIN_EMAIL;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        }
        return false;
    }

    @Override
    public void save(User user) throws SQLException {
        String sql = SqlScripts.USER_SAVE;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getName());
            stmt.setBoolean(4, user.isBlock());
            stmt.setString(5, user.getRole().getValue());
            stmt.setString(6, user.getResetToken());

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException{
        String sql = SqlScripts.USER_FIND_BY_EMAIL;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            User user = new User();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
                user.setEmail(resultSet.getString(2));
                user.setPasswordHash(resultSet.getString(3));
                user.setName(resultSet.getString(4));
                user.setBlock(resultSet.getBoolean(5));
                user.setRole(Role.fromString(resultSet.getString(6)));
                user.setResetToken(resultSet.getString(7));
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) throws SQLException{
        String sql = SqlScripts.USER_FIND_BY_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet resultSet = stmt.executeQuery();

            User user = new User();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
                user.setEmail(resultSet.getString(2));
                user.setPasswordHash(resultSet.getString(3));
                user.setName(resultSet.getString(4));
                user.setBlock(resultSet.getBoolean(5));
                user.setRole(Role.fromString(resultSet.getString(6)));
                user.setResetToken(resultSet.getString(7));
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<User> getAll() throws SQLException{
        ArrayList<User> users = new ArrayList<>();

        String sql = SqlScripts.USER_GET_ALL;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                User user = new User();

                user.setId(resultSet.getLong(1));
                user.setEmail(resultSet.getString(2));
                user.setPasswordHash(resultSet.getString(3));
                user.setName(resultSet.getString(4));
                user.setBlock(resultSet.getBoolean(5));
                user.setRole(Role.fromString(resultSet.getString(6)));
                user.setResetToken(resultSet.getString(7));
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public void deleteById(Long id) throws SQLException{
        String sql = SqlScripts.USER_DELETE_BY_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Long id, User user) throws SQLException{
        String sql = SqlScripts.USER_UPDATE;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(7, user.getId());
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getName());
            stmt.setBoolean(4, user.isBlock());
            stmt.setString(5, user.getRole().getValue());
            stmt.setString(6, user.getResetToken());

            stmt.executeUpdate();
        }
    }
}
