import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Storage {

    /**
     * Loads tasks from disk into the task list. Reads from ./data/Sagiri.dat in format: type | marked | name | time
     * Throws SagiriException if the file format is corrupted.
     */
    public static void loadTasks(TaskList taskList) throws SagiriException {
        try {
            File file = new File("./data/Sagiri.dat");
            if (!file.exists()) {
                return; // No file to load, start with empty list
            }

            List<String> lines = Files.readAllLines(Paths.get("./data/Sagiri.dat"));
            for (int lineNum = 1; lineNum <= lines.size(); lineNum++) {
                String line = lines.get(lineNum - 1);
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(" \\| ");
                if (parts.length != 4) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Expected 4 parts separated by ' | ', found " + parts.length);
                }

                String type = parts[0].trim();
                String marked = parts[1].trim();
                String name = parts[2].trim();
                String time = parts[3].trim();

                // Validate type
                if (!type.equals("T") && !type.equals("E") && !type.equals("D")) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Invalid task type '" + type + "'. Expected T, E, or D");
                }

                // Validate marked status
                if (!marked.equals("0") && !marked.equals("1")) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Invalid marked status '" + marked + "'. Expected 0 or 1");
                }

                // Validate name
                if (name.isEmpty()) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Task name cannot be empty");
                }

                Task task = null;
                if (type.equals("T")) {
                    // Todo tasks should have empty time field
                    if (!time.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Todo task should have empty time field, found '" + time + "'");
                    }
                    task = new Task(name);
                } else if (type.equals("E")) {
                    // Event tasks should have "start | end" format
                    String[] timeParts = time.split(" \\| ");
                    if (timeParts.length != 2) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Event task time should be in 'start | end' format, found '" + time + "'");
                    }
                    String start = timeParts[0].trim();
                    String end = timeParts[1].trim();
                    if (start.isEmpty() || end.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Event start and end times cannot be empty");
                    }
                    task = new Task(name, start, end);
                } else if (type.equals("D")) {
                    // Deadline tasks should have single time value
                    if (time.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Deadline task must have a deadline time");
                    }
                    task = new Task(name, time);
                }

                if (task != null) {
                    if (marked.equals("1")) {
                        task.markAsDone();
                    }
                    taskList.getTasks().add(task); // Directly add to the list
                }
            }
        } catch (IOException e) {
            throw new SagiriException("Error reading data file: " + e.getMessage());
        }
    }

    /**
     * Saves tasks to disk. Saves to ./data/Sagiri.dat in format: type | marked | name | time
     */
    public static void saveTasks(TaskList taskList) {
        try {
            // Create data directory if it doesn't exist
            Files.createDirectories(Paths.get("./data"));

            FileWriter fw = new FileWriter("./data/Sagiri.dat");
            for (Task task : taskList.getTasks()) {
                String type = task.getTypeIcon();
                String marked = task.isDone() ? "1" : "0";
                String name = task.getName();

                String time = "";
                if (task.getType() == TaskType.EVENT) {
                    String start = formatDateForStorage(task.getStartDateTime());
                    String end = formatDateForStorage(task.getEndDateTime());
                    time = start + " | " + end;
                } else if (task.getType() == TaskType.DEADLINE) {
                    time = formatDateForStorage(task.getEndDateTime());
                }

                fw.write(type + " | " + marked + " | " + name + " | " + time + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Formats a LocalDateTime to "dd-mm-yy" format for storage.
     * @param dateTime the LocalDateTime to format
     * @return formatted date string or empty string if dateTime is null
     */
    private static String formatDateForStorage(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
    }
}