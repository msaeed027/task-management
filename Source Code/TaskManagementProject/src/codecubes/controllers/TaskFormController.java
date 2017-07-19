package codecubes.controllers;

import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.Project;
import codecubes.models.Task;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Optional;

/**
 * Created by msaeed on 1/26/2017.
 */
public class TaskFormController {
    @FXML
    private VBox taskContainer;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    @FXML
    private ComboBox<User> assignTo;
    @FXML
    private Button backBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Label taskHeaderLabel;

    private Task task;

    @FXML
    protected void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();

                if (! sceneManager.isLoggedInUserManager()) {
                    if (sceneManager.getSession().containsKey("task")) { // edit case
                        description.setDisable(true);
                        name.setDisable(true);
                        assignTo.setDisable(true);
                        saveBtn.setVisible(false);
                        saveBtn.setManaged(false);
                    }
                    deleteBtn.setVisible(false);
                    deleteBtn.setManaged(false);
                }

                ModelManager modelManager = ModelManager.getInstance();

                assignTo.getItems().addAll(modelManager.all(User.class));
                assignTo.setConverter(new StringConverter<User>() {

                    @Override
                    public String toString(User user) {
                        if (user == null) return null;
                        return user.getEmail();
                    }

                    @Override
                    public User fromString(String string) {
                        User selectedUser = new User();
                        for (User user : assignTo.getItems()) {
                            if (user.getEmail().equals(string)) {
                                selectedUser = user;
                                break;
                            }
                        }
                        return selectedUser;
                    }
                });

                task = new Task();
                if (sceneManager.getSession().containsKey("task")) {
                    task = (Task) sceneManager.getSession().get("task");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            taskHeaderLabel.setText("Edit task");
                            name.setText(task.getName());
                            description.setText(task.getDescription());
                            assignTo.setValue(task.getUser());
                            saveBtn.setText("Update");
                        }
                    });
                }

                if (sceneManager.getSession().containsKey("task")) { // edit case
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (sceneManager.isLoggedInUserManager()) {
//                                deleteBtn.setVisible(true);
//                                deleteBtn.setManaged(true);
//                            }
//                        }
//                    });
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
                                        ModelManager.getInstance().delete(task);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/task-list.fxml");
                                            }
                                        });
                                    }
                                }).start();
                            } else {

                            }
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            deleteBtn.setVisible(false);
                            deleteBtn.setManaged(false);
                        }
                    });
                }

                backBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/task-list.fxml");
                    }
                });

                saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                task.setName(name.getText());
                                task.setDescription(description.getText());
                                task.setUser(assignTo.getValue());
                                if (sceneManager.getSession().containsKey("project")) {
                                    Project project = (Project) sceneManager.getSession().get("project");
                                    task.setProject(project);
                                }

                                if (sceneManager.getSession().containsKey("task")) {
                                    modelManager.update(task);
                                } else {
                                    task.setStatus(1);
                                    modelManager.save(task);
                                }

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/task-list.fxml");
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }).start();
    }

}
