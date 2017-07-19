package codecubes;

import codecubes.core.SceneManager;
import codecubes.seeders.Seeder;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by msaeed on 1/21/2017.
 */
public class Main extends Application {

    private static Stage primaryStage;
    private static Scene scene;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;
    public static final String TITLE = "Task Management";



    public static void setPrimaryStage(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle(TITLE);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setScene(Scene scene) {
        Main.scene = scene;
        Main.primaryStage.setScene(Main.scene);
    }

    public static Scene getScene() {
        return scene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Seeder.getInstance().run();
            }
        }).start();
        setPrimaryStage(primaryStage);
        SceneManager sceneManager = SceneManager.getInstance();
        setScene(new Scene((Parent) sceneManager.loadFxml("/codecubes/views/layouts/login.fxml"), WIDTH, HEIGHT));
        getPrimaryStage().show();
    }
}
