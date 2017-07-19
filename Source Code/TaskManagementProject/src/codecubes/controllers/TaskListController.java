package codecubes.controllers;

import codecubes.core.DragDropManager;
import codecubes.core.ModelManager;
import codecubes.core.SceneManager;
import codecubes.models.Project;
import codecubes.models.Task;
import codecubes.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.*;

/**
 * Created by msaeed on 1/25/2017.
 */
public class TaskListController {

    @FXML
    private VBox taskContainer;
    @FXML
    private Button createNewTaskBtn;
    @FXML
    public VBox todo, inProgress, done;
    @FXML
    public HBox board;
    @FXML
    public ScrollPane boardScrollPane;

    @FXML
    protected void initialize() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SceneManager sceneManager = SceneManager.getInstance();
                ModelManager modelManager = ModelManager.getInstance();

                sceneManager.getSession().remove("task");

                board.maxWidthProperty().bind(boardScrollPane.widthProperty().subtract(20));
                board.minWidthProperty().bind(boardScrollPane.widthProperty().subtract(20));
                boardScrollPane.minViewportHeightProperty().bind(taskContainer.heightProperty().subtract(30));
                board.minHeightProperty().bind(boardScrollPane.heightProperty().subtract(30));

                createNewTaskBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/task-form.fxml");
                    }
                });
                if (sceneManager.getSession().containsKey("project")) {
                    Project project = (Project) sceneManager.getSession().get("project");
                    HashMap parameters = new HashMap();
                    parameters.put("projectId", project.getId());
                    ArrayList<Task> todoTasks =
                            new ArrayList<Task>(modelManager.where(Task.class, "where project_id = (:projectId) and status = 1", parameters));
                    ArrayList<Task> inProgressTasks =
                            new ArrayList<Task>(modelManager.where(Task.class, "where project_id = (:projectId) and status = 2", parameters));
                    ArrayList<Task> doneTasks =
                            new ArrayList<Task>(modelManager.where(Task.class, "where project_id = (:projectId) and status = 3", parameters));

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addToViewList(todo, todoTasks);
                            addToViewList(inProgress, inProgressTasks);
                            addToViewList(done, doneTasks);
                        }
                    });
                }

                DragDropManager dragDropManager = DragDropManager.getInstance();

                dragDropManager.droppable(todo, new Callback() {
                    @Override
                    public Object call(Object param) {
                        DragEvent event = (DragEvent) param;
                        String id = event.getDragboard().getContent(DataFormat.PLAIN_TEXT).toString();
                        Task task = modelManager.findById(Task.class, Integer.parseInt(id));
                        task.setStatus(1);
                        modelManager.update(task);
                        return null;
                    }
                });
                dragDropManager.droppable(inProgress, new Callback() {
                    @Override
                    public Object call(Object param) {
                        DragEvent event = (DragEvent) param;
                        String id = event.getDragboard().getContent(DataFormat.PLAIN_TEXT).toString();
                        Task task = modelManager.findById(Task.class, Integer.parseInt(id));
                        task.setStatus(2);
                        modelManager.update(task);
                        return null;
                    }
                });
                dragDropManager.droppable(done, new Callback() {
                    @Override
                    public Object call(Object param) {
                        DragEvent event = (DragEvent) param;
                        String id = event.getDragboard().getContent(DataFormat.PLAIN_TEXT).toString();
                        Task task = modelManager.findById(Task.class, Integer.parseInt(id));
                        task.setStatus(3);
                        modelManager.update(task);
                        return null;
                    }
                });
            }
        }).start();
    }

    public void addToViewList(VBox viewList, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            VBox taskCard = new VBox();
            taskCard.getStyleClass().add("task");
            Label taskTitle = new Label(task.getName());
            taskTitle.setWrapText(true);
            taskCard.getChildren().add(taskTitle);
            if (task.getUser() != null) {
                Label taskAssignTo =
                        new Label("Assigned to: " + task.getUser().getFirstName() + " " + task.getUser().getLastName() + " <" + task.getUser().getEmail() + ">");
                taskAssignTo.setWrapText(true);
                taskAssignTo.getStyleClass().add("assignTo");
                taskCard.getChildren().add(taskAssignTo);
            }

            SceneManager sceneManager = SceneManager.getInstance();
            if (sceneManager.getSession().containsKey("loggedInUser")) {
                User loggedInUser = (User) sceneManager.getSession().get("loggedInUser");

//                if (sceneManager.isLoggedInUserManager()) {
                    taskCard.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            sceneManager.getSession().put("task", task);
                            sceneManager.loadPartial(sceneManager.getMainContainer(), "/codecubes/views/partials/task-form.fxml");
                        }
                    });
//                }

                if ((task.getUser() != null && loggedInUser.getId() == task.getUser().getId()) || sceneManager.isLoggedInUserManager()) {
                    DragDropManager.getInstance().draggable(taskCard, new Callback() {
                        @Override
                        public Object call(Object param) {
                            ClipboardContent content = new ClipboardContent();
                            content.putString(String.valueOf(task.getId()));
                            return content;
                        }
                    });
                }
            }
            viewList.getChildren().add(taskCard);
        }
    }

}
