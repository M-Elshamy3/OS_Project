package service;

import model.SchedulerModels.Metrics;
import model.SchedulerModels.Process;
import model.SchedulerModels.ResultRow;

import java.util.List;

public class ConclusionService {

    public String buildConclusionText(List<Process> processes, Metrics sjf, Metrics pr,
                                      List<ResultRow> sjfRows, List<ResultRow> prRows,
                                      ComparisonService.ScenarioType scenario,
                                      ComparisonService comparisonService) {
        if (scenario == ComparisonService.ScenarioType.D_VALIDATION) {
            return buildValidationConclusion();
        }

        int sjfWorst = comparisonService.getWorstWaiting(sjfRows);
        int prWorst = comparisonService.getWorstWaiting(prRows);

        StringBuilder sb = new StringBuilder();

        sb.append("Conclusion\n");
        sb.append("Selected workload type: ").append(comparisonService.getScenarioLabel(scenario)).append("\n\n");

        sb.append(buildOverallConclusion(sjf, pr)).append("\n\n");

        sb.append("Metric-specific conclusion\n");
        sb.append(buildMetricSpecificConclusion(sjf, pr)).append("\n");

        sb.append("Trade-off explanation\n");
        sb.append(buildTradeOffConclusion(scenario)).append("\n\n");

        sb.append("Fairness conclusion\n");
        sb.append(buildFairnessConclusion(sjfWorst, prWorst)).append("\n\n");

        sb.append("Final recommendation\n");
        sb.append(comparisonService.buildRecommendation(sjf, pr, sjfWorst, prWorst));

        return sb.toString();
    }

    private String buildOverallConclusion(Metrics sjf, Metrics pr) {
        int sjfWins = 0;
        int prWins = 0;

        if (sjf.getAvgWT() < pr.getAvgWT()) {
            sjfWins++;
        } else if (pr.getAvgWT() < sjf.getAvgWT()) {
            prWins++;
        }

        if (sjf.getAvgTAT() < pr.getAvgTAT()) {
            sjfWins++;
        } else if (pr.getAvgTAT() < sjf.getAvgTAT()) {
            prWins++;
        }

        if (sjf.getAvgRT() < pr.getAvgRT()) {
            sjfWins++;
        } else if (pr.getAvgRT() < sjf.getAvgRT()) {
            prWins++;
        }

        if (sjfWins > prWins) {
            return "SJF performed better overall across more average performance metrics.";
        } else if (prWins > sjfWins) {
            return "Priority Scheduling performed better overall across more average performance metrics.";
        } else {
            return "Neither algorithm dominated all metrics. The result depends on whether efficiency or urgency is more important.";
        }
    }

    private String buildMetricSpecificConclusion(Metrics sjf, Metrics pr) {
        StringBuilder sb = new StringBuilder();

        if (sjf.getAvgWT() < pr.getAvgWT()) {
            sb.append("SJF handled waiting time better.\n");
        } else if (pr.getAvgWT() < sjf.getAvgWT()) {
            sb.append("Priority Scheduling handled waiting time better.\n");
        } else {
            sb.append("Both handled waiting time equally.\n");
        }

        if (sjf.getAvgTAT() < pr.getAvgTAT()) {
            sb.append("SJF handled turnaround time better.\n");
        } else if (pr.getAvgTAT() < sjf.getAvgTAT()) {
            sb.append("Priority Scheduling handled turnaround time better.\n");
        } else {
            sb.append("Both handled turnaround time equally.\n");
        }

        if (sjf.getAvgRT() < pr.getAvgRT()) {
            sb.append("SJF handled response time better.\n");
        } else if (pr.getAvgRT() < sjf.getAvgRT()) {
            sb.append("Priority Scheduling handled response time better.\n");
        } else {
            sb.append("Both handled response time equally.\n");
        }

        return sb.toString();
    }

    private String buildTradeOffConclusion(ComparisonService.ScenarioType scenario) {
        switch (scenario) {
            case B_CONFLICT:
                return "This scenario demonstrates the trade-off between efficiency and urgency. SJF follows job length, while Priority Scheduling follows process importance.";
            case C_FAIRNESS:
                return "This scenario shows that good average metrics do not always guarantee fairness. One process may still wait much longer than others.";
            default:
                return "SJF focuses on efficiency by favoring shorter work, while Priority Scheduling focuses on urgency by favoring high-priority processes.";
        }
    }

    private String buildFairnessConclusion(int sjfWorst, int prWorst) {
        if (sjfWorst < prWorst) {
            return "SJF appeared fairer based on worst waiting time.";
        } else if (prWorst < sjfWorst) {
            return "Priority Scheduling appeared fairer based on worst waiting time.";
        } else {
            return "Both algorithms appeared equally fair based on worst waiting time.";
        }
    }

    private String buildValidationConclusion() {
        StringBuilder sb = new StringBuilder();

        sb.append("Conclusion\n");
        sb.append("Selected workload type: Scenario D - Validation case\n\n");
        sb.append("This scenario is not meant to compare scheduling metrics.\n");
        sb.append("Its purpose is to verify that invalid input is detected and blocked correctly.\n");
        sb.append("The program should reject duplicated IDs, empty IDs, negative arrival times, non-positive burst times, and invalid priority values.\n");
        sb.append("The final recommendation is to correct invalid entries first, then rerun the scheduling comparison.\n");

        return sb.toString();
    }
}