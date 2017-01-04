/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelverwaltungfx;

import db.DBInterface;
import db.DBConnector;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Nosenko
 */
public class LauncherController implements Initializable {        
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane launcher_buttonsPanel;

    @FXML
    private AnchorPane loginPane;
    
    @FXML
    private ToggleButton launcher_dbsettingsButton;

    @FXML
    private Button launcher_exitButton;

    @FXML
    private AnchorPane launcher_imagePanel;

    @FXML
    private ImageView launcher_imageView;

    @FXML
    private AnchorPane launcher_mainPanel;

    @FXML
    private Button launcher_startButton;  
    
    @FXML
    private Stage launcher_loginStage;
    
    @FXML
    private Stage launcher_stage;
    
    public void setStage(Stage stage){
        this.launcher_stage = stage;
    }
    
    @FXML
    private void handleStartProgram(ActionEvent event) throws IOException, SQLException {
        if (DBConnector.getInstance().getConnection() == null || DBConnector.getInstance().getConnection().isClosed()){   
            Connection c = DBConnector.getInstance().createConnection("jdbc:mysql://localhost:3306/", "root", "", "hotel");
            if (c == null){
                Stage s = new Stage(StageStyle.TRANSPARENT);
                s.initModality(Modality.APPLICATION_MODAL);            
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportDialog.fxml"));
                Scene scene = new Scene((Parent)loader.load());    
                ReportDialogController controller = (ReportDialogController)loader.getController();
                controller.setMessage("The program could not connect\nto DB with default settings.\nCheck DB-Setting");                
                controller.setStage(s);
                scene.setFill(Color.TRANSPARENT);
                s.setScene(scene);   
                s.show();
            } else {                
                startMainWindow();
            }
        } else {              
            startMainWindow();
        }
    }
    
    public void checkDB() throws SQLException{
        ArrayList<String> log = new ArrayList<>();
        DBInterface.createHotelTables(DBConnector.getInstance().getConnection(), log);
    }
    
    public void startMainWindow() throws IOException, SQLException{
        checkDB();
        launcher_stage.close();
        Stage s = new Stage(StageStyle.TRANSPARENT);        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = (Parent) loader.load();
        MainWindowController controller = (MainWindowController)loader.getController();
        controller.setStage(s);        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        s.setScene(scene); 
        s.show();
    }
        
    @FXML
    private void handleExitButtonAction(ActionEvent event) throws SQLException {
        if (DBConnector.getInstance().getConnection() != null) DBConnector.getInstance().getConnection().close();
        System.exit(0);       
    }
    
    @FXML
    private void handleDBSettingsButtonAction(ActionEvent event) throws IOException {  
        LoginController controller;
            
            BoxBlur bb = new BoxBlur();
            launcher_buttonsPanel.setDisable(true);
            launcher_buttonsPanel.setEffect(bb);
            bb.setWidth(5);
            bb.setHeight(5);
            bb.setIterations(1);
            
            launcher_loginStage = new Stage(StageStyle.TRANSPARENT);
            launcher_loginStage.initModality(Modality.APPLICATION_MODAL);            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = (Parent) loader.load();
            controller = (LoginController)loader.getController();
            controller.setCloseButton(launcher_dbsettingsButton);
            controller.setStage(launcher_loginStage);
            controller.setParentAncor(launcher_buttonsPanel);
            Scene scene = new Scene(root);          
            scene.setFill(Color.TRANSPARENT);
            launcher_loginStage.setScene(scene); 
            launcher_loginStage.setX(launcher_dbsettingsButton.localToScene(launcher_dbsettingsButton.getScene().getWindow().getX() + launcher_dbsettingsButton.getWidth() , launcher_dbsettingsButton.getScene().getWindow().getY()).getX());
            launcher_loginStage.setY(launcher_dbsettingsButton.localToScene(launcher_dbsettingsButton.getScene().getWindow().getX() + launcher_dbsettingsButton.getWidth() , launcher_dbsettingsButton.getScene().getWindow().getY()).getY());
            launcher_loginStage.show();          
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert launcher_buttonsPanel != null : "fx:id=\"launcher_buttonsPanel\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert launcher_dbsettingsButton != null : "fx:id=\"launcher_dbsettingsButton\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert launcher_exitButton != null : "fx:id=\"launcher_exitButton\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert launcher_imagePanel != null : "fx:id=\"launcher_imagePanel\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert launcher_imageView != null : "fx:id=\"launcher_imageView\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert launcher_mainPanel != null : "fx:id=\"launcher_mainPanel\" was not injected: check your FXML file 'Launcher.fxml'.";
        assert launcher_startButton != null : "fx:id=\"launcher_startButton\" was not injected: check your FXML file 'Launcher.fxml'.";
    }  
}
