package codecubes.controllers;

import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by msaeed on 1/22/2017.
 */
public class UserFormController {
    @FXML
    private VBox userContainer;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private CheckBox isManager;
    @FXML
    private Button backBtn, deleteBtn;
    @FXML
    private Label emailValidation, userHeaderLabel;

    private User user;

    @FXML
    protected void initialize() {
        SceneManager sceneManager = SceneManager.getInstance();

        user = new User();
        if (sceneManager.getSession().containsKey("user")) {
            user = (User) sceneManager.getSession().get("user");
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            email.setText(user.getEmail());
            isManager.setSelected(user.isManager());
//            password.setText(user.getPassword());
            saveBtn.setText("Update");
        }

        if (sceneManager.getSession().containsKey("user")) { // edit case
            userHeaderLabel.setText("Edit user");
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
                    if (result.get() == ButtonType.OK) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ModelManager.getInstance().delete(user);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/user-list.fxml");
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
                sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/user-list.fxml");
            }
        });

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        hideEmailValidation();
                        user.setFirstName(firstName.getText());
                        user.setLastName(lastName.getText());
                        user.setEmail(email.getText());
                        user.setManager(isManager.isSelected());

                        ModelManager modelManager = ModelManager.getInstance();
                        if (sceneManager.getSession().containsKey("user")) {
                            if (!password.getText().isEmpty()) {
                                user.setPassword(password.getText());
                            }
                            HashMap parameters = new HashMap();
                            parameters.put("email", email.getText());
                            parameters.put("id", user.getId());
                            List<User> users = ModelManager.getInstance().where(User.class, "where email = (:email) and id != (:id)", parameters);
                            if (users.size() == 0) {
                                modelManager.update(user);
                                next();
                            } else {
                                displayEmailValidation();
                            }
                        } else {
                            user.setPassword(password.getText());
                            HashMap parameters = new HashMap();
                            parameters.put("email", email.getText());
                            List<User> users = ModelManager.getInstance().where(User.class, "where email = (:email)", parameters);
                            if (users.size() == 0) {
                                modelManager.save(user);
                                next();
                            } else {
                                displayEmailValidation();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void hideEmailValidation() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                emailValidation.setText("");
                emailValidation.setVisible(false);
                emailValidation.setManaged(false);
            }
        });
    }

    public void displayEmailValidation() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                emailValidation.setText("Email address already exists.");
                emailValidation.setVisible(true);
                emailValidation.setManaged(true);
            }
        });
    }

    public void next() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();
                sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/user-list.fxml");
            }
        });
    }
}
