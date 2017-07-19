package codecubes.core;

import codecubes.Main;
import codecubes.models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by msaeed on 1/21/2017.
 */
public class SceneManager {

    private Pane mainContainer;
    private HashMap session;
    private static SceneManager instance = null;

    private SceneManager() {
        session = new HashMap();
    }

    public HashMap getSession() {
        return session;
    }

    public void setSession(HashMap session) {
        this.session = session;
    }

    public Pane getMainContainer() {
        return mainContainer;
    }

    public void setMainContainer(Pane mainContainer) {
        this.mainContainer = mainContainer;
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public Node loadFxml(String resource) {
        try {
            return FXMLLoader.load(getClass().getResource(resource));
        } catch (IOException e) {
            System.out.println("Check - IOException: SceneManager.laodFXML()");
            return null;
        }
    }

    public void loadLayout(String resource) {
        Main.setScene(new Scene((Parent) loadFxml(resource), Main.WIDTH, Main.HEIGHT));
    }

    public Node loadPartial(Pane pane, String resource) {
        Node partial = loadFxml(resource);
        pane.getChildren().removeAll();
        pane.getChildren().setAll(partial);
        return partial;
    }

    // we can separate this method in another file called LoggedInUser
    public boolean isLoggedInUserManager() {
        if (getSession().containsKey("loggedInUser")) {
            User loggedInUser = (User) getSession().get("loggedInUser");
            return loggedInUser.isManager();
        }
        return false;
    }
}
