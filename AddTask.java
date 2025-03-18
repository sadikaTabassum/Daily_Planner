import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddTask {
    private AddReminder addReminder = new AddReminder();
    private TimeBlock timeBlocker = new TimeBlock();

    public void execute() {
        Scanner scanner = new Scanner(System.in);

        // Collect task information
        System.out.println("Enter task description:");
        String description = scanner.nextLine();

        // Validate task priority input
        String priority;
        while (true) {
            System.out.println("Enter task priority as High, Medium or Low (H/M/L):");
            priority = scanner.nextLine().toUpperCase();
            if (priority.equals("H") || priority.equals("M") || priority.equals("L")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter H, M, or L.");
            }
        }

        // Validate task deadline input
        String deadline;
        while (true) {
            System.out.println("Enter task deadline (YYYY-MM-DD):");
            deadline = scanner.nextLine();
            if (isValidDate(deadline) && !isPastDate(deadline)) {
                break;
            } else {
                System.out.println("Invalid date or date is in the past. Please enter a valid future date in YYYY-MM-DD format.");
            }
        }

        // Validate task start time input
        String startTime;
        while (true) {
            System.out.println("Enter task start time (24H format, HH:mm):");
            startTime = scanner.nextLine();
            if (isValidTime(startTime)) {
                break;
            } else {
                System.out.println("Invalid time format. Please enter a valid time in HH:mm format.");
            }
        }

        System.out.println("Enter task duration (in hours):");
        int duration = scanner.nextInt();  // Duration in hours
        scanner.nextLine();  // Consume the newline

        // Check for conflicts with existing tasks
        Task conflictingTask = getConflictingTask(deadline, startTime, duration);
        if (conflictingTask != null) {
            if (isHigherPriority(conflictingTask.getPriority(), priority)) {
                System.out.println("The existing task has a higher priority. Rescheduling the new task...");
                String rescheduledTime = findNearestFreeTime(deadline, startTime, duration);
                if (rescheduledTime != null) {
                    String newDeadline = rescheduledTime.split(" ")[0];
                    String newStartTime = rescheduledTime.split(" ")[1];
                    System.out.println("Do you want to reassign the task \"" + description + "\" on " + newDeadline + " at " + newStartTime + "? (y/n):");
                    String confirmation = scanner.nextLine();
                    if (confirmation.equalsIgnoreCase("y")) {
                        deadline = newDeadline;
                        startTime = newStartTime;
                        System.out.println("Task reassigned to " + deadline + " at " + startTime + ". Description: " + description);
                    } else {
                        System.out.println("Task rescheduling canceled.");
                        return;
                    }
                } else {
                    System.out.println("No available free time found for rescheduling.");
                    return;
                }
            } else {
                System.out.println("The new task has a higher priority. Rescheduling the existing task...");
                String rescheduledTime = findNearestFreeTime(conflictingTask.getDeadline(), conflictingTask.getStartTime(), conflictingTask.getDuration());
                if (rescheduledTime != null) {
                    String newDeadline = rescheduledTime.split(" ")[0];
                    String newStartTime = rescheduledTime.split(" ")[1];
                    System.out.println("Do you want to reassign the existing task \"" + conflictingTask.getDescription() + "\" on " + newDeadline + " at " + newStartTime + "? (y/n):");
                    String confirmation = scanner.nextLine();
                    if (confirmation.equalsIgnoreCase("y")) {
                        conflictingTask.setDeadline(newDeadline);
                        conflictingTask.setStartTime(newStartTime);
                        System.out.println("Existing task reassigned to " + newDeadline + " at " + newStartTime + ". Description: " + conflictingTask.getDescription());
                    } else {
                        System.out.println("Task rescheduling canceled.");
                        return;
                    }
                } else {
                    System.out.println("No available free time found for rescheduling the existing task.");
                    return;
                }
            }
        }

        System.out.println("Is the task recurring? (true/false):");
        boolean isRecurring = scanner.nextBoolean();
        scanner.nextLine();  // Consume the newline

        int intervalDays = 0;
        int repetitions = 0;
        if (isRecurring) {
            System.out.println("Enter the interval in days for the recurring task:");
            intervalDays = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            System.out.println("Enter the number of repetitions for the recurring task:");
            repetitions = scanner.nextInt();
            scanner.nextLine();  // Consume the newline
        }

        // Create and add the task
        Task task = new Task(description, priority, deadline, startTime, duration, isRecurring);
        PlannerApp.getTaskManager().addTask(task);

        // Add the task to the time blocker
        timeBlocker.addTimeBlock(description, startTime, calculateEndTime(startTime, duration));

        // If the task is recurring, add repeated tasks to tasks.txt
        if (isRecurring) {
            addRecurringTasks(task, intervalDays, repetitions);
        }

        System.out.println("Task added successfully.");

        // Prompt the user for reminder
        while (true) {
            System.out.println("Do you want to add a reminder? (y/n):");
            String addReminderChoice = scanner.nextLine();
            if (addReminderChoice.equalsIgnoreCase("y")) {
                System.out.println("When should you be reminded?");
                System.out.println("1. Minutes before");
                System.out.println("2. Hours before");
                System.out.println("3. Days before");
                System.out.println("Enter your choice:");
                int reminderChoice = scanner.nextInt();
                scanner.nextLine();  // Consume the newline

                int reminderMinutes = 0;
                switch (reminderChoice) {
                    case 1:
                        System.out.println("Enter the number of minutes before the task for the reminder:");
                        reminderMinutes = scanner.nextInt();
                        scanner.nextLine();  // Consume the newline
                        break;
                    case 2:
                        System.out.println("Enter the number of hours before the task for the reminder:");
                        int reminderHours = scanner.nextInt();
                        scanner.nextLine();  // Consume the newline
                        reminderMinutes = reminderHours * 60;
                        break;
                    case 3:
                        System.out.println("Enter the number of days before the task for the reminder:");
                        int reminderDays = scanner.nextInt();
                        scanner.nextLine();  // Consume the newline
                        reminderMinutes = reminderDays * 24 * 60;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                        continue;  // Go back to the reminder options
                }

                if (reminderMinutes > 0) {
                    // Validate if the reminder time is feasible
                    if (isReminderFeasible(task, reminderMinutes)) {
                        addReminder.scheduleReminder(task, reminderMinutes);
                        System.out.println("Reminder set for " + reminderMinutes + " minutes before the task.");
                    } else {
                        System.out.println("Reminder time is not feasible. No reminder set.");
                    }
                }
                break;  // Exit the loop after setting the reminder
            } else if (addReminderChoice.equalsIgnoreCase("n")) {
                break;  // Exit the loop if the user chooses not to set a reminder
            } else {
                System.out.println("Invalid choice. Please enter 'y' or 'n'.");
            }
        }

        // Prompt the user for the next action
        System.out.println("1. Add another task");
        System.out.println("2. Back to home page");
        System.out.println("Enter your choice:");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume the newline
        if (choice == 1) {
            execute();
        } else {
            PlannerApp.backToHomePage();
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isPastDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date inputDate = sdf.parse(date);
            Date today = sdf.parse(sdf.format(new Date()));
            return inputDate.before(today);
        } catch (ParseException e) {
            return true;
        }
    }

    private boolean isValidTime(String time) {
        return time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    private boolean isReminderFeasible(Task task, int reminderMinutes) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date taskDate = sdf.parse(task.getDeadline() + " " + task.getStartTime());
            long reminderTime = taskDate.getTime() - reminderMinutes * 60 * 1000;
            return reminderTime > System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task getConflictingTask(String deadline, String startTime, int duration) {
        List<Task> tasks = PlannerApp.getTaskManager().getTasks();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date newTaskStart = sdf.parse(deadline + " " + startTime);
            Date newTaskEnd = new Date(newTaskStart.getTime() + duration * 60 * 60 * 1000);

            for (Task task : tasks) {
                Date taskStart = sdf.parse(task.getDeadline() + " " + task.getStartTime());
                Date taskEnd = new Date(taskStart.getTime() + task.getDuration() * 60 * 60 * 1000);

                if (newTaskStart.before(taskEnd) && newTaskEnd.after(taskStart)) {
                    return task;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isHigherPriority(String existingPriority, String newPriority) {
        List<String> priorityOrder = Arrays.asList("L", "M", "H");
        return priorityOrder.indexOf(existingPriority) > priorityOrder.indexOf(newPriority);
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

    private void addRecurringTasks(Task task, int intervalDays, int repetitions) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(task.getDeadline()));
            for (int i = 1; i <= repetitions; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, intervalDays);
                String newDeadline = sdf.format(calendar.getTime());
                Task recurringTask = new Task(task.getDescription(), task.getPriority(), newDeadline, task.getStartTime(), task.getDuration(), task.isRecurring());
                PlannerApp.getTaskManager().addTask(recurringTask);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String calculateEndTime(String startTime, int duration) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(startTime));
            calendar.add(Calendar.HOUR_OF_DAY, duration);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
