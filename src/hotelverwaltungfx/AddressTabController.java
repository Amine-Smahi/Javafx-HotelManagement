package hotelverwaltungfx;

import db.DBInterface;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    * Diese Klasse is für die Arbeit mit der Addresse-Tabelle verantwortlich ist.
    */

public class AddressTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButt;

    @FXML
    private ComboBox<String> addressCBox;

    @FXML
    private TextField cityField;

    @FXML
    private Button removeButt;

    @FXML
    private TextField streetField;

    @FXML
    private TextField zipField;

    @FXML
    private ComboBox<String> stateCBox;
    
    private final ObservableList<String> addressData = FXCollections.observableArrayList();   

    
    /**
    * füllt das Adressenformular mit den Daten aus der Datenbank.
    *
    * @param  event  Reaktion auf die Adressewahl
    * @see    ActionEvent
    */
    @FXML
    void handleAddressCBoxChangeAction(ActionEvent event) {
        if (!addressCBox.getItems().isEmpty()){ 
        if (!addressCBox.getValue().equals("New Address")){
                    addButt.setText("Apply");
                    removeButt.setDisable(false);
                    try {
                        ResultSet rs = DBInterface.getResultSet("SELECT city FROM address where id = " + addressCBox.getValue());
                        while(rs.next()){                            
                            cityField.setText(rs.getString("city"));
                        } 
                        
                        rs = DBInterface.getResultSet("SELECT zip FROM address where id = " + addressCBox.getValue());
                        while(rs.next()){                            
                            zipField.setText(rs.getString("zip"));
                        }
                        
                        rs = DBInterface.getResultSet("SELECT street FROM address where id = " + addressCBox.getValue());
                        while(rs.next()){                            
                            streetField.setText(rs.getString("street"));
                        }
                        
                        rs = DBInterface.getResultSet("SELECT fullname FROM address, state where address.id = " + addressCBox.getValue() + " && state.id = address.state");
                        while(rs.next()){                            
                           stateCBox.getSelectionModel().select(rs.getString("fullname"));
                        }
                                          
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {                   
                    addButt.setText("Add");
                    cityField.setText("");
                    streetField.setText("");
                    zipField.setText("");
                    stateCBox.getSelectionModel().clearSelection();
                    removeButt.setDisable(true); 
                }
        }
    }
    
    /**
    * speichert die Daten in die Datenbank.
    *
    * @param  event  Reaktion auf die Buttonklick
    * @throws IOException  when Input- oder Output- 
    *                      ausnahme auftritt
    * @see    ActionEvent
    */
    @FXML
    void handleAddApplyAddressAction(ActionEvent event) throws IOException { 
        if (cityField.getText().isEmpty() || zipField.getText().isEmpty() || streetField.getText().isEmpty()){
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
            try {
                int stateId = 0;
                ResultSet rs = DBInterface.getResultSet("SELECT id FROM state where fullname = '" + stateCBox.getValue() + "'");
                while (rs.next()){stateId = rs.getInt("id");}
                
                int nextId;
                if (addressCBox.getValue().equals("New Address")){  
                      // Der neue Adresseneintrag wird angelegt.
                      nextId = DBInterface.getNextId("address");
                      DBInterface.executeStatement("INSERT INTO `address`(`id`, `street`, `zip`, `city`, `state`) "
                      + "VALUES (" 
                      + nextId + ", '" 
                      + streetField.getText() + "', '" 
                      + zipField.getText() + "', '"
                      + cityField.getText() + "', "
                      + stateId + ");");        
                      addressCBox.getItems().add(String.valueOf(nextId)); 
                } else {
                    // Der Adresseneintrag wird geändert.
                    nextId = Integer.parseInt(addressCBox.getValue());
                    DBInterface.executeStatement("UPDATE `address` SET "
                                                    + "`street`='" + streetField.getText()
                                                    + "',`zip`='" + zipField.getText()
                                                    + "',`city`='" + cityField.getText()
                                                    + "',`state`=" + stateId
                                                    + " where id = " + addressCBox.getValue());     
                }
                refresh();
                addressCBox.setValue(String.valueOf(nextId)); 
            } catch (SQLException ex) {
                Logger.getLogger(AddressTabController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    /**
    * löscht einen Adresseneintrag aus der Datenbank.
    *
    * @param  event  Reaktion
    * @throws IOException  when Input- oder Output- 
    *                      ausnahme auftritt
    * @see    ActionEvent
    */
    @FXML
    void handleRemoveAddressAction(ActionEvent event) throws IOException {
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
            DBInterface.executeStatement("DELETE FROM `address` WHERE id = " + addressCBox.getValue());
            addressCBox.getItems().remove(addressCBox.getValue());
            addressCBox.getSelectionModel().selectFirst();
            stateCBox.getSelectionModel().selectFirst();
        }  
    }
    
    /**
    * initialisiert und stellt die GUI-Komponenten des Adressenfromulars ein.
    */
    @FXML
    void initialize() {
        assert addButt != null : "fx:id=\"addButt\" was not injected: check your FXML file 'AddressTab.fxml'.";
        assert addressCBox != null : "fx:id=\"addressCBox\" was not injected: check your FXML file 'AddressTab.fxml'.";
        assert cityField != null : "fx:id=\"cityField\" was not injected: check your FXML file 'AddressTab.fxml'.";
        assert removeButt != null : "fx:id=\"removeButt\" was not injected: check your FXML file 'AddressTab.fxml'.";
        assert streetField != null : "fx:id=\"streetField\" was not injected: check your FXML file 'AddressTab.fxml'.";
        assert zipField != null : "fx:id=\"zipField\" was not injected: check your FXML file 'AddressTab.fxml'.";

        addressCBox.setCellFactory(new Callback_Address());   
        addressCBox.setButtonCell(addressCBox.getCellFactory().call(null));
        addressCBox.setItems(addressData); 
        refresh();
        addressCBox.getSelectionModel().selectFirst();         
        ObservableList<String> stateData = FXCollections.observableArrayList(); 
        stateCBox.setItems(stateData); 
        stateCBox.getSelectionModel().selectFirst();        
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT fullname FROM state");
            while (rs.next()){stateData.add(rs.getString("fullname"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * füllt addresseComboBox mit Daten.
    */
    private void refresh(){
        addressData.removeAll(addressData);
        addressData.add("New Address");
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM address ORDER BY id");
            while (rs.next()){addressData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
