import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SchedulerApp extends Application {

    private final AppState state = new AppState();

    @Override
    public void start(Stage stage) {
        Label title = new Label("SJF vs Priority Comparison Project");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox root = new VBox(12);
        root.setPadding(new Insets(12));
        root.setFillWidth(true);
        root.setPrefWidth(1200);

        VBox inputPanel = InputPanelFactory.buildInputPanel(state);
        HBox processSection = SectionsFactory.buildProcessSection(state);
        HBox ganttSection = SectionsFactory.buildGanttSection(state);
        HBox resultsSection = SectionsFactory.buildResultsSection(state);
        VBox comparisonSection = SectionsFactory.buildComparisonSection(state);
        VBox conclusionSection = SectionsFactory.buildConclusionSection(state);

        root.getChildren().addAll(
                title,
                inputPanel,
                processSection,
                ganttSection,
                resultsSection,
                comparisonSection,
                conclusionSection
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane, 1180, 760);

        stage.setTitle("SJF Mode Selection vs Preemptive Priority Scheduling");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.centerOnScreen();
        stage.show();

        InputPanelFactory.generateInputRows(state, 5);
    }
}