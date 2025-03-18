import java.util.ArrayList;
import java.util.List;

public class TimeBlock {
    private String description;
    private String startTime;
    private String endTime;
    private List<TimeBlockEntry> timeBlocks;

    // No-argument constructor
    public TimeBlock() {
        this.timeBlocks = new ArrayList<>();
    }

    public TimeBlock(String description, String startTime, String endTime) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeBlocks = new ArrayList<>();
    }

    public void addTimeBlock(String description, String startTime, String endTime) {
        TimeBlockEntry block = new TimeBlockEntry(description, startTime, endTime);
        timeBlocks.add(block);
    }

    public List<TimeBlockEntry> getTimeBlocks() {
        return new ArrayList<>(timeBlocks);
    }

    // Rename Block to TimeBlockEntry for clarity
    public static class TimeBlockEntry {
        private final String description;
        private final String startTime;
        private final String endTime;

        public TimeBlockEntry(String description, String startTime, String endTime) {
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getDescription() {
            return description;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return description + ": " + startTime + " - " + endTime;
        }
    }
}
