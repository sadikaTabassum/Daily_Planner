public class Task {
    private String description;
    private String priority;
    private String deadline;  
    private String startTime;  
    private int duration;  
    private boolean isRecurring;

    public Task(String description, String priority, String deadline, String startTime, int duration, boolean isRecurring) {
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.startTime = startTime;
        this.duration = duration;
        this.isRecurring = isRecurring;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", deadline='" + deadline + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration=" + duration +
                ", isRecurring=" + isRecurring +
                '}';
    }
}
