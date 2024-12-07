package y_lab.audit_logging_spring_boot_starter.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class AuditServiceImplTest {

    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private AuditServiceImpl auditService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        String sql = "CREATE SCHEMA IF NOT EXISTS service;" +
                "CREATE TABLE IF NOT EXISTS service.audit (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id BIGINT, " +
                "date VARCHAR(50), " +
                "message VARCHAR(255));";

        Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().prepareStatement(
                sql).execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().prepareStatement(
                "DROP SCHEMA IF EXISTS service CASCADE;").execute();
    }

    @Test
    @DisplayName("Успешное создание аудита")
    void testCreateAudit(){
        AuditRecord auditRecord = new AuditRecord(1L, LocalDateTime.now(), "Test Audit 1");

        auditService.createAudit(auditRecord);

        ArrayList<AuditRecord> auditRecords = auditService.getAudit(1L);
        assertThat(auditRecords.get(0).message()).isEqualTo("Test Audit 1");
    }


    @Test
    @DisplayName("Получение аудита")
    void testGetAudit() throws SQLException {
        long userId = 2L;
        ArrayList<AuditRecord> expectedAuditRecords = new ArrayList<>();

        Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().prepareStatement(
                "INSERT INTO service.audit (user_id, date, message) " +
                        "VALUES ('2', '2024-11-02T11:29:38.771485', 'Test Audit 2')"
        ).execute();

        expectedAuditRecords.add(new AuditRecord(userId, LocalDateTime.parse("2024-11-02T11:29:38.771485"), "Test Audit 2"));

        ArrayList<AuditRecord> result = auditService.getAudit(userId);

        assertThat(result).isEqualTo(expectedAuditRecords);
    }
}
