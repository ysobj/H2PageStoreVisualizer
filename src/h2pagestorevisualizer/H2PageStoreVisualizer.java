/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author ysobj
 */
public class H2PageStoreVisualizer extends Application {

    protected static final Map<Integer, String> typeMap = new HashMap<>();

    static {
        typeMap.put(0, "TYPE_EMPTY");//An empty page.
        typeMap.put(1, "TYPE_DATA_LEAF");//A data leaf page (without overflow: + FLAG_LAST).
        typeMap.put(2, "TYPE_DATA_NODE");//A data node page (never has overflow pages).
        typeMap.put(3, "TYPE_DATA_OVERFLOW"); //A data overflow page (the last page: + FLAG_LAST).
        typeMap.put(4, "TYPE_BTREE_LEAF"); //A b-tree leaf page (without overflow: + FLAG_LAST).
        typeMap.put(5, "TYPE_BTREE_NODE"); //A b-tree node page (never has overflow pages).
        typeMap.put(6, "TYPE_FREE_LIST"); //A page containing a list of free pages (the last page: + FLAG_LAST).
        typeMap.put(7, "TYPE_STREAM_TRUNK"); //A stream trunk page.
        typeMap.put(8, "TYPE_STREAM_DATA"); //A stream data page.
    }

    @Override
    public void start(Stage primaryStage) {
        byte[] datafile = getDatafile("/sample.h2.db");
        int pageSize = readInt(datafile, 48);
        int pages = (int) datafile.length / pageSize;
        int cnt = 0;
        Group root = new Group();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 10; j++) {
                if (cnt < pages) {
                    Rectangle rect = null;
                    switch (cnt) {
                        case 0:
                            rect = createStaticPage(root, "contains a static file header");
                            break;
                        case 1:
                            rect = createStaticPage(root, "contain the variable file header");
                            break;
                        case 2:
                            rect = createStaticPage(root, "contain the variable file header(copy)");
                            break;
                        case 3:
                            rect = createStaticPage(root, "contains the first free list page");
                            break;
                        case 4:
                            rect = createStaticPage(root, "contains the meta table root page");
                            break;
                        default:
                            rect = createOtherPage(root, 0);

                    }
                    rect.setX(10 + j * 40);
                    rect.setY(10 + i * 40);
                    root.getChildren().add(rect);
                    cnt++;
                }
            }
        }
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("H2PageStoreVisualizer.css").toExternalForm());
        primaryStage.setTitle("Hello World!? " + pageSize + " * " + pages);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public int readInt(byte[] buff, int pos) {
        //org.h2.store.Data#readInt()
        int x = (buff[pos] << 24)
                + ((buff[pos + 1] & 0xff) << 16)
                + ((buff[pos + 2] & 0xff) << 8)
                + (buff[pos + 3] & 0xff);
        return x;
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

    protected Rectangle createStaticPage(Group root, String desc) {
        Rectangle rect = createPage(root);
        rect.getStyleClass().add("static-page");
        Tooltip t = new Tooltip(desc);
        Tooltip.install(rect, t);
        return rect;
    }

    protected Rectangle createOtherPage(Group root, int type) {
        Rectangle rect = createPage(root);
        Tooltip t = new Tooltip("Other Page");
        rect.getStyleClass().add("other-page");
        Tooltip.install(rect, t);
        return rect;
    }

    protected Rectangle createPage(Group root) {
        Rectangle rect = new Rectangle(30, 30);

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
