package ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import model.SchedulerModels.Process;
import model.SchedulerModels.ResultRow;
import model.SchedulerModels.Segment;

import java.util.List;

public class UIFactory {

    private UIFactory() {
    }

    public static TableView<Process> createProcessTable() {
        TableView<Process> table = new TableView<>();

        TableColumn<Process, String> pid = new TableColumn<>("PID");
        pid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPid()));

        TableColumn<Process, Number> arrival = new TableColumn<>("Arrival");
        arrival.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getArrival()));

        TableColumn<Process, Number> burst = new TableColumn<>("Burst");
        burst.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBurst()));

        TableColumn<Process, Number> priority = new TableColumn<>("Priority");
        priority.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPriority()));

        table.getColumns().addAll(pid, arrival, burst, priority);

        return table;
    }

    public static TableView<ResultRow> createResultTable() {
        TableView<ResultRow> table = new TableView<>();

        TableColumn<ResultRow, String> pid = new TableColumn<>("PID");
        pid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPid()));

        TableColumn<ResultRow, Number> at = new TableColumn<>("AT");
        at.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getArrival()));

        TableColumn<ResultRow, Number> bt = new TableColumn<>("BT");
        bt.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBurst()));

        TableColumn<ResultRow, Number> pr = new TableColumn<>("PR");
        pr.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPriority()));

        TableColumn<ResultRow, Number> st = new TableColumn<>("ST");
        st.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()));

        TableColumn<ResultRow, Number> ct = new TableColumn<>("CT");
        ct.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCompletion()));

        TableColumn<ResultRow, Number> wt = new TableColumn<>("WT");
        wt.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getWaiting()));

        TableColumn<ResultRow, Number> tat = new TableColumn<>("TAT");
        tat.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTurnaround()));

        TableColumn<ResultRow, Number> rt = new TableColumn<>("RT");
        rt.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getResponse()));

        table.getColumns().addAll(pid, at, bt, pr, st, ct, wt, tat, rt);

        return table;
    }

    public static TextArea createGanttTextArea() {
        TextArea area = new TextArea();

        area.setEditable(false);
        area.setPrefHeight(170);
        area.setWrapText(false);
        area.setStyle(
                "-fx-font-family: 'Consolas', 'Courier New', monospace;" +
                        "-fx-font-size: 14px;" +
                        "-fx-control-inner-background: #f8f8f8;"
        );

        return area;
    }

    public static String buildGanttText(List<Segment> gantt) {
        if (gantt == null || gantt.isEmpty()) {
            return "";
        }

        StringBuilder topBorder = new StringBuilder();
        StringBuilder processLine = new StringBuilder();
        StringBuilder bottomBorder = new StringBuilder();
        StringBuilder timeLine = new StringBuilder();

        for (int i = 0; i < gantt.size(); i++) {
            Segment segment = gantt.get(i);

            String pid = segment.getPid();
            int duration = segment.getEnd() - segment.getStart();

            int width = Math.max(7, pid.length() + 2);
            width = Math.max(width, duration * 2);

            topBorder.append("+").append("-".repeat(width));
            bottomBorder.append("+").append("-".repeat(width));

            int leftPad = (width - pid.length()) / 2;
            int rightPad = width - pid.length() - leftPad;

            processLine.append("|")
                    .append(" ".repeat(leftPad))
                    .append(pid)
                    .append(" ".repeat(rightPad));

            String start = String.valueOf(segment.getStart());

            timeLine.append(start);

            int spaces = width + 1 - start.length();

            timeLine.append(" ".repeat(Math.max(1, spaces)));

            if (i == gantt.size() - 1) {
                timeLine.append(segment.getEnd());
            }
        }

        topBorder.append("+");
        processLine.append("|");
        bottomBorder.append("+");

        return topBorder + "\n"
                + processLine + "\n"
                + bottomBorder + "\n"
                + timeLine;
    }
}