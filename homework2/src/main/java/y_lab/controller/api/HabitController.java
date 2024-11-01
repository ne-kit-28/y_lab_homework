package y_lab.controller.api;

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
import y_lab.service.serviceImpl.HabitServiceImpl;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(value = "/habit")
public class HabitController {

    private final HabitService habitService;
    private final HabitMapper habitMapper;

    @Autowired
    public HabitController(HabitServiceImpl habitService) {
        this.habitService = habitService;
        this.habitMapper = new HabitMapperImpl();
    }

    @GetMapping(value = "/{userId}/all/{filter}")
    public ResponseEntity<ArrayList<HabitResponseDto>> getHabits(@PathVariable("userId") @Positive long userId,
                                                                 @PathVariable("filter") String filter) {
        return ResponseEntity.ok(habitMapper.habitsToHabitResponseDtos(habitService.getHabits(userId, filter)));
    }

    @GetMapping(value = "/{userId}/{name}")
    public ResponseEntity<HabitResponseDto> getHabit(@PathVariable("userId") @Positive long userId,
                                                     @PathVariable("name") String name) {
        Optional<Habit> habit = habitService.getHabit(name, userId);
        return habit.map(value -> ResponseEntity.ok(habitMapper.habitToHabitResponseDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{userId}/create")
    public ResponseEntity<HabitResponseDto> createHabit(@PathVariable("userId") @Positive long userId,
                                                        @RequestBody @Valid HabitRequestDto habitRequestDto) {
        habitService.createHabit(userId, habitMapper.habitRequestDtoToHabit(habitRequestDto));
        Optional<Habit> habit = habitService.getHabit(habitRequestDto.name(), userId);
        return habit.map(value -> ResponseEntity.ok(habitMapper.habitToHabitResponseDto(habit.get()))).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/{userId}/{habitId}")
    public ResponseEntity<Void> updateHabit(@PathVariable("habitId") @Positive long habitId,
                                            @RequestBody @Valid HabitRequestDto habitRequestDto) {
        if (habitService.updateHabit(habitId, habitMapper.habitRequestDtoToHabit(habitRequestDto)))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/{userId}/{habitId}")
    public ResponseEntity<Void> deleteHabit(@PathVariable("habitId") @Positive long habitId) {
        if (habitService.deleteHabit(habitId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
