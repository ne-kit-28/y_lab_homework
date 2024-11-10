package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import y_lab.controller.api.ProgressController;
import y_lab.service.HabitService;
import y_lab.service.ProgressService;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProgressControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProgressService progressService;

    @Mock
    private HabitService habitService;

    @InjectMocks
    private ProgressController progressController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(progressController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("create progress")
    void createProgress() throws Exception {

        when(progressService.createProgress(anyLong())).thenReturn(true);

        mockMvc.perform(post("/progress/1/create/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("create progress when habiId is incorrect")
    void createProgressNotFound() throws Exception {

        when(progressService.createProgress(anyLong())).thenReturn(false);

        mockMvc.perform(post("/progress/1/create/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("get streak successfully")
    void getStreak() throws Exception {

        String streakMessage = "5-day streak!";
        when(progressService.calculateStreak(anyLong())).thenReturn(streakMessage);

        mockMvc.perform(get("/progress/1/streak/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(streakMessage));
    }

    @Test
    @DisplayName("get statistic successfully")
    void getStatistic() throws Exception {

        String statisticMessage = "Weekly progress: 80%";
        when(progressService.generateProgressStatistics(anyLong(), any())).thenReturn(statisticMessage);

        mockMvc.perform(get("/progress/1/statistic/1/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(statisticMessage));
    }

    @Test
    @DisplayName("get report successfully")
    void getReport() throws Exception {

        String reportMessage = "Detailed progress report for the month";
        when(progressService.generateReport(anyLong(), any())).thenReturn(reportMessage);

        mockMvc.perform(get("/progress/1/report/1/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(reportMessage));
    }
}
