package hotelverwaltungfx;

import db.DBInterface;
import db.DBConnector;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Nosenko
 */
public class LoginController implements Initializable {
    
    @FXML
    private ImageView closeArrow;
    
     @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button checkButt;

    @FXML
    private TextField connField;

    @FXML
    private Label connLabel;

    @FXML
    private Label dbLabel;

    @FXML
    private TextField dbnameField;

    @FXML
    private CheckBox defaultCheckBox;

    @FXML
    private TextField loginField;

    @FXML
    private Label loginLabel;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private PasswordField passField;

    @FXML
    private Label passLabel;

    @FXML
    private Text textMessage;
    
    @FXML
    private ToggleButton butt;
    
    private Stage thisStage;
    
    private AnchorPane parentStage;
    
    public void setStage(Stage thisStage){
        this.thisStage = thisStage;
    }
    
    public void setParentAncor(AnchorPane parentStage){
        this.parentStage = parentStage;
    }
    
    public void setCloseButton(ToggleButton butt){
        this.butt = butt;
    }
    
    @FXML
    private void handleCheckButtonAction(ActionEvent event) throws SQLException, IOException{
        Connection c = DBConnector.getInstance().createConnection(connField.getText(), loginField.getText(), passField.getText(), dbnameField.getText());
        if (c == null){
           textMessage.setFill(Color.RED);
           textMessage.setText("Connection is failed");
           if (DBConnector.getInstance().createConnection(connField.getText(), loginField.getText(), passField.getText(), "")!=null){
                Stage s = new Stage(StageStyle.TRANSPARENT);
                s.initModality(Modality.APPLICATION_MODAL);            
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfirmationDialog.fxml"));
                Scene scene = new Scene((Parent)loader.load());    
                ConfirmationDialogController controller = (ConfirmationDialogController)loader.getController();
                controller.setMessage("The Database does not exist.\n Should it be created?");
                controller.setStage(s);
                scene.setFill(Color.TRANSPARENT);
                s.setScene(scene);    
                s.setWidth(260);
                s.setHeight(152);        
                s.setX(loginPane.getScene().getWindow().getX() + (loginPane.getScene().getWidth() / 2) - (s.getWidth() / 2));
                s.setY(loginPane.getScene().getWindow().getY() + loginPane.getScene().getHeight() / 2 - s.getHeight() / 2);
                s.showAndWait();
                if (controller.getResult()){
                    DBInterface.executeStatement("CREATE DATABASE " + dbnameField.getText());
                } else {
                    DBConnector.getInstance().getConnection().close();
                }
            }
        } else {
            textMessage.setFill(Color.GREEN);
            textMessage.setText("Connection is established");
        }        
    }
    
    @FXML
    private void handleCheckBoxAction(MouseEvent event) throws SQLException {
        if (defaultCheckBox.isSelected()){
            loginField.setDisable(true);
            passField.setDisable(true);
            connField.setDisable(true);
            dbnameField.setDisable(true);
            loginField.setOpacity(0.5);
            passField.setOpacity(0.5);
            connField.setOpacity(0.5);
            dbnameField.setOpacity(0.5);
            loginField.setText("root");
            passField.setText("");
            connField.setText("jdbc:mysql://localhost:3306/");
            dbnameField.setText("hotel");
        } else {
            loginField.setDisable(false);
            passField.setDisable(false);
            connField.setDisable(false);
            dbnameField.setDisable(false);
            loginField.setOpacity(1);
            passField.setOpacity(1);
            connField.setOpacity(1);
            dbnameField.setOpacity(1);
            loginField.setText("user");
            passField.setText("*****");
            connField.setText("jdbc:mysql://localhost:3306/");
            dbnameField.setText("dbname");
        }
    }
    
    @FXML
    private void handleClickOnCloseArrow(MouseEvent event){     
        parentStage.setDisable(false);
        parentStage.setEffect(null);
        thisStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert checkButt != null : "fx:id=\"checkButt\" was not injected: check your FXML file 'Login.fxml'.";
        assert connField != null : "fx:id=\"connField\" was not injected: check your FXML file 'Login.fxml'.";
        assert connLabel != null : "fx:id=\"connLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginField != null : "fx:id=\"loginField\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginLabel != null : "fx:id=\"loginLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginPane != null : "fx:id=\"loginPane\" was not injected: check your FXML file 'Login.fxml'.";
        assert passField != null : "fx:id=\"passField\" was not injected: check your FXML file 'Login.fxml'.";
        assert passLabel != null : "fx:id=\"passLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert textMessage != null : "fx:id=\"textMessage\" was not injected: check your FXML file 'Login.fxml'.";
        assert defaultCheckBox != null : "fx:id=\"textMessage\" was not injected: check your FXML file 'Login.fxml'.";
    }
}
