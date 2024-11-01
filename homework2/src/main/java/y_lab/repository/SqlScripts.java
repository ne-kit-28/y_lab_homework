package y_lab.repository;

/**
 * SqlScripts is stores sql scripts.
 */

public class SqlScripts {
    public static final String HABIT_FIND_BY_ID =
            "SELECT * FROM domain.habits " +
                    "WHERE id = ?";

    public static final String HABIT_FIND_BY_NAME =
            "SELECT * FROM domain.habits " +
                    "WHERE name = ? AND user_id = ?";

    public static final String HABIT_SAVE =
            "INSERT INTO domain.habits (id, user_id, name, description, frequency, created_at) " +
                    "VALUES (nextval('domain.habit_id_seq'), ?, ?, ?, ?, ?)";

    public static final String HABIT_DELETE_BY_ID =
            "DELETE FROM domain.habits WHERE id = ?";

    public static final String HABIT_DELETE_ALL_BY_USER_ID =
            "DELETE FROM domain.habits WHERE user_id = ?";

    public static final String HABIT_FIND_BY_USER_ID =
            "SELECT * FROM domain.habits " +
                    "WHERE user_id = ?";

    public static final String HABIT_GET_ALL =
            "SELECT * FROM domain.habits";

    public static final String HABIT_UPDATE =
            "UPDATE domain.habits SET user_id = ?, name = ?, description = ?, frequency = ?, created_at = ? WHERE id = ?";

    public static final String USER_IS_EMAIL_EXIST =
            "SELECT COUNT(*) as count FROM domain.users WHERE email = ?";

    public static final String USER_IS_ADMIN_EMAIL =
            "SELECT COUNT(*) as count FROM service.admins WHERE email = ?";

    public static final String USER_SAVE =
            "INSERT INTO domain.users (id, email, password_hash, name, is_block, role, reset_token) " +
                    "VALUES (nextval('domain.user_id_seq'), ?, ?, ?, ?, ?, ?)"; // вызов nextval - явное использование Sequence

    public static final String USER_FIND_BY_EMAIL =
            "SELECT * FROM domain.users WHERE email = ?";

    public static final String USER_FIND_BY_ID =
            "SELECT * FROM domain.users WHERE id = ?";

    public static final String USER_GET_ALL =
            "SELECT * FROM domain.users";

    public static final String USER_DELETE_BY_ID =
            "DELETE FROM domain.users WHERE id = ?";

    public static final String USER_UPDATE =
            "UPDATE domain.users SET email = ?, password_hash = ?, name = ?, is_block = ?, role = ?, reset_token = ? WHERE id = ?";

    public static final String PROGRESS_SAVE =
            "INSERT INTO domain.progresses (id, user_id, habit_id, date) " +
                    "VALUES (nextval('domain.progress_id_seq'), ?, ?, ?)"; //вызов nextval - явное использование Sequence

    public static final String PROGRESS_DELETE_ALL_BY_HABIT_ID =
            "DELETE FROM domain.progresses WHERE habit_id = ?";

    public static final String PROGRESS_DELETE_ALL_BY_USER_ID =
            "DELETE FROM domain.progresses WHERE user_id = ?";

    public static final String PROGRESS_FIND_BY_ID =
            "SELECT * FROM domain.progresses " +
                    "WHERE id = ?";

    public static final String PROGRESS_FIND_BY_HABIT_ID =
            "SELECT * FROM domain.progresses " +
                    "WHERE habit_id = ?";
}
