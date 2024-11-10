package y_lab.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import y_lab.dto.ProgressResponseDto;
import y_lab.service.HabitService;
import y_lab.service.ProgressService;

@RestController
@RequestMapping(value = "/progress")
@Tag(name = "Progress", description = "Operations related to user progress on habits")
public class ProgressController {

    private final ProgressService progressService;
    private final HabitService habitService;

    @Autowired
    public ProgressController(HabitService habitService, ProgressService progressService) {
        this.progressService = progressService;
        this.habitService = habitService;
    }

    @Operation(summary = "Create progress",
            description = "Creates progress for a specific habit by user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Progress successfully created"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "Habit not found")
            })
    @PostMapping(value = "/{userId}/create/{habitId}")
    public ResponseEntity<Void> createProgress(@PathVariable("userId") @Positive long userId,
                                               @PathVariable("habitId") @Positive long habitId) {
        if (progressService.createProgress(habitId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get streak",
            description = "Calculates the streak for a specific habit by user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Successfully retrieved streak",
                            content = @Content(schema = @Schema(implementation = ProgressResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "Habit not found")
            })
    @GetMapping(value = "/{userId}/streak/{habitId}")
    public ResponseEntity<ProgressResponseDto> getStreak(@PathVariable("userId") @Positive long userId,
                                                         @PathVariable("habitId") @Positive long habitId) {
        String message = progressService.calculateStreak(habitId);
        return ResponseEntity.ok(new ProgressResponseDto(habitId, "streak", message));
    }

    @Operation(summary = "Get statistics",
            description = "Generates progress statistics for a specific habit by user for a given period.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Successfully retrieved statistics",
                            content = @Content(schema = @Schema(implementation = ProgressResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "Habit not found")
            })
    @GetMapping(value = "/{userId}/statistic/{habitId}/{period}")
    public ResponseEntity<ProgressResponseDto> getStatistic(@PathVariable("userId") @Positive long userId,
                                                            @PathVariable("habitId") @Positive long habitId,
                                                            @PathVariable("period") String period) {
        String message = progressService.generateProgressStatistics(habitId, period);
        return ResponseEntity.ok(new ProgressResponseDto(habitId, "statistic", message));
    }

    @Operation(summary = "Get report",
            description = "Generates a report of progress for a specific habit by user for a given period.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Successfully retrieved report",
                            content = @Content(schema = @Schema(implementation = ProgressResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "Habit not found")
            })
    @GetMapping(value = "/{userId}/report/{habitId}/{period}")
    public ResponseEntity<ProgressResponseDto> getReport(@PathVariable("userId") @Positive long userId,
                                                         @PathVariable("habitId") @Positive long habitId,
                                                         @PathVariable("period") String period) {
        String message = progressService.generateReport(habitId, period);
        return ResponseEntity.ok(new ProgressResponseDto(habitId, "report", message));
    }
}
