/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package h2pagestorevisualizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

import h2pagestorevisualizer.page.H2Page;
import h2pagestorevisualizer.page.H2PageFactory;

/**
 *
 * @author ysobj
 */
public class H2PageStoreVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) {
        byte[] datafile = getDatafile("/sample.h2.db");
        int pageSize = readInt(datafile, 48);
        System.out.println(String.format("pageSize: %d ", pageSize));
        int pages = (int) datafile.length / pageSize;
        int cnt = 0;
        Group root = new Group();
        for (int i = 0; cnt < pages; i++) {
            for (int j = 0; j < 10; j++) {
                if (cnt < pages) {
                    Rectangle rect = createRectangle(cnt, root, datafile, pageSize);
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

    private Rectangle createRectangle(int cnt, Group root, byte[] datafile, int pageSize) {
        Rectangle rect;
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
//                        case 3:
//                            rect = createStaticPage(root, "contains the first free list page");
//                            break;
//                        case 4:
//                            rect = createStaticPage(root, "contains the meta table root page");
//                            break;
            default:
                String styleClass = "other-page";
                if (cnt == 3 || cnt == 4) {
                    styleClass = "static-page";
                }
                byte[] page = Arrays.copyOfRange(datafile, pageSize * cnt, pageSize * (cnt + 1));
                H2Page h2page = H2PageFactory.create(cnt,page);
                System.out.println(h2page.getPageTypeDesc());
                rect = createOtherPage(root, styleClass, h2page);

        }
        return rect;
    }

    protected Rectangle createStaticPage(Group root, String desc) {
        Rectangle rect = createPage(root, true);
        rect.getStyleClass().add("static-page");
        Tooltip t = new Tooltip(desc);
        Tooltip.install(rect, t);
        return rect;
    }

    protected Rectangle createOtherPage(Group root, String styleClass, H2Page h2page) {
        Rectangle rect = createPage(root, false);
        String desc = h2page.getPageTypeDesc();
        Tooltip t = new Tooltip(desc);
        rect.getStyleClass().add(styleClass);
        Tooltip.install(rect, t);
        return rect;
    }

    protected Rectangle createPage(Group root, final boolean isStaticPage) {
        Rectangle rect = new Rectangle(30, 30);
        rect.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            System.out.println("MOUSE_ENTERED");
            if (isStaticPage) {
                rect.getStyleClass().remove("static-page");
                rect.getStyleClass().add("static-page-selected");
            } else {
                rect.getStyleClass().remove("other-page");
                rect.getStyleClass().add("other-page-selected");
            }
        });
        rect.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            System.out.println("MOUSE_EXITED");
            if (isStaticPage) {
                rect.getStyleClass().remove("static-page-selected");
                rect.getStyleClass().add("static-page");
            } else {
                rect.getStyleClass().remove("other-page-selected");
                rect.getStyleClass().add("other-page");
            }
        });
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
