package codecubes.controllers;

import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.Project;
import codecubes.models.Task;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by msaeed on 1/28/2017.
 */
public class ProjectReportController {
    @FXML
    private VBox projectContainer, reportContainer;
    @FXML
    private Button backBtn;
    @FXML
    private Label reportHeaderTitle;

    private Project project;

    @FXML
    protected void initialize() {
        SceneManager sceneManager = SceneManager.getInstance();
        ModelManager modelManager = ModelManager.getInstance();
        Project project = (Project) sceneManager.getSession().get("project");
        reportHeaderTitle.setText(project.getName() + " project report");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap parameters = new HashMap();
                parameters.put("projectId", project.getId());
                List<Long> todoTasks = modelManager.where("select count(tasks) from Project project inner join project.tasks tasks where tasks.project.id = (:projectId) and tasks.status = 1", parameters);
                List<Long>  inProgressTasks = modelManager.where("select count(tasks) from Project project inner join project.tasks tasks where tasks.project.id = (:projectId) and tasks.status = 2", parameters);
                List<Long>  doneTasks = modelManager.where("select count(tasks) from Project project inner join project.tasks tasks where tasks.project.id = (:projectId) and tasks.status = 3", parameters);

                int todoTasksCount = todoTasks.get(0).intValue();
                int inProgressTasksCount = inProgressTasks.get(0).intValue();
                int doneTasksCount = doneTasks.get(0).intValue();

                ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList(
                        new PieChart.Data("ToDo", todoTasksCount),
                        new PieChart.Data("In Progress", inProgressTasksCount),
                        new PieChart.Data("Done", doneTasksCount));

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        final PieChart chart = new PieChart(pieChartData);
                        chart.setTitle(project.getName());
                        chart.setLabelLineLength(10);
                        chart.setLegendSide(Side.LEFT);
                        reportContainer.getChildren().add(chart);
                    }
                });

            }
        }).start();

        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-list.fxml");
            }
        });
    }
}