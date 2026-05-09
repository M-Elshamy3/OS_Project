package model;
import java.util.List;

public class SchedulerModels {

    private SchedulerModels() {
    }

    public static class Process {
        private final String pid;
        private final int arrival;
        private final int burst;
        private final int priority;
        private final int inputOrder;

        public Process(String pid, int arrival, int burst, int priority, int inputOrder) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.priority = priority;
            this.inputOrder = inputOrder;
        }

        public String getPid() {
            return pid;
        }

        public int getArrival() {
            return arrival;
        }

        public int getBurst() {
            return burst;
        }

        public int getPriority() {
            return priority;
        }

        public int getInputOrder() {
            return inputOrder;
        }
    }

    public static class ResultRow {
        private final String pid;
        private final int arrival;
        private final int burst;
        private final int priority;
        private final int start;
        private final int completion;
        private final int waiting;
        private final int turnaround;
        private final int response;

        public ResultRow(String pid, int arrival, int burst, int priority, int start,
                         int completion, int waiting, int turnaround, int response) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.priority = priority;
            this.start = start;
            this.completion = completion;
            this.waiting = waiting;
            this.turnaround = turnaround;
            this.response = response;
        }

        public String getPid() {
            return pid;
        }
        public int getArrival() {
            return arrival;
        }
        public int getBurst() {
            return burst;
        }
        public int getPriority() {
            return priority;
        }
        public int getStart() {
            return start;
        }
        public int getCompletion() {
            return completion;
        }
        public int getWaiting() {
            return waiting;
        }
        public int getTurnaround() {
            return turnaround;
        }
        public int getResponse() {
            return response;
        }
    }
    public static class Segment {
        private final String pid;
        private final int start;
        private int end;
        public Segment(String pid, int start, int end) {
            this.pid = pid;
            this.start = start;
            this.end = end;
        }
        public String getPid() {
            return pid;
        }
        public int getStart() {
            return start;
        }
        public int getEnd() {
            return end;
        }
        public void setEnd(int end) {
            this.end = end;
        }
    }
    public static class Metrics {
        private final double avgWT;
        private final double avgTAT;
        private final double avgRT;
        public Metrics(double avgWT, double avgTAT, double avgRT) {
            this.avgWT = avgWT;
            this.avgTAT = avgTAT;
            this.avgRT = avgRT;
        }
        public double getAvgWT() {
            return avgWT;
        }
        public double getAvgTAT() {
            return avgTAT;
        }
        public double getAvgRT() {
            return avgRT;
        }
    }
    public static class ScheduleOutput {
        private final List<ResultRow> rows;
        private final List<Segment> gantt;
        public ScheduleOutput(List<ResultRow> rows, List<Segment> gantt) {
            this.rows = rows;
            this.gantt = gantt;
        }
        public List<ResultRow> getRows() {
            return rows;
        }
        public List<Segment> getGantt() {
            return gantt;
        }
    }
}