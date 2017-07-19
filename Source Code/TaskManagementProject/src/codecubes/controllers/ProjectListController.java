package codecubes.controllers;

import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.Project;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.HashMap;

/**
 * Created by msaeed on 1/25/2017.
 */
public class ProjectListController {

    @FXML
    private VBox projectContainer;
    @FXML
    private Button createNewProjectBtn;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn action;


    @FXML
    protected void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();

                if (! sceneManager.isLoggedInUserManager()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            action.setVisible(false);
                            createNewProjectBtn.setVisible(false);
                            createNewProjectBtn.setManaged(false);
                        }
                    });
                }

                ModelManager modelManager = ModelManager.getInstance();

                sceneManager.getSession().remove("project");
                ObservableList<Project> projects;
                if (sceneManager.isLoggedInUserManager()) {
                    projects = FXCollections.observableList(modelManager.all(Project.class));
                } else {
                    HashMap parameters = new HashMap();
                    User loggedInUser = (User) sceneManager.getSession().get("loggedInUser");
                    parameters.put("id", loggedInUser.getId());
                    projects = FXCollections.observableList(modelManager.where(
                                    "select distinct project from Project project inner join project.tasks tasks where tasks.user.id = (:id)",
                                    parameters
                            )
                    );
                }
                if (sceneManager.isLoggedInUserManager()) {
                    Callback<TableColumn<Project, String>, TableCell<Project, String>> cellFactory =
                            new Callback<TableColumn<Project, String>, TableCell<Project, String>>() {
                                @Override
                                public TableCell call(final TableColumn<Project, String> param) {
                                    final TableCell<Project, String> cell = new TableCell<Project, String>() {
                                        final Button editBtn = new Button("Edit");
                                        final Button reportBtn = new Button("Report");

                                        @Override
                                        public void updateItem(String item, boolean empty) {
                                            super.updateItem(item, empty);
                                            if (empty) {
                                                setGraphic(null);
                                                setText(null);
                                            } else {
                                                editBtn.setOnAction((ActionEvent event) -> {
                                                    Project project = getTableView().getItems().get(getIndex());
                                                    sceneManager.getSession().put("project", project);
                                                    sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-form.fxml");
                                                });
                                                reportBtn.setOnAction((ActionEvent event) -> {
                                                    Project project = getTableView().getItems().get(getIndex());
                                                    sceneManager.getSession().put("project", project);
                                                    sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-report.fxml");
                                                });
                                                HBox container = new HBox();
                                                container.setSpacing(5);
                                                container.getChildren().add(editBtn);
                                                container.getChildren().add(reportBtn);
                                                setGraphic(container);
                                                setText(null);
                                            }
                                        }
                                    };
                                    return cell;
                                }
                            };

                    action.setCellFactory(cellFactory);
                }

                tableView.setItems(projects);

                createNewProjectBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-form.fxml");
                    }
                });

                tableView.setRowFactory(tableView -> {
                    TableRow<Project> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (!row.isEmpty())) {
                            Project rowData = row.getItem();
                            sceneManager.getSession().put("project", rowData);
                            sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/task-list.fxml");
                        }
                    });
                    return row;
                });
            }
        }).start();
    }
}
