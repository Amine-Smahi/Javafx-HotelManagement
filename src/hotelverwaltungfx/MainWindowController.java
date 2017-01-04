package hotelverwaltungfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Nosenko
 */
public class MainWindowController implements Initializable {
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane contextPane;

    @FXML
    private ToggleButton customizeButt;

    @FXML
    private Button exitButt;

    @FXML
    private Pane labelPane;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private AnchorPane mainStage;

    @FXML
    private ToggleButton statButt;

    @FXML
    private ToggleButton wizardButt;
    
    @FXML
    private Stage stage;
    
    public void setStage(Stage stage){
        this.stage = stage;
    }

     /**
    * Customize
    */
    @FXML
    void customizeDB(ActionEvent event) throws IOException {  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Customizer.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        root.setPrefHeight(contextPane.getHeight());
        root.setPrefWidth(contextPane.getWidth()); 
        contextPane.getChildren().clear();
        contextPane.getChildren().add(root);          
    }

     /**
    * Exit
    */
    @FXML
    void exitProgramm(ActionEvent event) {
        System.exit(0);
    }

     /**
    * Statistiken
    */
    @FXML
    void showStatistics(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Statistics.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        root.setPrefHeight(contextPane.getHeight());
        root.setPrefWidth(contextPane.getWidth()); 
        contextPane.getChildren().clear();
        contextPane.getChildren().add(root);
    }

    /**
    * Wizard
    */
    @FXML
    void wizardOrder(ActionEvent event){        
        contextPane.getChildren().clear();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      assert contextPane != null : "fx:id=\"contextPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert customizeButt != null : "fx:id=\"customizeButt\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert exitButt != null : "fx:id=\"exitButt\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert labelPane != null : "fx:id=\"labelPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert mainSplitPane != null : "fx:id=\"mainSplitPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert mainStage != null : "fx:id=\"mainStage\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert statButt != null : "fx:id=\"statButt\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert wizardButt != null : "fx:id=\"wizardButt\" was not injected: check your FXML file 'MainWindow.fxml'.";
    }
}
