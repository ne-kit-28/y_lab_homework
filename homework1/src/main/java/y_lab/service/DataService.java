package y_lab.service;

/**
 * Interface for data services that handle file operations.
 * This interface provides methods for saving and loading data to and from files.
 */
public interface DataService {

    /**
     * Saves data to a specified file.
     *
     * @param fileName the name of the file to which data will be saved
     */
    public void saveToFile(String fileName);

    /**
     * Loads data from a specified file.
     *
     * @param fileName the name of the file from which data will be loaded
     */
    public void loadFromFile(String fileName);
}
