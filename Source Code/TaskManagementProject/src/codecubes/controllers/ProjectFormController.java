package codecubes.controllers;

import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.Project;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Created by msaeed on 1/25/2017.
 */
public class ProjectFormController {
    @FXML
    private VBox projectContainer;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    @FXML
    private Button backBtn, deleteBtn;
    @FXML
    private Label projectHeaderLabel;

    private Project project;

    @FXML
    protected void initialize() {
        SceneManager sceneManager = SceneManager.getInstance();

        project = new Project();
        if (sceneManager.getSession().containsKey("project")) {
            projectHeaderLabel.setText("Edit project");
            project = (Project) sceneManager.getSession().get("project");
            name.setText(project.getName());
            description.setText(project.getDescription());
            saveBtn.setText("Update");
        }

        if (sceneManager.getSession().containsKey("project")) { // edit case
            deleteBtn.setVisible(true);
            deleteBtn.setManaged(true);
            deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Ara you want to delete this item?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ModelManager.getInstance().delete(project);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-list.fxml");
                                    }
                                });
                            }
                        }).start();
                    } else {

                    }
                }
            });
        } else {
            deleteBtn.setVisible(false);
            deleteBtn.setManaged(false);
        }

        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-list.fxml");
            }
        });

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        project.setName(name.getText());
                        project.setDescription(description.getText());

                        ModelManager modelManager = ModelManager.getInstance();
                        if (sceneManager.getSession().containsKey("project")) {
                            modelManager.update(project);
                        } else {
                            if (sceneManager.getSession().containsKey("loggedInUser")) {
                                User loggedInUser = (User) sceneManager.getSession().get("loggedInUser");
                                project.setUser(loggedInUser);
                            }
                            modelManager.save(project);
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/project-list.fxml");
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
