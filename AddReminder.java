import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AddReminder {
    private Timer timer;

    public AddReminder() {
        timer = new Timer();
    }

    public void scheduleReminder(Task task, int reminderMinutes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date taskDate = sdf.parse(task.getDeadline() + " " + task.getStartTime());
            long reminderTime = taskDate.getTime() - TimeUnit.MINUTES.toMillis(reminderMinutes);

            if (reminderTime > System.currentTimeMillis()) {
                timer.schedule(new ReminderTask(task), new Date(reminderTime));
            } else {
                System.out.println("Reminder time has already passed. No reminder set.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ReminderTask extends TimerTask {
        private Task task;

        public ReminderTask(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            System.out.println("Reminder: " + task.getDescription() + " is starting soon!");
            showNotification("Task Reminder", "Reminder: " + task.getDescription() + " is starting soon!");
        }
    }

    private void showNotification(String title, String message) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // You can use any image file here
            TrayIcon trayIcon = new TrayIcon(image, "Task Reminder");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Task Reminder");

            try {
                tray.add(trayIcon);
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
                Thread.sleep(5000);
                tray.remove(trayIcon);
            } catch (AWTException | InterruptedException e) {
                System.err.println("Error displaying notification: " + e.getMessage());
            }
        } else {
            System.err.println("System tray not supported!");
        }
    }

    public static void main(String[] args) {
        // Example usage
        Task task = new Task("Study", "High", "2023-10-01", "08:00", 2, false);
        AddReminder addReminder = new AddReminder();
        addReminder.scheduleReminder(task, 15); // Set a reminder 15 minutes before the task
    }
}