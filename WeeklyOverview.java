import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

public class WeeklyOverview {
    public void execute() {
        int totalTasks = 0;
        int completedTasks = 0;
        int missedTasks = 0;
        int weeklyCompletedTasks = 0;
        int weeklyTotalTasks = 0;
        int monthlyCompletedTasks = 0;
        int monthlyTotalTasks = 0;

        // Get current date
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Get start of the week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startOfWeek = calendar.getTime();

        // Get start of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Read tasks from tasks.txt to get the total tasks
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalTasks++;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Date taskDate = sdf.parse(parts[2]);
                    if (!taskDate.before(startOfWeek)) {
                        weeklyTotalTasks++;
                    }
                    if (!taskDate.before(startOfMonth)) {
                        monthlyTotalTasks++;
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error reading tasks file: " + e.getMessage());
        }

        // Read tasks from completed_tasks.txt to get the completed tasks
        try (BufferedReader reader = new BufferedReader(new FileReader("completed_tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                completedTasks++;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Date taskDate = sdf.parse(parts[2]);
                    if (!taskDate.before(startOfWeek)) {
                        weeklyCompletedTasks++;
                    }
                    if (!taskDate.before(startOfMonth)) {
                        monthlyCompletedTasks++;
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error reading completed tasks file: " + e.getMessage());
        }

        missedTasks = totalTasks - completedTasks;
        double completionRate = totalTasks == 0 ? 0 : ((double) completedTasks / totalTasks) * 100;
        double weeklyCompletionRate = weeklyTotalTasks == 0 ? 0 : ((double) weeklyCompletedTasks / weeklyTotalTasks) * 100;
        double monthlyCompletionRate = monthlyTotalTasks == 0 ? 0 : ((double) monthlyCompletedTasks / monthlyTotalTasks) * 100;
        double productivityScore = monthlyCompletionRate / 10;

        // Display the overview statistics
        System.out.println("User Overview:");
        System.out.println("Total Tasks: " + totalTasks);
        System.out.println("Completed Tasks: " + completedTasks);
        System.out.println("Missed Tasks: " + missedTasks);
        System.out.println("Completion Rate: " + String.format("%.2f", completionRate) + "%");
        System.out.println("Weekly Completion Rate: " + String.format("%.2f", weeklyCompletionRate) + "%");
        System.out.println("Monthly Completion Rate: " + String.format("%.2f", monthlyCompletionRate) + "%");
        System.out.println("Productivity Score (out of 10): " + String.format("%.2f", productivityScore));

        // Provide a personalized inspirational message based on the productivity score
        String message = getInspirationalMessage(productivityScore);
        System.out.println("Inspirational Message: " + message);

        // Option to go back to home page
        new PlannerApp().backToHomePage();
    }

    private String getInspirationalMessage(double productivityScore) {
        if (productivityScore >= 9) {
            return "Excellent work! Keep up the great productivity!";
        } else if (productivityScore >= 7) {
            return "Great job! You're doing really well!";
        } else if (productivityScore >= 5) {
            return "Good effort! Keep pushing to improve!";
        } else if (productivityScore >= 3) {
            return "You're making progress! Stay focused and keep going!";
        } else {
            return "Don't give up! Every step forward is progress!";
        }
    }
}
