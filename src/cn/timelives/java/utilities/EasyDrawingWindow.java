package cn.timelives.java.utilities;

import cn.timelives.java.utilities.structure.Pair;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/*
 * Created at 2018/12/2 22:16
 * @author liyicheng
 */
public class EasyDrawingWindow extends Application {
    private boolean closed = false;

    volatile static EasyDrawingWindow instance;
    static CountDownLatch latch = new CountDownLatch(1);


    public EasyDrawingWindow(){
        instance = this;
        latch.countDown();
    }

    @Override
    public void start(Stage stage) throws Exception {
//        Group root = new Group(canvas);
//        Scene s = new Scene(root, 500, 500, Color.WHITE);
//        stage.setScene(s);
//        stage.setTitle("Canvas");
//        stage.show();
////        drawCanvas();
    }

    private Pair<Stage, Canvas> initDrawingStage(Stage stage, double width, double height, String title){
        stage.setTitle(title);
        Canvas can = new Canvas(width,height);
        Pane pane = new Pane(can);
        Scene s = new Scene(pane);
        stage.setScene(s);

        can.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putImage(can.snapshot(null,null));
                clipboard.setContent(clipboardContent);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Canvas copying operation");
                alert.setHeaderText(null);
                alert.setContentText("Succeeded in copying the image!");
                alert.showAndWait();
            }
        });
        return new Pair<>(stage,can);
    }

    public Pair<Stage, Canvas> createNewStage(double width, double height, String title){
        Stage s = new Stage();
        return initDrawingStage(s,width,height,title);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    private static volatile boolean created = false;

    static synchronized void create(){
        if(created){
            return;
        }
        new Thread(()-> Application.launch(EasyDrawingWindow.class)).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        created = true;
    }


    /**
     * Creates a new stage and a canvas.
     * @param width
     * @param height
     * @param title
     * @return
     */
    public static Pair<Stage, Canvas> createCanvas(double width,double height,String title){
        create();
        BlockingQueue<Pair<Stage, Canvas>> re = new ArrayBlockingQueue<>(1);
        Platform.runLater(()->{
            var t = instance.createNewStage(width, height, title);
            re.add(t);
        });
        try {
            return re.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


}
