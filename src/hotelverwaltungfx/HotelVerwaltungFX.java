/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelverwaltungfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Nosenko
 */
public class HotelVerwaltungFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.TRANSPARENT);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Launcher.fxml"));
        Parent root = (Parent) loader.load();
        LauncherController controller = (LauncherController)loader.getController();
        controller.setStage(stage);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        
        
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}