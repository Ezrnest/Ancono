package cn.timelives.java.utilities;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * Created at 2018/12/2 22:16
 * @author liyicheng
 */
public class EasyDrawingWindow extends Application {
    private Canvas canvas = new Canvas(500,500);



    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane(canvas);
        Scene s = new Scene(root,500,500, Color.WHITE);
        stage.setScene(s);
        stage.setTitle("Canvas");
        stage.show();
    }

    public void show(){
        Application.launch(EasyDrawingWindow.class);
    }



    public static void main(String[] args)throws Exception {
        EasyDrawingWindow win = new EasyDrawingWindow();
        win.show();
        Platform.runLater(()->{
            Canvas c=  win.canvas;
            var g = c.getGraphicsContext2D();


        });

    }
}
