import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class MarkTaskAsDone {
    public void execute() {
        Scanner scanner = new Scanner(System.in);

        // Fetch all tasks from tasks.txt
        List<Task> tasks = PlannerApp.getTaskManager().getTasks();

        // Display all tasks with their indices
        System.out.println("Here are all the upcoming tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }

        System.out.println("Enter the number of the task you want to mark as done:");
        int taskNumber = scanner.nextInt();

        // Validate the input
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task taskToMark = tasks.get(taskNumber - 1);
        markTaskAsDone(taskToMark);
        playSound("taskDone.wav");

        System.out.println("Task marked as done successfully.");

        System.out.println("1. Mark another task as done");
        System.out.println("2. Back to home page");
        System.out.println("Enter your choice:");
        int choice = scanner.nextInt();
        if (choice == 1) {
            new MarkTaskAsDone().execute();
        } else {
            new PlannerApp().backToHomePage();
        }
    }

    // Method to mark task as done by its description
    private void markTaskAsDone(Task task) {
        // Remove the task from the tasks list
        PlannerApp.getTaskManager().removeTask(task);

        // Add the task to the completed tasks list
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
}
