package sagiri.storage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import sagiri.exception.SagiriException;


import sagiri.task.Task;
import sagiri.task.TaskList;
import sagiri.task.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Storage {

    /**
     * Loads tasks from disk into the task list. Reads from ./data/Sagiri.dat in format: type | marked | name | start | end
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
                if (parts.length != 5) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Expected 5 parts separated by ' | ', found " + parts.length);
                }

                String type = parts[0].trim();
                String marked = parts[1].trim();
                String name = parts[2].trim();
                String start = parts[3].trim();
                String end = parts[4].trim();

                // Handle null placeholders
                if (start.equals("null")) {
                    start = "";
                }
                
                if (end.equals("null")) {
                    end = "";
                }

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
                    // Todo tasks should have empty start and end
                    if (!start.isEmpty() || !end.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Todo task should have empty start and end fields, found start='" + start + "', end='" + end + "'");
                    }
                    task = new Task(name);
                } else if (type.equals("E")) {
                    // Event tasks should have both start and end
                    if (start.isEmpty() || end.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Event task must have both start and end times, found start='" + start + "', end='" + end + "'");
                    }
                    task = new Task(name, start, end);
                } else if (type.equals("D")) {
                    // Deadline tasks should have empty start and non-empty end
                    if (!start.isEmpty() || end.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Deadline task should have empty start and non-empty end, found start='" + start + "', end='" + end + "'");
                    }
                    task = new Task(name, end);
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
     * Saves tasks to disk. Saves to ./data/Sagiri.dat in format: type | marked | name | start | end
     * If no start and end date, saves as "null".
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

                String start = "null";
                String end = "null";
                if (task.getType() == TaskType.EVENT) {
                    start = formatDateForStorage(task.getStartDateTime());
                    end = formatDateForStorage(task.getEndDateTime());
                } else if (task.getType() == TaskType.DEADLINE) {
                    end = formatDateForStorage(task.getEndDateTime());
                }

                fw.write(type + " | " + marked + " | " + name + " | " + start + " | " + end + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Formats a LocalDateTime to "dd-mm-yy" format for storage.
     * @param dateTime the LocalDateTime to format
     * @return formatted date string or "null" if dateTime is null
     */
    private static String formatDateForStorage(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "null";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
    }
}