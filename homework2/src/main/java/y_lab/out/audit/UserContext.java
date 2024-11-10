package y_lab.out.audit;

/**
 * Класс {@code UserContext} используется для хранения идентификатора текущего пользователя
 * в контексте потока. Он реализует механизм хранения идентификатора пользователя
 * с использованием {@link ThreadLocal}, что позволяет безопасно получать и устанавливать
 * идентификатор пользователя в многопоточной среде.
 */
public class UserContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    /**
     * Устанавливает идентификатор текущего пользователя.
     *
     * @param userId идентификатор пользователя для установки.
     */
    public static void setUserId(Long userId) {
        currentUserId.set(userId);
    }

    /**
     * Получает идентификатор текущего пользователя.
     *
     * @return идентификатор текущего пользователя, или {@code null}, если идентификатор не установлен.
     */
    public static Long getUserId() {
        return currentUserId.get();
    }

    /**
     * Очищает идентификатор текущего пользователя из контекста потока.
     * Этот метод следует вызывать, чтобы избежать утечек памяти
     * после завершения работы с идентификатором пользователя.
     */
    public static void clear() {
        currentUserId.remove();
    }
}
