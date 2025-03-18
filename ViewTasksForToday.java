import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.util.InputMismatchException;

public class ViewTasksForToday {
    private Pomodoro pomodoro = PlannerApp.getPomodoro();

    public void execute() {
        // Logic to view tasks for today
        System.out.println("Displaying tasks for today...");

        List<Task> tasks = getTasksForToday();
        Collections.sort(tasks, Comparator.comparing(Task::getStartTime));

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String priorityColor = getPriorityColor(task.getPriority());
            System.out.println((i + 1) + ". " + priorityColor + "(" + task.getPriority().charAt(0) + ")" + "\u001B[0m" + task.getDescription() + " on " + task.getDeadline() + " at " + task.getStartTime() + " for " + task.getDuration() + " hours");
        }

        // Options for the user
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            System.out.println("Options:");
            System.out.println("1. Assign Pomodoro session for a task");
            System.out.println("2. Remove a task");
            System.out.println("3. Back to home page");
            System.out.println("Enter your choice:");

            try {
                choice = scanner.nextInt();

                if (choice >= 1 && choice <= 3) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter a valid option or press 3 to go back to the home page.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }

        switch (choice) {
            case 1:
                assignPomodoroSession(tasks);
                break;
            case 2:
                removeTask(tasks);
                break;
            case 3:
                PlannerApp.backToHomePage();
                break;
        }
    }

    private List<Task> getTasksForToday() {
        List<Task> tasksForToday = new ArrayList<>();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedReader br = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Task task = new Task(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), Boolean.parseBoolean(parts[5]));
                    if (task.getDeadline().equals(today)) {
                        tasksForToday.add(task);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasksForToday;
    }

    private void assignPomodoroSession(List<Task> tasks) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the task you want to assign a Pomodoro session to:");
        int taskNumber = scanner.nextInt();

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks.get(taskNumber - 1);
        System.out.println("Enter the duration for the Pomodoro session (in minutes):");
        int sessionDuration = scanner.nextInt();
        int breakDuration = 5;

        while (true) {
            System.out.println("Starting Pomodoro session for task: " + task.getDescription());
            long startTime = System.currentTimeMillis();
            long endTime = startTime + sessionDuration * 60 * 1000;

            while (System.currentTimeMillis() < endTime) {
                System.out.println("Options:");
                System.out.println("1. Task finished");
                System.out.println("2. End Pomodoro session");
                System.out.println("Enter your choice:");
                int option = scanner.nextInt();

                if (option == 1) {
                    markTaskAsDone(task);
                    System.out.println("Task marked as done.");
                    askForNextAction();
                    return;
                } else if (option == 2) {
                    System.out.println("Pomodoro session ended.");
                    askForNextAction();
                    return;
                } else {
                    System.out.println("Invalid choice. Please enter a valid option.");
                }
            }

            if (System.currentTimeMillis() >= endTime) {
                playSound("taskDone.wav"); // Play sound when session is complete

                System.out.println("Break time started for " + breakDuration + " minutes.");
                pomodoro.startBreak(breakDuration);
                playSound("taskDone.wav"); // Play sound when break is over

                System.out.println("Break time is over. Do you want to start another Pomodoro session? (Y/N):");
                String anotherSession = scanner.next();
                if (anotherSession.equalsIgnoreCase("Y")) {
                    System.out.println("Enter the duration for the next Pomodoro session (in minutes):");
                    sessionDuration = scanner.nextInt();
                } else {
                    break;
                }
            }
        }

        System.out.println("Did you complete the task? (Y/N):");
        String completed = scanner.next();
        if (completed.equalsIgnoreCase("Y")) {
            markTaskAsDone(task);
            System.out.println("Task marked as done.");
        } else {
            System.out.println("Task not marked as done.");
        }

        askForNextAction(); // Return to the task list after the Pomodoro session
    }

    private void askForNextAction() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to do next?");
        System.out.println("1. View tasks for today");
        System.out.println("2. Go back to home page");
        System.out.println("Enter your choice:");
        int choice = scanner.nextInt();

        if (choice == 1) {
            execute();
        } else if (choice == 2) {
            PlannerApp.backToHomePage();
        } else {
            System.out.println("Invalid choice. Returning to home page.");
            PlannerApp.backToHomePage();
        }
    }

    private void removeTask(List<Task> tasks) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the task you want to remove:");
        int taskNumber = scanner.nextInt();

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks.get(taskNumber - 1);
        PlannerApp.getTaskManager().removeTask(task);
        System.out.println("Task removed successfully.");

        execute(); // Return to the task list after removing the task
    }

    private void markTaskAsDone(Task task) {
        PlannerApp.getTaskManager().removeTask(task);
        PlannerApp.getTaskManager().addCompletedTask(task);
    }

    private void playSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    private String getPriorityColor(String priority) {
        switch (priority.toUpperCase()) {
            case "HIGH":
                return "\u001B[31m"; // Red
            case "MEDIUM":
                return "\u001B[34m"; // Blue
            case "LOW":
                return "\u001B[33m"; // Yellow
            default:
                return "\u001B[0m"; // Reset
        }
    }
}
