package codecubes.core;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Created by msaeed on 1/25/2017.
 */
public class DragDropManager {

    private static DragDropManager instance;

    private DragDropManager() {

    }

    public static DragDropManager getInstance() {
        if (instance == null) {
            instance = new DragDropManager();
        }
        return instance;
    }

    public void draggable(VBox taskCard, Callback callback) {
        taskCard.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = taskCard.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = (ClipboardContent) callback.call(event);
                db.setContent(content);
                event.consume();
            }
        });
    }

    public void draggable(ObservableList childerns) {
        for(int i = 0; i < childerns.size(); i++) {
            Pane task = (Pane) childerns.get(i);
            task.setOnDragDetected(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    Dragboard db = task.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("123");
                    db.setContent(content);
                    event.consume();
                }
            });

        }
    }

    public void droppable(VBox node, Callback callback) {
        node.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        node.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Pane p = (Pane) event.getGestureSource();
                if (p.getParent() != node) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    callback.call(event);
                }
                node.getChildren().add((Pane) event.getGestureSource());
                event.consume();
            }
        });
    }

}
