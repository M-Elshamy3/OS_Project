import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import model.SchedulerModels.Metrics;
import model.SchedulerModels.Process;
import model.SchedulerModels.ScheduleOutput;
import service.ComparisonService;
import ui.UIFactory;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private SimulationController() {
    }

    public static void runSimulation(AppState state) {
        state.validationArea.clear();

        List<String> errors = new ArrayList<>();
        List<Process> processes = readProcesses(state, errors);

        state.selectedScenario = state.scenarioService.detectScenario(processes);

        if (!errors.isEmpty()) {
            handleValidationErrors(state, processes, errors);
            return;
        }

        updateValidInputArea(state, processes);
        runAlgorithmsAndDisplay(state, processes);
    }

    private static List<Process> readProcesses(AppState state, List<String> errors) {
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < state.inputFields.size(); i++) {
            TextField[] fields = state.inputFields.get(i);

            String pid = fields[0].getText().trim();
            String atText = fields[1].getText().trim();
            String btText = fields[2].getText().trim();
            String prText = fields[3].getText().trim();

            Integer at = state.validationService.parseInteger(atText);
            Integer bt = state.validationService.parseInteger(btText);
            Integer pr = state.validationService.parseInteger(prText);

            addBasicFieldErrors(errors, i, pid, at, bt, pr);

            processes.add(new Process(
                    pid,
                    at == null ? 0 : at,
                    bt == null ? 0 : bt,
                    pr == null ? 0 : pr,
                    i
            ));
        }

        state.validationService.validateProcesses(processes, errors);

        return processes;
    }

    private static void addBasicFieldErrors(List<String> errors, int row, String pid,
                                            Integer at, Integer bt, Integer pr) {
        if (pid.isEmpty()) {
            errors.add("Row " + (row + 1) + ": PID cannot be empty.");
        }

        if (at == null) {
            errors.add("Row " + (row + 1) + ": Arrival Time must be an integer.");
        }

        if (bt == null) {
            errors.add("Row " + (row + 1) + ": Burst Time must be an integer.");
        }

        if (pr == null) {
            errors.add("Row " + (row + 1) + ": Priority must be an integer.");
        }
    }

    private static void handleValidationErrors(AppState state, List<Process> processes, List<String> errors) {
        state.validationArea.setText(String.join("\n", errors));

        clearOutputOnly(state);

        if (state.selectedScenario == ComparisonService.ScenarioType.D_VALIDATION) {
            Metrics emptyMetrics = new Metrics(0, 0, 0);

            state.comparisonArea.setText(state.comparisonService.buildComparisonText(
                    processes,
                    emptyMetrics,
                    emptyMetrics,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    state.selectedScenario
            ));

            state.conclusionArea.setText(state.comparisonService.buildConclusionText(
                    processes,
                    emptyMetrics,
                    emptyMetrics,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    state.selectedScenario
            ));
        }
    }

    private static void updateValidInputArea(AppState state, List<Process> processes) {
        state.validationArea.setText(
                "Input is valid.\n" +
                        "Detected workload type: " + state.getScenarioDisplayName(state.selectedScenario) + "\n" +
                        "Selected SJF mode: " + state.getSelectedSjfModeText()
        );

        state.processTable.setItems(FXCollections.observableArrayList(processes));

        state.priorityArea.setText(state.comparisonService.buildPriorityText(
                processes,
                state.isPreemptiveSjfSelected()
        ));
    }

    private static void runAlgorithmsAndDisplay(AppState state, List<Process> processes) {
        ScheduleOutput sjfOut = state.isPreemptiveSjfSelected()
                ? state.schedulerService.runPreemptiveSJF(processes)
                : state.schedulerService.runNonPreemptiveSJF(processes);

        ScheduleOutput priorityOut = state.schedulerService.runPreemptivePriority(processes);

        Metrics sjfMetrics = state.schedulerService.calculateMetrics(sjfOut.getRows());
        Metrics priorityMetrics = state.schedulerService.calculateMetrics(priorityOut.getRows());

        state.sjfGanttArea.setText(UIFactory.buildGanttText(sjfOut.getGantt()));
        state.priorityGanttArea.setText(UIFactory.buildGanttText(priorityOut.getGantt()));

        state.sjfResultTable.setItems(FXCollections.observableArrayList(sjfOut.getRows()));
        state.priorityResultTable.setItems(FXCollections.observableArrayList(priorityOut.getRows()));

        setAverageLabels(state, sjfMetrics, priorityMetrics);
        setAnalysisText(state, processes, sjfOut, priorityOut, sjfMetrics, priorityMetrics);
    }

    private static void setAverageLabels(AppState state, Metrics sjfMetrics, Metrics priorityMetrics) {
        state.sjfAvgLabel.setText(String.format(
                "Mode: %s    Average WT = %.2f    Average TAT = %.2f    Average RT = %.2f",
                state.getSelectedSjfModeText(),
                sjfMetrics.getAvgWT(),
                sjfMetrics.getAvgTAT(),
                sjfMetrics.getAvgRT()
        ));

        state.priorityAvgLabel.setText(String.format(
                "Average WT = %.2f    Average TAT = %.2f    Average RT = %.2f",
                priorityMetrics.getAvgWT(),
                priorityMetrics.getAvgTAT(),
                priorityMetrics.getAvgRT()
        ));
    }

    private static void setAnalysisText(AppState state, List<Process> processes,
                                        ScheduleOutput sjfOut, ScheduleOutput priorityOut,
                                        Metrics sjfMetrics, Metrics priorityMetrics) {
        state.comparisonArea.setText(
                "Selected SJF mode: " + state.getSelectedSjfModeText() + "\n\n" +
                        state.comparisonService.buildComparisonText(
                                processes,
                                sjfMetrics,
                                priorityMetrics,
                                sjfOut.getRows(),
                                priorityOut.getRows(),
                                state.selectedScenario
                        )
        );

        state.conclusionArea.setText(
                "Selected SJF mode: " + state.getSelectedSjfModeText() + "\n\n" +
                        state.comparisonService.buildConclusionText(
                                processes,
                                sjfMetrics,
                                priorityMetrics,
                                sjfOut.getRows(),
                                priorityOut.getRows(),
                                state.selectedScenario
                        )
        );
    }

    public static void clearOutputOnly(AppState state) {
        state.processTable.setItems(FXCollections.observableArrayList());

        state.priorityArea.clear();

        state.sjfGanttArea.clear();
        state.priorityGanttArea.clear();

        state.sjfResultTable.setItems(FXCollections.observableArrayList());
        state.priorityResultTable.setItems(FXCollections.observableArrayList());

        state.comparisonArea.clear();
        state.conclusionArea.clear();

        state.sjfAvgLabel.setText("");
        state.priorityAvgLabel.setText("");
    }

    public static void clearAll(AppState state) {
        state.selectedScenario = ComparisonService.ScenarioType.CUSTOM;

        state.countSpinner.getValueFactory().setValue(5);
        InputPanelFactory.generateInputRows(state, 5);

        state.validationArea.clear();

        clearOutputOnly(state);

        if (state.preemptiveSjfButton != null) {
            state.preemptiveSjfButton.setSelected(true);
            state.styleModeButtons();
        }
    }
}