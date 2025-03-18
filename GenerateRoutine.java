import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateRoutine {
    private TaskManager taskManager;
    private UserManager userManager;
    private List<TimeBlock> timeBlocks;
    private TimeBlock timeBlockManager;


    public GenerateRoutine() {
        this.taskManager = PlannerApp.getTaskManager();
        this.userManager = PlannerApp.getUserManager();
        this.timeBlocks = new ArrayList<>();
        this.timeBlockManager = PlannerApp.getTimeBlockManager();
        ensureManagersInitialized();
    }


    private void ensureManagersInitialized() {
        if (taskManager == null) {
            throw new IllegalStateException("TaskManager must be initialized");
        }
        if (userManager == null) {
            throw new IllegalStateException("UserManager must be initialized");
        }
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the day of the week for which you want to generate the routine:");
        String dayOfWeek = scanner.nextLine();
        generateRoutine(dayOfWeek);


        System.out.println("Press any key to go back to the home page.");
        scanner.nextLine();
        PlannerApp.backToHomePage();
    }

    public void generateRoutine(String dayOfWeek) {
        List<String> timeSlots = new ArrayList<>(Collections.nCopies(24, "Free time"));

        setFixedActivities(timeSlots, dayOfWeek);
        assignTasksToRoutine(timeSlots, dayOfWeek);
        displayRoutine(timeSlots);
    }

    private void setFixedActivities(List<String> timeSlots, String dayOfWeek) {
        boolean isWeekend = dayOfWeek.equalsIgnoreCase("Saturday") || dayOfWeek.equalsIgnoreCase("Sunday");

        if (!isWeekend) {

            for (int i = 8; i < 18; i++) {
                timeSlots.set(i, "University");
                timeBlocks.add(new TimeBlock("University", i + ":00", (i + 1) + ":00"));
            }


            timeSlots.set(13, "Lunch");
            timeBlocks.add(new TimeBlock("Lunch", "13:00", "14:00"));


            for (int i = 18; i < 20; i++) {
                timeSlots.set(i, "Free time");
                timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
            }


            boolean isMorningPerson = userManager.isMorningPerson();
            if (isMorningPerson) {
                timeSlots.set(20, "Dinner");
                timeBlocks.add(new TimeBlock("Dinner", "20:00", "21:00"));
            } else {
                timeSlots.set(21, "Dinner");
                timeBlocks.add(new TimeBlock("Dinner", "21:00", "22:00"));
            }


            String sleepStartTime = userManager.getSleepStartTime();
            int sleepStartHour = Integer.parseInt(sleepStartTime.split(":")[0]);
            if (isMorningPerson) {
                for (int i = 21; i < sleepStartHour; i++) {
                    timeSlots.set(i, "Free time");
                    timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
                }
            } else {
                for (int i = 22; i < sleepStartHour; i++) {
                    timeSlots.set(i, "Free time");
                    timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
                }
            }


            setSleepingTime(timeSlots, sleepStartHour);
        } else {

            for (int i = 8; i < 12; i++) {
                timeSlots.set(i, "Free time");
                timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
            }


            timeSlots.set(13, "Lunch");
            timeBlocks.add(new TimeBlock("Lunch", "13:00", "14:00"));


            for (int i = 14; i < 17; i++) {
                timeSlots.set(i, "Free time");
                timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
            }


            boolean isMorningPerson = userManager.isMorningPerson();
            if (isMorningPerson) {
                timeSlots.set(20, "Dinner");
                timeBlocks.add(new TimeBlock("Dinner", "20:00", "21:00"));
            } else {
                timeSlots.set(21, "Dinner");
                timeBlocks.add(new TimeBlock("Dinner", "21:00", "22:00"));
            }


            String sleepStartTime = userManager.getSleepStartTime();
            int sleepStartHour = Integer.parseInt(sleepStartTime.split(":")[0]);
            if (isMorningPerson) {
                for (int i = 21; i < sleepStartHour; i++) {
                    timeSlots.set(i, "Free time");
                    timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
                }
            } else {
                for (int i = 22; i < sleepStartHour; i++) {
                    timeSlots.set(i, "Free time");
                    timeBlocks.add(new TimeBlock("Free time", i + ":00", (i + 1) + ":00"));
                }
            }


            setSleepingTime(timeSlots, sleepStartHour);
        }
    }

    private void setSleepingTime(List<String> timeSlots, int sleepStartHour) {
        String sleepEndTime = userManager.getSleepEndTime();
        int sleepEndHour = Integer.parseInt(sleepEndTime.split(":")[0]);

        if (sleepStartHour < sleepEndHour) {
            for (int i = sleepStartHour; i < sleepEndHour; i++) {
                timeSlots.set(i, "Sleeping");
                timeBlocks.add(new TimeBlock("Sleeping", i + ":00", (i + 1) + ":00"));
            }
        } else {
            for (int i = sleepStartHour; i < 24; i++) {
                timeSlots.set(i, "Sleeping");
                timeBlocks.add(new TimeBlock("Sleeping", i + ":00", (i + 1) + ":00"));
            }
            for (int i = 0; i < sleepEndHour; i++) {
                timeSlots.set(i, "Sleeping");
                timeBlocks.add(new TimeBlock("Sleeping", i + ":00", (i + 1) + ":00"));
            }
        }
    }

    private void assignTasksToRoutine(List<String> timeSlots, String dayOfWeek) {
        List<Task> tasks = taskManager.getTasks();

        for (Task task : tasks) {
            if (task.getDeadline().equalsIgnoreCase(dayOfWeek)) {
                int taskStartHour = Integer.parseInt(task.getStartTime().split(":")[0]);
                int taskDuration = task.getDuration();

                for (int i = 0; i < taskDuration; i++) {
                    int currentHour = taskStartHour + i;
                    if (currentHour < 24 && !timeSlots.get(currentHour).equals("University") && !timeSlots.get(currentHour).equals("Sleeping")) {
                        if (timeSlots.get(currentHour).equals("Free time")) {
                            timeSlots.set(currentHour, task.getDescription());
                            timeBlocks.add(new TimeBlock(task.getDescription(), currentHour + ":00", (currentHour + 1) + ":00"));
                            timeBlockManager.addTimeBlock(task.getDescription(), currentHour + ":00", (currentHour + 1) + ":00");
                        } else {
                            timeSlots.set(currentHour, timeSlots.get(currentHour) + " / " + task.getDescription());
                            timeBlocks.add(new TimeBlock(timeSlots.get(currentHour), currentHour + ":00", (currentHour + 1) + ":00"));
                            timeBlockManager.addTimeBlock(timeSlots.get(currentHour), currentHour + ":00", (currentHour + 1) + ":00");
                        }
                    }
                }
            }
        }
    }

    private void displayRoutine(List<String> timeSlots) {
        for (int i = 0; i < timeSlots.size(); i++) {
            System.out.println(i + ":00 - " + timeSlots.get(i));
        }
    }

    public List<String> getFreeTimeSlots() {
        List<String> freeTimeSlots = new ArrayList<>();
        List<String> timeSlots = new ArrayList<>(Collections.nCopies(24, "Free time"));


        String dayOfWeek = "Monday";
        setFixedActivities(timeSlots, dayOfWeek);
        assignTasksToRoutine(timeSlots, dayOfWeek);


        for (int i = 0; i < timeSlots.size(); i++) {
            if (timeSlots.get(i).equals("Free time")) {
                freeTimeSlots.add(i + ":00 - " + (i + 1) + ":00");
            }
        }

        return freeTimeSlots;
    }
}
