package codecubes.controllers;

import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 * Created by msaeed on 1/21/2017.
 */
public class UserListController {

    @FXML
    private VBox userContainer;
    @FXML
    private Button createNewUserBtn;
    @FXML
    private TableView tableView;

    @FXML
    protected void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();
                ModelManager modelManager = ModelManager.getInstance();

                sceneManager.getSession().remove("user");

                ObservableList<User> users = FXCollections.observableList(modelManager.all(User.class));
                tableView.setItems(users);

                createNewUserBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/user-form.fxml");
                    }
                });

                tableView.setRowFactory(tableView -> {
                    TableRow<User> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (!row.isEmpty())) {
                            User rowData = row.getItem();
                            sceneManager.getSession().put("user", rowData);
                            sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/user-form.fxml");
                        }
                    });
                    return row;
                });
            }
        }).start();
    }
}
