package y_lab.service;

import y_lab.domain.Habit;
import y_lab.domain.enums.Frequency;

import java.util.ArrayList;

public interface HabitService {

    public void createHabit(Long userId, String name, String description, Frequency frequency);

    public void deleteHabit(Long id);

    public ArrayList<Habit> getHabits(Long userId, Object filter);

    public Long getHabit(String habitName, Long userId);

    public void updateHabit(Long id, String newName, String newDescription, Frequency newFrequency);
}
