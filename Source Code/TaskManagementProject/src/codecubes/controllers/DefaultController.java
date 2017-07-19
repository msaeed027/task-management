package codecubes.controllers;

import codecubes.core.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Created by msaeed on 1/21/2017.
 */
public class DefaultController {

    @FXML
    private VBox mainContainer;
    @FXML
    private Button userManagementBtn;
    @FXML
    private Button projectManagementBtn, logoutBtn, exitBtn;
//    @FXML
//    private Label loggedInUserName;

    @FXML
    protected void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();

                sceneManager.setMainContainer(mainContainer);

//                User loggedInUser = (User) sceneManager.getSession().get("loggedInUser");
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        loggedInUserName.setText("Hi, " + loggedInUser.getFirstName());
//                    }
//                });

                if (sceneManager.isLoggedInUserManager()) {
                    userManagementBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            sceneManager.loadPartial(mainContainer, "/codecubes/views/partials/user-list.fxml");
                        }
                    });
                } else {
                    userManagementBtn.setVisible(false);
                    userManagementBtn.setManaged(false);
                }

                projectManagementBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        sceneManager.loadPartial(mainContainer, "/codecubes/views/partials/project-list.fxml");
                    }
                });

                logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        sceneManager.getSession().remove("loggedInUser");
                        sceneManager.loadLayout("/codecubes/views/layouts/login.fxml");
                    }
                });

                exitBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        Platform.exit();
                    }
                });
            }
        }).start();
    }
}
