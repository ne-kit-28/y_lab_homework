package y_lab.service;

public interface ProgressService {

    public void createProgress(Long userId, Long habitId);

    public void generateProgressStatistics(Long habitId, String period);

    public void calculateStreak(Long habitId);

    public void generateReport(Long habitId, String period);
}
