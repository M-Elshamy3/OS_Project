import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.SchedulerModels.Process;
import service.ComparisonService;

import java.util.List;

public class InputPanelFactory {

    private InputPanelFactory() {
    }

    public static VBox buildInputPanel(AppState state) {
        Label sectionTitle = new Label("Input Panel");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        state.countSpinner = new Spinner<>(1, 100, 5);
        state.countSpinner.setEditable(true);
        state.countSpinner.setPrefWidth(90);

        HBox controls = buildMainControls(state);
        HBox modeControls = buildModeControls(state);

        state.inputGrid = new GridPane();
        state.inputGrid.setHgap(10);
        state.inputGrid.setVgap(8);

        state.validationArea = new TextArea();
        state.validationArea.setEditable(false);
        state.validationArea.setPrefRowCount(4);
        state.validationArea.setPrefHeight(90);

        VBox box = new VBox(
                10,
                sectionTitle,
                controls,
                modeControls,
                state.inputGrid,
                new Label("Validation Behavior"),
                state.validationArea
        );

        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        return box;
    }

    private static HBox buildMainControls(AppState state) {
        Button createRowsButton = new Button("Create Rows");
        Button runButton = new Button("Run Comparison");
        Button clearButton = new Button("Clear");

        Button scenarioAButton = new Button("Scenario A");
        Button scenarioBButton = new Button("Scenario B");
        Button scenarioCButton = new Button("Scenario C");
        Button scenarioDButton = new Button("Scenario D");

        createRowsButton.setOnAction(e -> {
            state.selectedScenario = ComparisonService.ScenarioType.CUSTOM;
            generateInputRows(state, state.countSpinner.getValue());
        });

        runButton.setOnAction(e -> SimulationController.runSimulation(state));
        clearButton.setOnAction(e -> SimulationController.clearAll(state));

        scenarioAButton.setOnAction(e -> {
            state.selectedScenario = ComparisonService.ScenarioType.A_BASIC_MIXED;
            loadScenario(state, state.scenarioService.scenarioA());
        });

        scenarioBButton.setOnAction(e -> {
            state.selectedScenario = ComparisonService.ScenarioType.B_CONFLICT;
            loadScenario(state, state.scenarioService.scenarioB());
        });

        scenarioCButton.setOnAction(e -> {
            state.selectedScenario = ComparisonService.ScenarioType.C_FAIRNESS;
            loadScenario(state, state.scenarioService.scenarioC());
        });

        scenarioDButton.setOnAction(e -> {
            state.selectedScenario = ComparisonService.ScenarioType.D_VALIDATION;
            loadScenario(state, state.scenarioService.scenarioD());
        });

        HBox controls = new HBox(10,
                new Label("Number of Processes:"),
                state.countSpinner,
                createRowsButton,
                runButton,
                clearButton,
                scenarioAButton,
                scenarioBButton,
                scenarioCButton,
                scenarioDButton
        );

        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    private static HBox buildModeControls(AppState state) {
        // Single toggle that controls BOTH SJF and Priority Scheduling
        state.preemptiveButton = new ToggleButton("Preemptive");
        state.nonPreemptiveButton = new ToggleButton("Non-preemptive");

        ToggleGroup modeGroup = new ToggleGroup();

        state.preemptiveButton.setToggleGroup(modeGroup);
        state.nonPreemptiveButton.setToggleGroup(modeGroup);

        state.preemptiveButton.setSelected(true);

        state.preemptiveButton.setMinWidth(130);
        state.nonPreemptiveButton.setMinWidth(150);

        state.styleModeButtons();

        modeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                state.preemptiveButton.setSelected(true);
            }
            state.styleModeButtons();
        });

        Label note = new Label("(applies to both SJF and Priority Scheduling)");
        note.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        HBox box = new HBox(10,
                new Label("Choose Mode:"),
                state.preemptiveButton,
                state.nonPreemptiveButton,
                note
        );

        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public static void generateInputRows(AppState state, int n) {
        state.inputGrid.getChildren().clear();
        state.inputFields.clear();

        state.inputGrid.add(new Label("PID"), 0, 0);
        state.inputGrid.add(new Label("Arrival Time"), 1, 0);
        state.inputGrid.add(new Label("Burst Time"), 2, 0);
        state.inputGrid.add(new Label("Priority"), 3, 0);

        for (int i = 0; i < n; i++) {
            TextField pidField = new TextField();
            TextField arrivalField = new TextField();
            TextField burstField = new TextField();
            TextField priorityField = new TextField();

            pidField.setPrefWidth(160);
            arrivalField.setPrefWidth(120);
            burstField.setPrefWidth(120);
            priorityField.setPrefWidth(120);

            state.inputFields.add(new TextField[]{pidField, arrivalField, burstField, priorityField});

            state.inputGrid.add(pidField, 0, i + 1);
            state.inputGrid.add(arrivalField, 1, i + 1);
            state.inputGrid.add(burstField, 2, i + 1);
            state.inputGrid.add(priorityField, 3, i + 1);
        }
    }

    public static void loadScenario(AppState state, List<Process> processes) {
        state.countSpinner.getValueFactory().setValue(processes.size());

        generateInputRows(state, processes.size());

        for (int i = 0; i < processes.size(); i++) {
            state.inputFields.get(i)[0].setText(processes.get(i).getPid());
            state.inputFields.get(i)[1].setText(String.valueOf(processes.get(i).getArrival()));
            state.inputFields.get(i)[2].setText(String.valueOf(processes.get(i).getBurst()));
            state.inputFields.get(i)[3].setText(String.valueOf(processes.get(i).getPriority()));
        }
    }
}
