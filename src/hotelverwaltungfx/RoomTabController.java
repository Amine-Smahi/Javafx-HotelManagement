package hotelverwaltungfx;

import db.HelpFunctions;
import db.DBInterface;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
    * Diese Klasse is für die Arbeit mit der Room-Tabelle verantwortlich ist.
    */

public class RoomTabController {

     @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButt;

    @FXML
    private TextField descrField;

    @FXML
    private TextField priceField;

    @FXML
    private Button removeButt;

    @FXML
    private ComboBox<String> roomCBox;
    
    private final ObservableList<String> roomData = FXCollections.observableArrayList();

     /**
    * speichert die Daten in die Datenbank.
    *
    * @param  event  Reaktion auf die Buttonklick
    * @throws IOException  when Input- oder Output- 
    *                      ausnahme auftritt
    * @see    ActionEvent
    */
    @FXML
    void handleAddApplyRoomAction(ActionEvent event) throws IOException {
        if (priceField.getText().isEmpty() || descrField.getText().isEmpty() || !HelpFunctions.isDouble(priceField.getText())){
            // Die Formularsdaten sind nicht korrekt. Es wird ein Reportdialog aufgerufen.
            Stage s = new Stage(StageStyle.TRANSPARENT);
            s.initModality(Modality.APPLICATION_MODAL);            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportDialog.fxml"));
            Scene scene = new Scene((Parent)loader.load());    
            ReportDialogController controller = (ReportDialogController)loader.getController();
            controller.setMessage("Input data is invalid");                
            controller.setStage(s);
            scene.setFill(Color.TRANSPARENT);
            s.setScene(scene);   
            s.show();
        } else {
            // Die Formularsdaten sind korrekt. Sie werden in die Datenbank eingetragen.
            int nextId;
            if (roomCBox.getValue().equals("New Room")){   
                  nextId = DBInterface.getNextId("room");
                  DBInterface.executeStatement("INSERT INTO room(id, description, price) "
                  + "VALUES (" 
                  + nextId + ", '" 
                  + descrField.getText() + "', " 
                  + priceField.getText() + ");");
                  roomCBox.getItems().add(String.valueOf(nextId)); 
                  
            } else {
                  nextId = Integer.parseInt(roomCBox.getValue());
                  DBInterface.executeStatement("UPDATE room "
                          + "SET description = '" + descrField.getText()
                          + "', price = " + priceField.getText()
                          + " WHERE id = " + roomCBox.getValue());     
            }  
            refresh();
            roomCBox.setValue(String.valueOf(nextId));
        }
    }

     /**
    * löscht die Daten aus der Datenbank.
    *
    * @param  event  Reaktion
    * @throws IOException  when Input- oder Output- 
    *                      ausnahme auftritt
    * @see    ActionEvent
    */
    @FXML
    void handleRemoveRoomAction(ActionEvent event) throws IOException {
        Stage s = new Stage(StageStyle.TRANSPARENT);
        s.initModality(Modality.APPLICATION_MODAL);            
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfirmationDialog.fxml"));
        Scene scene = new Scene((Parent)loader.load());    
        ConfirmationDialogController controller = (ConfirmationDialogController)loader.getController();
        controller.setMessage("All connected data will be removed.\n Will you continue?");
        controller.setStage(s);
        scene.setFill(Color.TRANSPARENT);
        s.setScene(scene); 
        s.setWidth(260);
        s.setHeight(152);
        s.showAndWait();
        
        if (controller.getResult()){
            DBInterface.executeStatement("DELETE FROM `room` WHERE id = " + roomCBox.getValue());
            roomCBox.getItems().remove(roomCBox.getValue());
            roomCBox.getSelectionModel().selectFirst();
        }
    }

     /**
    * initialisiert und stellt die GUI-Komponenten ein.
    */
    @FXML
    void initialize() {
        assert addButt != null : "fx:id=\"addButt\" was not injected: check your FXML file 'RoomTab.fxml'.";
        assert descrField != null : "fx:id=\"descrField\" was not injected: check your FXML file 'RoomTab.fxml'.";
        assert priceField != null : "fx:id=\"priceField\" was not injected: check your FXML file 'RoomTab.fxml'.";
        assert removeButt != null : "fx:id=\"removeButt\" was not injected: check your FXML file 'RoomTab.fxml'.";
        assert roomCBox != null : "fx:id=\"roomCBox\" was not injected: check your FXML file 'RoomTab.fxml'.";
    
        roomCBox.setCellFactory(new Callback_Room());  
        roomCBox.setButtonCell(roomCBox.getCellFactory().call(null));
        roomCBox.setItems(roomData);
        refresh();
        roomCBox.getSelectionModel().selectFirst();    
        // füllt das Formular mit den Daten aus der Datenbank.   
        roomCBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> selected, String oldFruit, String newFruit) { 
              if (!roomCBox.getItems().isEmpty()){  
                if (!roomCBox.getValue().equals("New Room")){
                    addButt.setText("Apply");
                    removeButt.setDisable(false);
                    try {
                        ResultSet rs = DBInterface.getResultSet("SELECT description FROM room where id = " + roomCBox.getValue());
                        while(rs.next()){                            
                            descrField.setText(rs.getString("description"));
                        }   
                        rs = DBInterface.getResultSet("SELECT price FROM room where id = " + roomCBox.getValue());
                        while(rs.next()){
                            priceField.setText(rs.getString("price"));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    addButt.setText("Add");
                    descrField.setText("");
                    priceField.setText("");
                    removeButt.setDisable(true);
                }
            }      
            }
        });
        
    }
    
    /**
    * füllt roomComboBox mit Daten.
    */
    private void refresh(){
        roomData.removeAll(roomData);
        roomData.add("New Room");
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM room ORDER BY id");
            while (rs.next()){roomData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
