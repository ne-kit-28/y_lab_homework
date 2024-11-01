package y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import y_lab.domain.Habit;
import y_lab.dto.HabitRequestDto;
import y_lab.dto.HabitResponseDto;

import java.util.ArrayList;

/**
 * Mapper interface for converting between Habit entities and Habit-related Data Transfer Objects (DTOs).
 * <p>
 * This interface uses MapStruct to generate the implementation for mapping
 * between Habit entities and HabitRequestDto, and HabitResponseDto.
 * </p>
 */
@Mapper(imports = {y_lab.domain.enums.Frequency.class})
public interface HabitMapper {

    /**
     * Converts a Habit entity to a HabitResponseDto.
     *
     * @param habit the Habit entity to be converted
     * @return the corresponding HabitResponseDto
     */
    HabitResponseDto habitToHabitResponseDto(Habit habit);

    /**
     * Converts a list of Habit entities to a list of HabitResponseDtos.
     *
     * @param habits the list of Habit entities to be converted
     * @return the corresponding list of HabitResponseDtos
     */
    ArrayList<HabitResponseDto> habitsToHabitResponseDtos(ArrayList<Habit> habits);

    /**
     * Converts a HabitRequestDto to a Habit entity.
     *
     * @param habitRequestDto the HabitRequestDto to be converted
     * @return the corresponding Habit entity with the appropriate frequency
     */
    @Mapping(target = "frequency", expression = "java(Frequency.fromString(habitRequestDto.frequency()))")
    Habit habitRequestDtoToHabit(HabitRequestDto habitRequestDto);
}
