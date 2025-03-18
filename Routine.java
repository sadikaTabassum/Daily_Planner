import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Routine {
    private List<Task> tasks;


    public Routine() {
        tasks = new ArrayList<>();
    }


    public void generateMorningRoutine() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the time you wake up (e.g., 7:00 AM):");
        String wakeUpTime = scanner.nextLine();
        System.out.println("Morning Routine: Wake up at " + wakeUpTime);


        addTaskToRoutine(scanner, "First task");
        addTaskToRoutine(scanner, "Second task");
    }


    public void generateNightRoutine() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the time you go to bed (e.g., 11:00 PM):");
        String bedTime = scanner.nextLine();
        System.out.println("Night Routine: Go to bed at " + bedTime);


        addTaskToRoutine(scanner, "First task");
        addTaskToRoutine(scanner, "Second task");
    }


    private void addTaskToRoutine(Scanner scanner, String taskLabel) {
        System.out.println("Enter your " + taskLabel + " (e.g., Study, Exercise, etc.):");
        String taskDescription = scanner.nextLine();

        System.out.println("Enter time for " + taskLabel + " (e.g., 9:00 AM):");
        String taskTime = scanner.nextLine();

        System.out.println("Enter priority for " + taskLabel + " (high, medium, low):");
        String taskPriority = scanner.nextLine().toLowerCase();

        System.out.println("Is the task recurring? (true/false):");
        boolean isRecurring = scanner.nextBoolean();
        scanner.nextLine();


        String deadline = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        int duration = 0;


        tasks.add(new Task(taskDescription, taskPriority, deadline, taskTime, duration, isRecurring));
    }


    public void displayRoutine() {
        System.out.println("Your routine:");
        for (Task task : tasks) {
            System.out.println(task);
        }
    }


    public List<String> getFreeTimeSlots() {
        List<String> freeTimeSlots = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Collections.sort(tasks, Comparator.comparing(Task::getStartTime));

        try {
            Date startOfDay = sdf.parse("00:00");
            Date endOfDay = sdf.parse("23:59");


            if (!tasks.isEmpty()) {
                Date firstTaskStart = sdf.parse(tasks.get(0).getStartTime());
                if (startOfDay.before(firstTaskStart)) {
                    freeTimeSlots.add(sdf.format(startOfDay) + " - " + sdf.format(firstTaskStart));
                }
            }


            for (int i = 0; i < tasks.size() - 1; i++) {
                Date currentTaskEnd = sdf.parse(tasks.get(i).getStartTime());
                currentTaskEnd.setTime(currentTaskEnd.getTime() + tasks.get(i).getDuration() * 60 * 60 * 1000);
                Date nextTaskStart = sdf.parse(tasks.get(i + 1).getStartTime());

                if (currentTaskEnd.before(nextTaskStart)) {
                    freeTimeSlots.add(sdf.format(currentTaskEnd) + " - " + sdf.format(nextTaskStart));
                }
            }


            if (!tasks.isEmpty()) {
                Date lastTaskEnd = sdf.parse(tasks.get(tasks.size() - 1).getStartTime());
                lastTaskEnd.setTime(lastTaskEnd.getTime() + tasks.get(tasks.size() - 1).getDuration() * 60 * 60 * 1000);
                if (lastTaskEnd.before(endOfDay)) {
                    freeTimeSlots.add(sdf.format(lastTaskEnd) + " - " + sdf.format(endOfDay));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return freeTimeSlots;
    }
}
