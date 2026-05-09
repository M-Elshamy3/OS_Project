import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import model.SchedulerModels.Process;
import model.SchedulerModels.ResultRow;
import service.ComparisonService;
import service.ScenarioService;
import service.SchedulerService;
import service.ValidationService;

import java.util.ArrayList;
import java.util.List;

public class AppState {

    Spinner<Integer> countSpinner;
    GridPane inputGrid;
    TextArea validationArea;

    TableView<Process> processTable;

    TextArea priorityArea;
    TextArea sjfGanttArea;
    TextArea priorityGanttArea;

    TableView<ResultRow> sjfResultTable;
    TableView<ResultRow> priorityResultTable;

    Label sjfAvgLabel;
    Label priorityAvgLabel;

    TextArea comparisonArea;
    TextArea conclusionArea;

    ToggleButton preemptiveButton;
    ToggleButton nonPreemptiveButton;

    final List<TextField[]> inputFields = new ArrayList<>();

    final ValidationService validationService = new ValidationService();
    final SchedulerService schedulerService = new SchedulerService();
    final ScenarioService scenarioService = new ScenarioService();
    final ComparisonService comparisonService = new ComparisonService();

    ComparisonService.ScenarioType selectedScenario = ComparisonService.ScenarioType.CUSTOM;

    boolean isPreemptiveSelected() {
        return preemptiveButton == null || preemptiveButton.isSelected();
    }

    String getSelectedSjfModeText() {
        if (isPreemptiveSelected()) {
            return "Preemptive SJF / SRTF";
        }
        return "Non-preemptive SJF";
    }

    String getSelectedPriorityModeText() {
        if (isPreemptiveSelected()) {
            return "Preemptive Priority Scheduling";
        }
        return "Non-preemptive Priority Scheduling";
    }

    String getScenarioDisplayName(ComparisonService.ScenarioType scenario) {
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

    void styleModeButtons() {
        String selectedStyle = "-fx-background-color: #1f5fa8; -fx-text-fill: white; -fx-font-weight: bold;";
        String normalStyle = "-fx-background-color: #e6e6e6; -fx-text-fill: black;";

        if (preemptiveButton != null && nonPreemptiveButton != null) {
            preemptiveButton.setStyle(preemptiveButton.isSelected() ? selectedStyle : normalStyle);
            nonPreemptiveButton.setStyle(nonPreemptiveButton.isSelected() ? selectedStyle : normalStyle);
        }
    }
}
