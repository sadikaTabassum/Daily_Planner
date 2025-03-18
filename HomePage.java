import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class HomePage {
    public void execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nHome Page:");
        System.out.println("1. Add Task");
        System.out.println("2. Remove Task");
        System.out.println("3. Mark Task as Done");
        System.out.println("4. View Tasks for Today");
        System.out.println("5. Generate Routine");
        System.out.println("6. Pomodoro Clock");
        System.out.println("7. User Overview");
        System.out.println("8. Missed Task Rescheduling");
        System.out.println("9. Log Out");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> new AddTask().execute();
            case 2 -> new RemoveTask().execute();
            case 3 -> new MarkTaskAsDone().execute();
            case 4 -> new ViewTasksForToday().execute();
            case 5 -> new GenerateRoutine().execute();
            case 6 -> new PomodoroMenu().execute();
            case 7 -> new WeeklyOverview().execute();
            case 8 -> rescheduleMissedTasks();
            case 9 -> PlannerApp.logout();
            default -> System.out.println("Invalid option. Returning to Home.");
        }
    }

    private void rescheduleMissedTasks() {
        Scanner scanner = new Scanner(System.in);
        List<Task> missedTasks = getMissedTasksFromFile();
        if (missedTasks.isEmpty()) {
            System.out.println("No missed tasks to reschedule.");
            return;
        }

        System.out.println("Missed Tasks:");
        for (int i = 0; i < missedTasks.size(); i++) {
            Task task = missedTasks.get(i);
            System.out.println((i + 1) + ". " + task.getDescription() + " (Deadline: " + task.getDeadline() + ", Start Time: " + task.getStartTime() + ")");
        }

        System.out.print("Enter the number of the task you want to reschedule (or 0 to cancel): ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine();

        if (taskNumber == 0) {
            return;
        }

        if (taskNumber < 1 || taskNumber > missedTasks.size()) {
            System.out.println("Invalid task number. Please try again.");
            return;
        }

        Task taskToReschedule = missedTasks.get(taskNumber - 1);
        String rescheduledTime = findNearestFreeTime(taskToReschedule.getDeadline(), taskToReschedule.getStartTime(), taskToReschedule.getDuration());
        if (rescheduledTime != null) {
            String newDeadline = rescheduledTime.split(" ")[0];
            String newStartTime = rescheduledTime.split(" ")[1];
            System.out.println("Do you want to reassign the task \"" + taskToReschedule.getDescription() + "\" on " + newDeadline + " at " + newStartTime + "? (y/n):");
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("y")) {
                taskToReschedule.setDeadline(newDeadline);
                taskToReschedule.setStartTime(newStartTime);
                System.out.println("Task reassigned successfully.");
            } else {
                System.out.println("Task rescheduling canceled.");
            }
        } else {
            System.out.println("No available free time found for rescheduling.");
        }
    }

    private List<Task> getMissedTasksFromFile() {
        List<Task> missedTasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("missed_tasks.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String description = parts[0];
                    String priority = parts[1];
                    String deadline = parts[2];
                    String startTime = parts[3];
                    int duration = Integer.parseInt(parts[4]);
                    boolean isRecurring = Boolean.parseBoolean(parts[5]);
                    Task task = new Task(description, priority, deadline, startTime, duration, isRecurring);
                    missedTasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading missed tasks file: " + e.getMessage());
        }
        return missedTasks;
    }

    private String findNearestFreeTime(String deadline, String startTime, int duration) {
        List<String> freeTimes = getFreeTimeSlotsFromRoutine();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date newTaskStart = sdf.parse(deadline + " " + startTime);
            Date newTaskEnd = new Date(newTaskStart.getTime() + duration * 60 * 60 * 1000);

            for (String freeTime : freeTimes) {
                String[] timeParts = freeTime.split(" - ");
                Date freeTimeStart = sdf.parse(deadline + " " + timeParts[0]);
                Date freeTimeEnd = sdf.parse(deadline + " " + timeParts[1]);

                if (newTaskEnd.before(freeTimeStart)) {
                    return sdf.format(freeTimeStart);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> getFreeTimeSlotsFromRoutine() {
        GenerateRoutine generateRoutine = new GenerateRoutine();
        return generateRoutine.getFreeTimeSlots();
    }
}
