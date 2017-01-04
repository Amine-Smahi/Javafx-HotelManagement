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
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
    * Diese Klasse is für die Arbeit mit der Guest-Tabelle verantwortlich ist.
    */

public class GuestTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButt;

    @FXML
    private ImageView addPersonButt;

    @FXML
    private ComboBox<String> corrAdrCBox;

    @FXML
    private ComboBox<String> guestCBox;

    @FXML
    private ComboBox<String> personCBox;

    @FXML
    private ListView<String> personList;

    @FXML
    private Button removeButt;

    @FXML
    private ImageView removePersonButt;

    private final ObservableList<String> listData = FXCollections.observableArrayList();
    private final ObservableList<String> corrData = FXCollections.observableArrayList();
    private final ObservableList<String> guestData = FXCollections.observableArrayList();

    /**
    * füllt das Kundenformular mit den Daten aus der Datenbank.
    *
    * @param  event  Reaktion auf die Adressewahl
    * @see    ActionEvent
    */    
    @FXML
    void handleGuestCBoxChangeAction(ActionEvent event) {
        personList.getItems().removeAll(listData);   
        corrData.clear();
        if (!guestCBox.getItems().isEmpty()){  
            if (!guestCBox.getValue().equals("New Guest")){
                        addButt.setText("Apply");
                        removeButt.setDisable(false);
                        try {
                            ResultSet rs = DBInterface.getResultSet("SELECT person FROM person_is_guest where guest = " + guestCBox.getValue());
                            while(rs.next()){                            
                               personList.getItems().add(rs.getString("person"));
                               ResultSet rs1 = DBInterface.getResultSet("SELECT address FROM person where id = " + rs.getString("person"));
                               while(rs1.next()){ 
                                 String str = rs1.getString("address");
                                 if (!corrData.contains(str)) corrData.add(str);
                               }
                            }  

                            rs = DBInterface.getResultSet("SELECT address FROM guest where id = "+ guestCBox.getValue());
                            while(rs.next()){  
                                corrAdrCBox.getSelectionModel().select(rs.getString("address"));
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                        }
            } else {                   
                        addButt.setText("Add");                     
                        personCBox.getSelectionModel().clearSelection();
                        corrAdrCBox.getSelectionModel().clearSelection();
                        removeButt.setDisable(true); 
            }
        }
    }
        
    /**
    * fügt eine Person in die Liste hinzu.
    *
    * @param  event  Reaktion auf einen Klick   
    * @see    MouseEvent
    */
    @FXML
    void addPerson(MouseEvent event) { 
        if (!listData.contains(personCBox.getValue())){
            try {
                listData.add(personCBox.getValue());
                ResultSet rs = DBInterface.getResultSet("SELECT address FROM person where person.id = " + personCBox.getValue());
                while(rs.next()){ 
                      String str = rs.getString("address");
                      if (!corrData.contains(str)) corrData.add(str);
                }
            } catch (SQLException ex) {
                Logger.getLogger(GuestTabController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
     /**
    * löscht eine Person aus der Liste.
    *
    * @param  event  Reaktion auf einen Klick   
    * @see    MouseEvent
    */
    @FXML
    void removePerson(MouseEvent event) {
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT address FROM person where person.id = " + personList.getSelectionModel().getSelectedItem());
            while(rs.next()){
                 corrAdrCBox.getItems().remove(rs.getString("address"));
            }
            listData.remove(personList.getSelectionModel().getSelectedItem());
        } catch (SQLException ex) {
            Logger.getLogger(GuestTabController.class.getName()).log(Level.SEVERE, null, ex);
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
    void handleAddApplyGuestAction(ActionEvent event) throws IOException {
        if (!corrAdrCBox.getSelectionModel().isEmpty()){
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
             if (guestCBox.getValue().equals("New Guest")){   
                  nextId = DBInterface.getNextId("guest");
                  DBInterface.executeStatement("INSERT INTO `guest`(`id`, `address`)"
                          + " VALUES (" + nextId
                          + "," + corrAdrCBox.getValue()
                          + ")");        
                  guestCBox.getItems().add(String.valueOf(nextId));  
                  
                  for (String str : listData){
                      DBInterface.executeStatement("INSERT INTO `person_is_guest`(`person`, `guest`)"
                              + " VALUES (" + str
                              + "," + nextId
                              + ")");
                  }
            } else {
                  nextId = Integer.parseInt(guestCBox.getValue());
                  
                  try {
                      ResultSet rs = DBInterface.getResultSet("SELECT `person` FROM `person_is_guest` WHERE guest = " + guestCBox.getValue());
                      while (rs.next()){
                          String str = rs.getString("person");
                          DBInterface.executeStatement("DELETE FROM `person_is_guest` WHERE person = " + str + "&& guest = " + guestCBox.getValue());
                      }  
                  } catch (SQLException ex) {
                      Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                  }
                 
                  for (String str : listData){
                      DBInterface.executeStatement("INSERT INTO `person_is_guest`(`person`, `guest`)"
                              + " VALUES (" + str
                              + "," + guestCBox.getValue()
                              + ")");
                  }
                  
                  DBInterface.executeStatement("UPDATE `guest` SET "
                          + "`address`=" + corrAdrCBox.getValue()
                          + " WHERE id = " + guestCBox.getValue());       
            }
            refresh();
            guestCBox.setValue(String.valueOf(nextId)); 
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
    void handleRemoveGuestAction(ActionEvent event) throws IOException {
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
            try {
                          ResultSet rs = DBInterface.getResultSet("SELECT `person` FROM `person_is_guest` WHERE guest = " + guestCBox.getValue());
                          while (rs.next()){
                              String str = rs.getString("person");
                              DBInterface.executeStatement("DELETE FROM `person_is_guest` WHERE person = " + str + "&& guest = " + guestCBox.getValue());
                          }  
                      } catch (SQLException ex) {
                          Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                      }

            DBInterface.executeStatement("DELETE FROM `guest` WHERE id = " + guestCBox.getValue());
            guestCBox.getItems().remove(guestCBox.getValue());
            guestCBox.getSelectionModel().selectFirst();
            personCBox.getSelectionModel().clearSelection();
            corrAdrCBox.getSelectionModel().clearSelection();
        }
    }

    /**
    * initialisiert und stellt die GUI-Komponenten ein.
    */
    @FXML
    void initialize() {
        assert addButt != null : "fx:id=\"addGuestButt\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert addPersonButt != null : "fx:id=\"addPersonButt\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert corrAdrCBox != null : "fx:id=\"corrAdrCBox\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert guestCBox != null : "fx:id=\"guestCBox\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert personCBox != null : "fx:id=\"personCBox\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert personList != null : "fx:id=\"personList\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert removeButt != null : "fx:id=\"removeGuestButt\" was not injected: check your FXML file 'GuestTab.fxml'.";
        assert removePersonButt != null : "fx:id=\"removePersonButt\" was not injected: check your FXML file 'GuestTab.fxml'.";

        corrAdrCBox.setCellFactory(new Callback_Address());
        guestCBox.setCellFactory(new Callback_Guest());
        corrAdrCBox.setButtonCell(corrAdrCBox.getCellFactory().call(null));
        guestCBox.setButtonCell(guestCBox.getCellFactory().call(null));
        personCBox.setCellFactory(new Callback_Person());
        personCBox.setButtonCell(personCBox.getCellFactory().call(null));
        personList.setItems(listData);
        corrAdrCBox.setItems(corrData);
        
        
        guestCBox.setCellFactory(new Callback_Guest());
        guestCBox.setItems(guestData); 
        refresh();
        guestCBox.getSelectionModel().selectFirst();   
        
        ObservableList<String> personData = FXCollections.observableArrayList(); 
        personCBox.setItems(personData); 
        personCBox.getSelectionModel().selectFirst();        
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM person ORDER BY id");
            while (rs.next()){personData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * füllt guestComboBox mit Daten.
    */
    private void refresh(){
        guestData.removeAll(guestData);
        guestData.add("New Guest");
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM guest ORDER BY id");
            while (rs.next()){guestData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
