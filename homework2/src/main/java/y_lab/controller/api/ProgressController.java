package y_lab.controller.api;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import y_lab.dto.ProgressResponseDto;
import y_lab.service.HabitService;
import y_lab.service.ProgressService;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;

@RestController
@RequestMapping(value = "/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final HabitService habitService;

    @Autowired
    public ProgressController(HabitServiceImpl habitService, ProgressServiceImpl progressService) {
        this.progressService = progressService;
        this.habitService = habitService;
    }

    @PostMapping(value = "/{userId}/create/{habitId}")
    public ResponseEntity<Void> createProgress(@PathVariable("habitId") @Positive long habitId) {
        if (progressService.createProgress(habitId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{userId}/streak/{habitId}")
    public ResponseEntity<ProgressResponseDto> getStreak(@PathVariable("habitId") @Positive long habitId) {
        String message = progressService.calculateStreak(habitId);
        return ResponseEntity.ok(new ProgressResponseDto(habitId, "streak", message));
    }

    @GetMapping(value = "/{userId}/statistic/{habitId}/{period}")
    public ResponseEntity<ProgressResponseDto> getStatistic(@PathVariable("habitId") @Positive long habitId,
                                                            @PathVariable("period") String period) {
        String message = progressService.generateProgressStatistics(habitId, period);
        return ResponseEntity.ok(new ProgressResponseDto(habitId, "statistic", message));
    }

    @GetMapping(value = "/{userId}/report/{habitId}/{period}")
    public ResponseEntity<ProgressResponseDto> getReport(@PathVariable("habitId") @Positive long habitId,
                                                         @PathVariable("period") String period) {
        String message = progressService.generateReport(habitId, period);
        return ResponseEntity.ok(new ProgressResponseDto(habitId, "report", message));
    }
}