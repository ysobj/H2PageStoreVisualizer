/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author ysobj
 */
public class H2PageStoreVisualizer extends Application {

    protected static Color staticPages = Color.web("red", 0.2);
    protected static Color otherPages = Color.web("white", 0.2);

    @Override
    public void start(Stage primaryStage) {
        byte[] datafile = getDatafile("/sample.h2.db");
        int pages = (int) datafile.length / 2048;
        int cnt = 0;
        Group root = new Group();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (cnt < pages) {
                    cnt++;
                    Rectangle rect = null;
                    if (cnt >= 1 && cnt <= 5) {
                        rect = createRect(root, staticPages);
                    } else {
                        rect = createRect(root, otherPages);
                    }
                    rect.setX(10 + j * 40);
                    rect.setY(10 + i * 40);
                    root.getChildren().add(rect);
                }
            }
        }
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        primaryStage.setTitle("Hello World!?" + pages);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    protected byte[] getDatafile(String filePath) {
        try {
            InputStream input = this.getClass().getResourceAsStream(filePath);
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            int v;
            while ((v = input.read()) != -1) {
                output.write(v);
            }
            return output.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(H2PageStoreVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected Rectangle createRect(Group root, Color strokeColor) {
        Rectangle rect = new Rectangle(30, 30, Color.web("white", 0.05));
        rect.setStrokeType(StrokeType.OUTSIDE);
        rect.setStroke(strokeColor);
        rect.setStrokeWidth(3);

        rect.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            ParallelTransition transition = new ParallelTransition();
            FadeTransition fadeTransition
                    = new FadeTransition(Duration.millis(1000), rect);
            fadeTransition.setFromValue(1.0f);
            fadeTransition.setToValue(0.1f);
            fadeTransition.setAutoReverse(false);

            ScaleTransition scaleTransition
                    = new ScaleTransition(Duration.millis(1000), rect);
            scaleTransition.setToX(20f);
            scaleTransition.setToY(20f);
            scaleTransition.setAutoReverse(false);

            transition.getChildren().addAll(fadeTransition, scaleTransition);

            transition.setOnFinished((ActionEvent event1) -> {
                root.getChildren().remove(rect);
            });

            transition.play();
        });
        return rect;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
