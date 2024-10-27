package y_lab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;
import y_lab.service.HabitService;
import y_lab.service.ProgressService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProgressControllerTest {

    @Mock
    private ProgressService progressService;

    @Mock
    private HabitService habitService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ProgressController progressController;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(request.getParameter("habitId")).thenReturn("1");
        when(request.getParameter("userId")).thenReturn("1");
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_withStreakType_success() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress");
        when(request.getParameter("type")).thenReturn("streak");
        when(habitService.getHabit(anyLong())).thenReturn(Optional.of(new Habit(1L, 1L, "sleep", "a lot", Frequency.DAILY, LocalDate.now().minusDays(1))));
        when(progressService.calculateStreak(anyLong())).thenReturn("Streak result");

        progressController.doGet(request, response);
        assertEquals("\"Streak result\"", responseWriter.toString().trim());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGet_withStatisticType_success() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress");
        when(request.getParameter("type")).thenReturn("statistic");
        when(request.getParameter("period")).thenReturn("weekly");
        when(habitService.getHabit(anyLong())).thenReturn(Optional.of(new Habit(1L, 1L, "sleep", "a lot", Frequency.DAILY, LocalDate.now().minusDays(1))));
        when(progressService.generateProgressStatistics(anyLong(), anyString())).thenReturn("Statistic result");

        progressController.doGet(request, response);
        assertEquals("\"Statistic result\"", responseWriter.toString().trim());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGet_withReportType_success() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress");
        when(request.getParameter("type")).thenReturn("report");
        when(request.getParameter("period")).thenReturn("monthly");
        when(habitService.getHabit(anyLong())).thenReturn(Optional.of(new Habit(1L, 1L, "sleep", "a lot", Frequency.DAILY, LocalDate.now().minusDays(1))));
        when(progressService.generateReport(anyLong(), anyString())).thenReturn("Report result");

        progressController.doGet(request, response);
        assertEquals("\"Report result\"", responseWriter.toString().trim());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGet_withInvalidHabitId() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress");
        when(request.getParameter("habitId")).thenReturn("invalid_id");
        progressController.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный habitId");
    }

    @Test
    void testDoGet_habitNotFound() throws Exception {
        when(habitService.getHabit(anyLong())).thenReturn(Optional.empty());

        progressController.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
    }

    @Test
    void testDoPost_createProgress_success() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress/create");
        when(progressService.createProgress(anyLong())).thenReturn(true);

        progressController.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoPost_createProgress_habitNotFound() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress/create");
        when(progressService.createProgress(anyLong())).thenReturn(false);

        progressController.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Habit with such id not found");
    }

    @Test
    void testDoPost_withInvalidHabitId() throws Exception {
        when(request.getServletPath()).thenReturn("/api/progress/create");
        when(request.getParameter("habitId")).thenReturn("invalid_id");

        progressController.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "id is incorrect");
    }
}
