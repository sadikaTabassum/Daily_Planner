import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();

    public TaskManager() {
        loadTasksFromFile();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        saveTasksToFile();
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        saveTasksToFile();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks);
        allTasks.addAll(completedTasks);
        return allTasks;
    }

    public void markTaskAsDone(String taskName) {
        Task taskToMark = null;
        Iterator<Task> iterator = this.tasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getDescription().equals(taskName)) {
                taskToMark = task;
                iterator.remove();
                break;
            }
        }

        if (taskToMark != null) {
            this.completedTasks.add(taskToMark);
            saveTasksToFile();
            saveCompletedTasksToFile();
        }
    }

    private void loadTasksFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("tasks.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 6) { // Adjust to handle 6 fields
                    tasks.add(new Task(
                    parts[0], // Description
                    parts[1], // Priority
                    parts[2], // Deadline
                    parts[3], // Start Time
                    Integer.parseInt(parts[4]), // Duration
                    Boolean.parseBoolean(parts[5])));
                } else {
                    System.err.println("Skipping invalid task entry: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"))) {
            for (Task task : tasks) {
                writer.write(task.getDescription() + "," + task.getPriority() + "," +
                    task.getDeadline() + "," + task.getStartTime() + "," + task.getDuration() + "," + task.isRecurring());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCompletedTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("completed_tasks.txt", true))) {
            for (Task task : completedTasks) {
                writer.write(task.getDescription() + "," + task.getDeadline() + "," +
                    task.getPriority() + "," + task.getStartTime() + "," + task.getDuration() + "," + task.isRecurring());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCompletedTask(Task task) {

        completedTasks.add(task);

    }
}
