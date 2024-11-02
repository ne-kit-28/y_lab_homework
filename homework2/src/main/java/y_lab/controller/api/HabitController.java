package y_lab.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import y_lab.domain.Habit;
import y_lab.dto.HabitRequestDto;
import y_lab.dto.HabitResponseDto;
import y_lab.mapper.HabitMapper;
import y_lab.mapper.HabitMapperImpl;
import y_lab.service.HabitService;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(value = "/habit")
@Tag(name = "Habit Management", description = "Operations related to habit management")
public class HabitController {

    private final HabitService habitService;
    private final HabitMapper habitMapper;

    @Autowired
    public HabitController(HabitService habitService) {
        this.habitService = habitService;
        this.habitMapper = new HabitMapperImpl();
    }

    @Operation(summary = "Get all habits of a user", description = "Retrieve all habits for a specific user with a filter")
    @GetMapping(value = "/{userId}/all/{filter}")
    public ResponseEntity<ArrayList<HabitResponseDto>> getHabits(
            @Parameter(description = "ID of the user to retrieve habits for", required = true)
            @PathVariable("userId") @Positive long userId,

            @Parameter(description = "Filter to apply on habits", required = true)
            @PathVariable("filter") String filter) {
        return ResponseEntity.ok(habitMapper.habitsToHabitResponseDtos(habitService.getHabits(userId, filter)));
    }

    @Operation(summary = "Get a specific habit by name", description = "Retrieve a habit by its name for a specific user")
    @GetMapping(value = "/{userId}/{name}")
    public ResponseEntity<HabitResponseDto> getHabit(
            @Parameter(description = "ID of the user to retrieve the habit for", required = true)
            @PathVariable("userId") @Positive long userId,

            @Parameter(description = "Name of the habit to retrieve", required = true)
            @PathVariable("name") String name) {
        Optional<Habit> habit = habitService.getHabit(name, userId);
        return habit.map(value -> ResponseEntity.ok(habitMapper.habitToHabitResponseDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new habit", description = "Create a new habit for a specific user")
    @PostMapping(value = "/{userId}/create")
    public ResponseEntity<HabitResponseDto> createHabit(
            @Parameter(description = "ID of the user to create the habit for", required = true)
            @PathVariable("userId") @Positive long userId,

            @Parameter(description = "Habit details to be created", required = true)
            @RequestBody @Valid HabitRequestDto habitRequestDto) {
        habitService.createHabit(userId, habitMapper.habitRequestDtoToHabit(habitRequestDto));
        Optional<Habit> habit = habitService.getHabit(habitRequestDto.name(), userId);
        return habit.map(value -> ResponseEntity.ok(habitMapper.habitToHabitResponseDto(habit.get())))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Update an existing habit", description = "Update a specific habit for a user")
    @PutMapping(value = "/{userId}/{habitId}")
    public ResponseEntity<Void> updateHabit(
            @Parameter(description = "ID of the habit to update", required = true)
            @PathVariable("habitId") @Positive long habitId,

            @Parameter(description = "Updated habit details", required = true)
            @RequestBody @Valid HabitRequestDto habitRequestDto) {
        if (habitService.updateHabit(habitId, habitMapper.habitRequestDtoToHabit(habitRequestDto)))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Delete a specific habit", description = "Delete a specific habit for a user")
    @DeleteMapping(value = "/{userId}/{habitId}")
    public ResponseEntity<Void> deleteHabit(
            @Parameter(description = "ID of the habit to delete", required = true)
            @PathVariable("habitId") @Positive long habitId) {
        if (habitService.deleteHabit(habitId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
