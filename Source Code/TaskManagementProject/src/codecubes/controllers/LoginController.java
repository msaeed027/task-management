package codecubes.controllers;

import codecubes.core.Encryption;
import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;

/**
 * Created by msaeed on 1/26/2017.
 */
public class LoginController {
    @FXML
    private VBox mainContainer;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Label errors;
    @FXML
    private Button loginBtn;

    @FXML
    protected void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();
                ModelManager modelManager = ModelManager.getInstance();
                Encryption  encryption = Encryption.getInstance();

                loginBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        errors.setText("");
                                    }
                                });

                                HashMap parameters = new HashMap();
                                parameters.put("email", email.getText());
                                try {
                                    parameters.put("password", encryption.sha1(password.getText()));
                                } catch (Exception exception) {
                                    parameters.put("password", password.getText());
                                }
                                List<User> users = modelManager.where(User.class, "where email = (:email) and password = (:password)", parameters);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (users.size() > 0) {
                                            sceneManager.getSession().put("loggedInUser", users.get(0));
                                            sceneManager.loadLayout("/codecubes/views/layouts/default.fxml");
                                        } else {
                                            errors.setText("Invalid Username or Password.");
                                        }
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
