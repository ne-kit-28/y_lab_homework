package y_lab.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import y_lab.controller.api.HabitController;
import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;
import y_lab.dto.HabitRequestDto;
import y_lab.dto.HabitResponseDto;
import y_lab.mapper.HabitMapper;
import y_lab.service.HabitService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class HabitControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private HabitService habitService;

    @Mock
    private HabitMapper habitMapper;

    @InjectMocks
    private HabitController habitController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Get habits with valid filter and userId")
    public void getHabits() throws Exception {
        long userId = 1L;
        String filter = "test";
        HabitResponseDto habitResponseDto = new HabitResponseDto(1L, "sleep", "a lot", "daily", "12-02-2222");
        ArrayList<HabitResponseDto> habitResponseDtos = new ArrayList<>();
        habitResponseDtos.add(habitResponseDto);
        habitResponseDtos.add(habitResponseDto);

        when(habitService.getHabits(userId, filter)).thenReturn(new ArrayList<>());
        when(habitMapper.habitsToHabitResponseDtos(any())).thenReturn(habitResponseDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/habit/{userId}/all/{filter}", userId, filter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

        verify(habitService).getHabits(userId, filter);
    }

    @Test
    @DisplayName("get the habit when the habit is exist")
    public void getHabit() throws Exception {

        long userId = 1L;
        String habitName = "sleep";
        Habit habit = new Habit("sleep", "a lot", Frequency.DAILY, LocalDate.now());
        HabitResponseDto habitResponseDto = new HabitResponseDto(1L, "sleep", "a lot", "daily", "12-02-2222");

        when(habitService.getHabit(habitName, userId)).thenReturn(Optional.of(habit));
        when(habitMapper.habitToHabitResponseDto(habit)).thenReturn(habitResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/habit/{userId}/{name}", userId, habitName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(habitName));

        verify(habitService).getHabit(habitName, userId);
    }

    @Test
    @DisplayName("Create habit when input is valid")
    public void createHabit() throws Exception {

        long userId = 1L;
        HabitRequestDto habitRequestDto = new HabitRequestDto("sleep", "a lot", "daily");
        Habit habit = new Habit("sleep", "a lot", Frequency.DAILY, LocalDate.now());
        HabitResponseDto habitResponseDto = new HabitResponseDto(1L, "sleep", "a lot", "daily", "12-02-2222");

        when(habitMapper.habitRequestDtoToHabit(habitRequestDto)).thenReturn(habit);
        when(habitService.getHabit(habitRequestDto.name(), userId)).thenReturn(Optional.of(habit));
        when(habitMapper.habitToHabitResponseDto(habit)).thenReturn(habitResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/habit/{userId}/create", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("sleep"));
    }

    @Test
    @DisplayName("update successfully")
    public void updateHabit() throws Exception {

        long userId = 1L;
        long habitId = 1L;
        HabitRequestDto habitRequestDto = new HabitRequestDto("sleep", "a lot","daily");
        Habit habit = new Habit(1L, userId,"sleep", "a lot", Frequency.DAILY, LocalDate.now());

        when(habitMapper.habitRequestDtoToHabit(habitRequestDto)).thenReturn(habit);
        when(habitService.updateHabit(any(), any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/habit/{userId}/{habitId}", userId, habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("delete successfully")
    public void deleteHabit_ShouldReturnOk_WhenHabitDeletedSuccessfully() throws Exception {

        long userId = 1L;
        long habitId = 1L;

        when(habitService.deleteHabit(habitId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/habit/{userId}/{habitId}", userId, habitId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(habitService).deleteHabit(habitId);
    }
}
