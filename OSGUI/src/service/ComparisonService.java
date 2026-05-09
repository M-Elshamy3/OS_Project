package service;

import model.SchedulerModels.Metrics;
import model.SchedulerModels.Process;
import model.SchedulerModels.ResultRow;

import java.util.List;

public class ComparisonService {

    public enum ScenarioType {
        A_BASIC_MIXED,
        B_CONFLICT,
        C_FAIRNESS,
        D_VALIDATION,
        CUSTOM
    }

    private final ConclusionService conclusionService = new ConclusionService();

    public String buildPriorityText(List<Process> processes, boolean preemptiveSJF) {
        StringBuilder sb = new StringBuilder();

        if (preemptiveSJF) {
            sb.append("Selected SJF mode: Preemptive SJF / Shortest Remaining Time First.\n");
            sb.append("SJF rule: choose the process with the shortest remaining burst time.\n");
            sb.append("SJF tie handling: remaining time, then arrival time, then input order.\n\n");
        } else {
            sb.append("Selected SJF mode: Non-preemptive SJF.\n");
            sb.append("SJF rule: when CPU becomes free, choose the available process with the shortest burst time.\n");
            sb.append("SJF tie handling: burst time, then arrival time, then input order.\n\n");
        }

        sb.append("Priority rule: smaller number means higher priority.\n");
        sb.append("Priority mode: preemptive priority scheduling.\n");
        sb.append("Priority tie handling: priority, then arrival time, then remaining burst time, then input order.\n\n");

        sb.append("PID\tPriority\n");

        for (Process p : processes) {
            sb.append(p.getPid()).append("\t").append(p.getPriority()).append("\n");
        }

        return sb.toString();
    }

    public String buildPriorityText(List<Process> processes) {
        return buildPriorityText(processes, true);
    }

    public String buildComparisonText(List<Process> processes, Metrics sjf, Metrics pr,
                                      List<ResultRow> sjfRows, List<ResultRow> prRows,
                                      ScenarioType scenario) {
        if (scenario == ScenarioType.D_VALIDATION) {
            return buildValidationAnalysis(processes);
        }

        int sjfWorst = getWorstWaiting(sjfRows);
        int prWorst = getWorstWaiting(prRows);

        StringBuilder sb = new StringBuilder();

        sb.append("Metric Comparison\n");
        sb.append("Selected workload type: ").append(getScenarioLabel(scenario)).append("\n\n");

        sb.append(buildScenarioIntro(scenario)).append("\n\n");
        sb.append(buildMetricSummary(sjf, pr, sjfWorst, prWorst)).append("\n");

        sb.append("Required Analysis Questions\n\n");

        sb.append("1. Which algorithm gave lower average waiting time?\n");
        sb.append(answerWaitingTime(sjf, pr)).append("\n");

        sb.append("2. Which algorithm gave lower average turnaround time?\n");
        sb.append(answerTurnaroundTime(sjf, pr)).append("\n");

        sb.append("3. Did SJF favor short jobs more strongly?\n");
        sb.append(answerShortJobs(scenario)).append("\n");

        sb.append("4. Did Priority Scheduling favor urgent processes more strongly?\n");
        sb.append(answerPriorityBehavior(scenario)).append("\n");

        sb.append("5. Was any starvation or unfair delay observed?\n");
        sb.append(answerFairness(sjfWorst, prWorst)).append("\n");

        sb.append("6. Which algorithm would you recommend for the tested workload, and why?\n");
        sb.append(buildRecommendation(sjf, pr, sjfWorst, prWorst));

        return sb.toString();
    }

    public String buildConclusionText(List<Process> processes, Metrics sjf, Metrics pr,
                                      List<ResultRow> sjfRows, List<ResultRow> prRows,
                                      ScenarioType scenario) {
        return conclusionService.buildConclusionText(
                processes,
                sjf,
                pr,
                sjfRows,
                prRows,
                scenario,
                this
        );
    }

    String getScenarioLabel(ScenarioType scenario) {
        switch (scenario) {
            case A_BASIC_MIXED:
                return "Scenario A - Basic mixed workload";
            case B_CONFLICT:
                return "Scenario B - Conflict between burst time and priority";
            case C_FAIRNESS:
                return "Scenario C - Fairness / starvation-sensitive case";
            case D_VALIDATION:
                return "Scenario D - Validation case";
            default:
                return "Custom workload";
        }
    }

    String buildRecommendation(Metrics sjf, Metrics pr, int sjfWorst, int prWorst) {
        int sjfScore = 0;
        int prScore = 0;

        if (sjf.getAvgWT() < pr.getAvgWT()) {
            sjfScore++;
        } else if (pr.getAvgWT() < sjf.getAvgWT()) {
            prScore++;
        }

        if (sjf.getAvgTAT() < pr.getAvgTAT()) {
            sjfScore++;
        } else if (pr.getAvgTAT() < sjf.getAvgTAT()) {
            prScore++;
        }

        if (sjf.getAvgRT() < pr.getAvgRT()) {
            sjfScore++;
        } else if (pr.getAvgRT() < sjf.getAvgRT()) {
            prScore++;
        }

        if (sjfWorst < prWorst) {
            sjfScore++;
        } else if (prWorst < sjfWorst) {
            prScore++;
        }

        if (sjfScore > prScore) {
            return "SJF is recommended because it performed better across more measured metrics.\n";
        } else if (prScore > sjfScore) {
            return "Priority Scheduling is recommended because it performed better across more measured metrics and gave stronger service to urgent processes.\n";
        } else {
            return "Neither algorithm clearly dominates. SJF is better for efficiency, while Priority Scheduling is better for urgency.\n";
        }
    }

    int getWorstWaiting(List<ResultRow> rows) {
        return rows.stream().mapToInt(ResultRow::getWaiting).max().orElse(0);
    }

    private String buildScenarioIntro(ScenarioType scenario) {
        switch (scenario) {
            case A_BASIC_MIXED:
                return "This workload has different arrival times, burst times, and priorities. It shows normal behavior for both algorithms.";
            case B_CONFLICT:
                return "This workload creates a conflict between short jobs and urgent jobs. It shows the trade-off between SJF efficiency and priority urgency.";
            case C_FAIRNESS:
                return "This workload is fairness-sensitive. It checks whether one process waits much longer than the others.";
            default:
                return "This is a custom workload entered at runtime. The explanation is based on the measured results.";
        }
    }

    private String buildMetricSummary(Metrics sjf, Metrics pr, int sjfWorst, int prWorst) {
        StringBuilder sb = new StringBuilder();

        sb.append(compareMetric("Lower average waiting time", "SJF", "Priority Scheduling", sjf.getAvgWT(), pr.getAvgWT()));
        sb.append(compareMetric("Lower average turnaround time", "SJF", "Priority Scheduling", sjf.getAvgTAT(), pr.getAvgTAT()));
        sb.append(compareMetric("Lower average response time", "SJF", "Priority Scheduling", sjf.getAvgRT(), pr.getAvgRT()));

        if (sjfWorst < prWorst) {
            sb.append("Lower worst waiting time: SJF\n");
        } else if (prWorst < sjfWorst) {
            sb.append("Lower worst waiting time: Priority Scheduling\n");
        } else {
            sb.append("Worst waiting time: equal in both algorithms\n");
        }

        return sb.toString();
    }

    private String compareMetric(String label, String leftName, String rightName, double left, double right) {
        if (left < right) {
            return label + ": " + leftName + "\n";
        } else if (right < left) {
            return label + ": " + rightName + "\n";
        } else {
            return label + ": equal in both algorithms\n";
        }
    }

    private String answerWaitingTime(Metrics sjf, Metrics pr) {
        if (sjf.getAvgWT() < pr.getAvgWT()) {
            return "SJF gave the lower average waiting time because it favored shorter jobs or shorter remaining jobs.\n";
        } else if (pr.getAvgWT() < sjf.getAvgWT()) {
            return "Priority Scheduling gave the lower average waiting time because the priority order worked better for this workload.\n";
        } else {
            return "Both algorithms gave the same average waiting time.\n";
        }
    }

    private String answerTurnaroundTime(Metrics sjf, Metrics pr) {
        if (sjf.getAvgTAT() < pr.getAvgTAT()) {
            return "SJF gave the lower average turnaround time because shorter work completed sooner overall.\n";
        } else if (pr.getAvgTAT() < sjf.getAvgTAT()) {
            return "Priority Scheduling gave the lower average turnaround time because urgent processes completed more effectively.\n";
        } else {
            return "Both algorithms gave the same average turnaround time.\n";
        }
    }

    private String answerShortJobs(ScenarioType scenario) {
        switch (scenario) {
            case B_CONFLICT:
                return "Yes. This conflict scenario shows that SJF favors shorter jobs while Priority Scheduling follows urgency.\n";
            case C_FAIRNESS:
                return "Yes. SJF favors short jobs strongly, which can delay longer jobs and create fairness concerns.\n";
            default:
                return "Yes. SJF focuses on job length. In preemptive mode it uses shortest remaining time, and in non-preemptive mode it selects the shortest available burst when CPU becomes free.\n";
        }
    }

    private String answerPriorityBehavior(ScenarioType scenario) {
        switch (scenario) {
            case B_CONFLICT:
                return "Yes. This conflict scenario shows that Priority Scheduling favors urgent high-priority processes even if they are longer.\n";
            case C_FAIRNESS:
                return "Yes. Priority Scheduling favors urgent processes, but low-priority processes may experience longer delay.\n";
            default:
                return "Yes. Priority Scheduling selects the most urgent available process according to the priority rule.\n";
        }
    }

    private String answerFairness(int sjfWorst, int prWorst) {
        if (sjfWorst < prWorst) {
            return "A fairness difference was observed. SJF had the lower worst waiting time.\n";
        } else if (prWorst < sjfWorst) {
            return "A fairness difference was observed. Priority Scheduling had the lower worst waiting time.\n";
        } else {
            return "Both algorithms had the same worst waiting time, so neither had a clear fairness advantage.\n";
        }
    }

    private String buildValidationAnalysis(List<Process> processes) {
        StringBuilder sb = new StringBuilder();

        sb.append("Validation Analysis\n");
        sb.append("Selected workload type: Scenario D - Validation case\n\n");

        sb.append("This scenario demonstrates input validation behavior rather than scheduling performance.\n");
        sb.append("The system should reject invalid rows before any simulation starts.\n\n");

        sb.append("Validation checks enforced:\n");
        sb.append("- PID must not be empty.\n");
        sb.append("- PID must be unique.\n");
        sb.append("- Arrival time must be zero or positive.\n");
        sb.append("- Burst time must be greater than zero.\n");
        sb.append("- Priority must be greater than zero.\n\n");

        sb.append("Observed input review:\n");

        for (Process p : processes) {
            sb.append("Process ")
                    .append(p.getPid().isEmpty() ? "Blank PID" : p.getPid())
                    .append(": arrival=")
                    .append(p.getArrival())
                    .append(", burst=")
                    .append(p.getBurst())
                    .append(", priority=")
                    .append(p.getPriority())
                    .append("\n");
        }

        return sb.toString();
    }
}