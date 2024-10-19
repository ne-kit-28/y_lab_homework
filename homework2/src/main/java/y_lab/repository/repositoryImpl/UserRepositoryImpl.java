package y_lab.repository.repositoryImpl;

import lombok.Getter;
import lombok.Setter;
import y_lab.domain.User;
import y_lab.domain.enums.Role;
import y_lab.repository.UserRepository;

import java.sql.*;
import java.util.*;

@Getter
@Setter
public class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean isEmailExist(String email) throws SQLException {
        String sql =
                "SELECT COUNT(*) as count FROM domain.users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql =
                "SELECT COUNT(*) as count FROM service.admins WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql =
                "INSERT INTO domain.users (id, email, password_hash, name, is_block, role, reset_token) " +
                        "VALUES (nextval('domain.user_id_seq'), ?, ?, ?, ?, ?, ?)"; // вызов nextval - явное использование Sequence

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql =
                "SELECT * FROM domain.users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql =
                "SELECT * FROM domain.users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

        String sql =
                "SELECT * FROM domain.users";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql =
                "DELETE FROM domain.users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Long id, User user) throws SQLException{
        String sql = "UPDATE domain.users SET email = ?, password_hash = ?, name = ?, is_block = ?, role = ?, reset_token = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
