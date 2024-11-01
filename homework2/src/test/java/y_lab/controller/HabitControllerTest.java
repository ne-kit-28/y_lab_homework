package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;
import y_lab.dto.HabitRequestDto;
import y_lab.service.HabitService;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitControllerTest {

    @Mock
    private HabitService habitService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private HabitController habitController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    @DisplayName("Успешное получение всех привычек")
    void testDoGetAllHabitsSuccess() throws Exception {
        // Настройка параметров запроса и мока сервиса
        when(request.getServletPath()).thenReturn("/api/habit/all");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("filter")).thenReturn("filter");

        ArrayList<Habit> habits = new ArrayList<>();
        habits.add(new Habit(1L, 1L, "sleep", "a lot", Frequency.DAILY, LocalDate.now().minusDays(1)));
        when(habitService.getHabits(any(), any())).thenReturn(habits);

        habitController.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write(any(String.class));
    }

    @Test
    @DisplayName("Успешное создание привычки")
    void testDoPostCreateHabitSuccess() throws Exception {
        when(request.getParameter("userId")).thenReturn("1");
        HabitRequestDto habitRequestDto = new HabitRequestDto("Updated Habit", "a new description", "daily");
        when(habitService.createHabit(anyLong(), any(Habit.class))).thenReturn((long) 1);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(habitRequestDto))));


        habitController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Успешное обновление привычки")
    void testDoPutUpdateHabitSuccess() throws Exception {
        when(request.getParameter("habitId")).thenReturn("1");
        when(habitService.updateHabit(any(), any())).thenReturn(true);
        HabitRequestDto habitRequestDto = new HabitRequestDto("Updated Habit", "a new description", "daily");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(habitRequestDto))));

        habitController.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).print(true);
    }

    @Test
    @DisplayName("Удаление привычки")
    void testDoDeleteHabitSuccess() throws Exception {
        when(request.getParameter("habitId")).thenReturn("1");
        when(habitService.deleteHabit(1L)).thenReturn(true);

        habitController.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).print(true);
    }
}
