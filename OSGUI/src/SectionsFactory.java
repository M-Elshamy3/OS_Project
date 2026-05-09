import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.UIFactory;

public class SectionsFactory {

    private SectionsFactory() {
    }

    public static HBox buildProcessSection(AppState state) {
        Label leftTitle = createSectionTitle("Process Table");

        state.processTable = UIFactory.createProcessTable();
        state.processTable.setPrefHeight(220);
        state.processTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox left = createBox(680, leftTitle, state.processTable);

        Label rightTitle = createSectionTitle("Algorithm / Priority Input Area");

        state.priorityArea = new TextArea();
        state.priorityArea.setEditable(false);
        state.priorityArea.setPrefHeight(220);
        state.priorityArea.setWrapText(true);

        VBox right = createBox(460, rightTitle, state.priorityArea);

        return new HBox(12, left, right);
    }

    public static HBox buildGanttSection(AppState state) {
        Label sjfTitle = createSectionTitle("Gantt Chart for SJF");
        state.sjfGanttArea = UIFactory.createGanttTextArea();

        VBox left = createBox(570, sjfTitle, state.sjfGanttArea);

        Label prTitle = createSectionTitle("Gantt Chart for Priority");
        state.priorityGanttArea = UIFactory.createGanttTextArea();

        VBox right = createBox(570, prTitle, state.priorityGanttArea);

        return new HBox(12, left, right);
    }

    public static HBox buildResultsSection(AppState state) {
        Label sjfTitle = createSectionTitle("Results Table for SJF");

        state.sjfResultTable = UIFactory.createResultTable();
        state.sjfResultTable.setPrefHeight(260);
        state.sjfResultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        state.sjfAvgLabel = new Label();

        VBox left = createBox(570, sjfTitle, state.sjfResultTable, state.sjfAvgLabel);

        Label prTitle = createSectionTitle("Results Table for Priority");

        state.priorityResultTable = UIFactory.createResultTable();
        state.priorityResultTable.setPrefHeight(260);
        state.priorityResultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        state.priorityAvgLabel = new Label();

        VBox right = createBox(570, prTitle, state.priorityResultTable, state.priorityAvgLabel);

        return new HBox(12, left, right);
    }

    public static VBox buildComparisonSection(AppState state) {
        Label title = createSectionTitle("Comparison Summary Section");

        state.comparisonArea = new TextArea();
        state.comparisonArea.setEditable(false);
        state.comparisonArea.setPrefHeight(220);
        state.comparisonArea.setWrapText(true);

        return createFullWidthBox(title, state.comparisonArea);
    }

    public static VBox buildConclusionSection(AppState state) {
        Label title = createSectionTitle("Final Conclusion Area");

        state.conclusionArea = new TextArea();
        state.conclusionArea.setEditable(false);
        state.conclusionArea.setPrefHeight(150);
        state.conclusionArea.setWrapText(true);

        return createFullWidthBox(title, state.conclusionArea);
    }

    private static Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }
    private static VBox createBox(double width, javafx.scene.Node... nodes) {
        VBox box = new VBox(8, nodes);
        box.setPrefWidth(width);
        box.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");
        return box;
    }
    private static VBox createFullWidthBox(javafx.scene.Node... nodes) {
        VBox box = new VBox(8, nodes);
        box.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");
        return box;
    }
}