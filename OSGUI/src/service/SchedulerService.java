package service;
import model.SchedulerModels.Metrics;
import model.SchedulerModels.Process;
import model.SchedulerModels.ResultRow;
import model.SchedulerModels.ScheduleOutput;
import model.SchedulerModels.Segment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class SchedulerService {

    public ScheduleOutput runPreemptiveSJF(List<Process> processes) {
        int n = processes.size();

        int[] remaining = new int[n];
        int[] firstStart = new int[n];
        int[] completion = new int[n];
        boolean[] done = new boolean[n];

        Arrays.fill(firstStart, -1);

        for (int i = 0; i < n; i++) {
            remaining[i] = processes.get(i).getBurst();
        }

        int finished = 0;
        int time = 0;

        List<Segment> gantt = new ArrayList<>();

        while (finished < n) {
            int chosen = choosePreemptiveSJF(processes, remaining, done, time);

            if (chosen == -1) {
                appendSegment(gantt, "IDLE", time, time + 1);
                time++;
                continue;
            }

            if (firstStart[chosen] == -1) {
                firstStart[chosen] = time;
            }

            appendSegment(gantt, processes.get(chosen).getPid(), time, time + 1);

            remaining[chosen]--;
            time++;

            if (remaining[chosen] == 0) {
                done[chosen] = true;
                completion[chosen] = time;
                finished++;
            }
        }

        return new ScheduleOutput(buildRows(processes, firstStart, completion), gantt);
    }

    public ScheduleOutput runNonPreemptiveSJF(List<Process> processes) {
        int n = processes.size();

        int[] start = new int[n];
        int[] completion = new int[n];
        boolean[] done = new boolean[n];

        Arrays.fill(start, -1);

        int finished = 0;
        int time = 0;

        List<Segment> gantt = new ArrayList<>();

        while (finished < n) {
            int chosen = chooseNonPreemptiveSJF(processes, done, time);

            if (chosen == -1) {
                appendSegment(gantt, "IDLE", time, time + 1);
                time++;
                continue;
            }

            start[chosen] = time;

            int burst = processes.get(chosen).getBurst();

            appendSegment(gantt, processes.get(chosen).getPid(), time, time + burst);

            time += burst;
            completion[chosen] = time;
            done[chosen] = true;
            finished++;
        }

        return new ScheduleOutput(buildRows(processes, start, completion), gantt);
    }

    public ScheduleOutput runPreemptivePriority(List<Process> processes) {
        int n = processes.size();

        int[] remaining = new int[n];
        int[] firstStart = new int[n];
        int[] completion = new int[n];
        boolean[] done = new boolean[n];

        Arrays.fill(firstStart, -1);

        for (int i = 0; i < n; i++) {
            remaining[i] = processes.get(i).getBurst();
        }

        int finished = 0;
        int time = 0;

        List<Segment> gantt = new ArrayList<>();

        while (finished < n) {
            int chosen = choosePreemptivePriority(processes, remaining, done, time);

            if (chosen == -1) {
                appendSegment(gantt, "IDLE", time, time + 1);
                time++;
                continue;
            }

            if (firstStart[chosen] == -1) {
                firstStart[chosen] = time;
            }

            appendSegment(gantt, processes.get(chosen).getPid(), time, time + 1);

            remaining[chosen]--;
            time++;

            if (remaining[chosen] == 0) {
                done[chosen] = true;
                completion[chosen] = time;
                finished++;
            }
        }

        return new ScheduleOutput(buildRows(processes, firstStart, completion), gantt);
    }

    public ScheduleOutput runNonPreemptivePriority(List<Process> processes) {
        int n = processes.size();

        int[] start = new int[n];
        int[] completion = new int[n];
        boolean[] done = new boolean[n];

        Arrays.fill(start, -1);

        int finished = 0;
        int time = 0;

        List<Segment> gantt = new ArrayList<>();

        while (finished < n) {
            int chosen = chooseNonPreemptivePriority(processes, done, time);

            if (chosen == -1) {
                appendSegment(gantt, "IDLE", time, time + 1);
                time++;
                continue;
            }

            start[chosen] = time;

            int burst = processes.get(chosen).getBurst();

            appendSegment(gantt, processes.get(chosen).getPid(), time, time + burst);

            time += burst;
            completion[chosen] = time;
            done[chosen] = true;
            finished++;
        }

        return new ScheduleOutput(buildRows(processes, start, completion), gantt);
    }

    private int choosePreemptiveSJF(List<Process> processes, int[] remaining, boolean[] done, int time) {
        int chosen = -1;

        for (int i = 0; i < processes.size(); i++) {
            if (!done[i] && processes.get(i).getArrival() <= time && remaining[i] > 0) {
                if (chosen == -1) {
                    chosen = i;
                } else if (remaining[i] < remaining[chosen]) {
                    chosen = i;
                } else if (remaining[i] == remaining[chosen]) {
                    if (processes.get(i).getArrival() < processes.get(chosen).getArrival()) {
                        chosen = i;
                    } else if (processes.get(i).getArrival() == processes.get(chosen).getArrival()
                            && processes.get(i).getInputOrder() < processes.get(chosen).getInputOrder()) {
                        chosen = i;
                    }
                }
            }
        }

        return chosen;
    }

    private int chooseNonPreemptiveSJF(List<Process> processes, boolean[] done, int time) {
        int chosen = -1;

        for (int i = 0; i < processes.size(); i++) {
            if (!done[i] && processes.get(i).getArrival() <= time) {
                if (chosen == -1) {
                    chosen = i;
                } else if (processes.get(i).getBurst() < processes.get(chosen).getBurst()) {
                    chosen = i;
                } else if (processes.get(i).getBurst() == processes.get(chosen).getBurst()) {
                    if (processes.get(i).getArrival() < processes.get(chosen).getArrival()) {
                        chosen = i;
                    } else if (processes.get(i).getArrival() == processes.get(chosen).getArrival()
                            && processes.get(i).getInputOrder() < processes.get(chosen).getInputOrder()) {
                        chosen = i;
                    }
                }
            }
        }

        return chosen;
    }

    private int choosePreemptivePriority(List<Process> processes, int[] remaining, boolean[] done, int time) {
        int chosen = -1;

        for (int i = 0; i < processes.size(); i++) {
            if (!done[i] && processes.get(i).getArrival() <= time && remaining[i] > 0) {
                if (chosen == -1) {
                    chosen = i;
                } else if (processes.get(i).getPriority() < processes.get(chosen).getPriority()) {
                    chosen = i;
                } else if (processes.get(i).getPriority() == processes.get(chosen).getPriority()) {
                    if (processes.get(i).getArrival() < processes.get(chosen).getArrival()) {
                        chosen = i;
                    } else if (processes.get(i).getArrival() == processes.get(chosen).getArrival()) {
                        if (remaining[i] < remaining[chosen]) {
                            chosen = i;
                        } else if (remaining[i] == remaining[chosen]
                                && processes.get(i).getInputOrder() < processes.get(chosen).getInputOrder()) {
                            chosen = i;
                        }
                    }
                }
            }
        }

        return chosen;
    }

    private int chooseNonPreemptivePriority(List<Process> processes, boolean[] done, int time) {
        int chosen = -1;

        for (int i = 0; i < processes.size(); i++) {
            if (!done[i] && processes.get(i).getArrival() <= time) {
                if (chosen == -1) {
                    chosen = i;
                } else if (processes.get(i).getPriority() < processes.get(chosen).getPriority()) {
                    chosen = i;
                } else if (processes.get(i).getPriority() == processes.get(chosen).getPriority()) {
                    if (processes.get(i).getArrival() < processes.get(chosen).getArrival()) {
                        chosen = i;
                    } else if (processes.get(i).getArrival() == processes.get(chosen).getArrival()
                            && processes.get(i).getInputOrder() < processes.get(chosen).getInputOrder()) {
                        chosen = i;
                    }
                }
            }
        }

        return chosen;
    }

    private List<ResultRow> buildRows(List<Process> processes, int[] start, int[] completion) {
        List<ResultRow> rows = new ArrayList<>();

        for (int i = 0; i < processes.size(); i++) {
            int tat = completion[i] - processes.get(i).getArrival();
            int wt = tat - processes.get(i).getBurst();
            int rt = start[i] - processes.get(i).getArrival();

            rows.add(new ResultRow(
                    processes.get(i).getPid(),
                    processes.get(i).getArrival(),
                    processes.get(i).getBurst(),
                    processes.get(i).getPriority(),
                    start[i],
                    completion[i],
                    wt,
                    tat,
                    rt
            ));
        }

        return rows;
    }

    private void appendSegment(List<Segment> gantt, String pid, int start, int end) {
        if (!gantt.isEmpty()
                && gantt.get(gantt.size() - 1).getPid().equals(pid)
                && gantt.get(gantt.size() - 1).getEnd() == start) {
            gantt.get(gantt.size() - 1).setEnd(end);
        } else {
            gantt.add(new Segment(pid, start, end));
        }
    }

    public Metrics calculateMetrics(List<ResultRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return new Metrics(0, 0, 0);
        }

        double wt = 0;
        double tat = 0;
        double rt = 0;

        for (ResultRow r : rows) {
            wt += r.getWaiting();
            tat += r.getTurnaround();
            rt += r.getResponse();
        }

        int n = rows.size();

        return new Metrics(wt / n, tat / n, rt / n);
    }
}
