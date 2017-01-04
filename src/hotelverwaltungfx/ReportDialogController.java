package hotelverwaltungfx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Dialog mit einer Nachricht
 * @author Nosenko
 */
public class ReportDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button okButt;

    @FXML
    private Label textMessage;
    
    private Stage s;

    @FXML
    void ok(ActionEvent event) {
        s.close();
    }
    
    @FXML
    void setStage(Stage s){
        this.s = s;
    }
    
    @FXML
    void setMessage(String str){
        textMessage.setText(str);
    } 

    @FXML
    void initialize() {
        assert okButt != null : "fx:id=\"okButt\" was not injected: check your FXML file 'ReportDialog.fxml'.";
        assert textMessage != null : "fx:id=\"textMessage\" was not injected: check your FXML file 'ReportDialog.fxml'.";
    }
}
