package y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import y_lab.domain.Habit;
import y_lab.dto.HabitRequestDto;
import y_lab.dto.HabitResponseDto;

import java.util.ArrayList;

@Mapper(imports = {y_lab.domain.enums.Frequency.class})
public interface HabitMapper {
    HabitResponseDto habitToHabitResponseDto(Habit habit);

    ArrayList<HabitResponseDto> habitsToHabitResponseDtos(ArrayList<Habit> habits);

    @Mapping(target = "frequency", expression = "java(Frequency.fromString(habitRequestDto.frequency()))")
    Habit habitRequestDtoToHabit(HabitRequestDto habitRequestDto);
}
