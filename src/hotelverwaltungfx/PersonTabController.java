package hotelverwaltungfx;

import db.DBInterface;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
    * Diese Klasse is für die Arbeit mit der Person-Tabelle verantwortlich ist.
    */

public class PersonTabController {
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButt;

    @FXML
    private ComboBox<String> addressCBox;

    @FXML
    private ComboBox<String> genderCBox;

    @FXML
    private GridPane gridpane;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> nationalityCBox;

    @FXML
    private ComboBox<String> personCBox;

    @FXML
    private Button removeButt;

    @FXML
    private TextField surnameField;

    @FXML
    private ComboBox<String> titleCBox;
    
    private DatePicker birthdayDatePicker;
    
    private final ObservableList<String> personData = FXCollections.observableArrayList();  

      /**
    * füllt das Personformular mit den Daten aus der Datenbank.
    *
    * @param  event  Reaktion auf die Adressewahl
    * @see    ActionEvent
    */
    
    @FXML
    void handlePersonCBoxChangeAction(ActionEvent event) {
        if (!personCBox.getItems().isEmpty()){  
        if (!personCBox.getValue().equals("New Person")){
                    addButt.setText("Apply");
                    removeButt.setDisable(false);
                    try {
                        ResultSet rs = DBInterface.getResultSet("SELECT gender FROM person where id = " + personCBox.getValue());
                        while(rs.next()){                            
                            genderCBox.getSelectionModel().select(rs.getString("gender"));
                        } 
                        
                        rs = DBInterface.getResultSet("SELECT name FROM person where id = " + personCBox.getValue());
                        while(rs.next()){                            
                            nameField.setText(rs.getString("name"));
                        }
                        
                        rs = DBInterface.getResultSet("SELECT surname FROM person where id = " + personCBox.getValue());
                        while(rs.next()){                            
                            surnameField.setText(rs.getString("surname"));
                        }
                        
                        rs = DBInterface.getResultSet("SELECT birthdate FROM person where id = " + personCBox.getValue());
                        while(rs.next()){                            
                            birthdayDatePicker.setSelectedDate(rs.getDate("birthdate"));
                        }
                        
                        rs = DBInterface.getResultSet("SELECT description FROM person, nationality where person.id = " + personCBox.getValue() + " && nationality.id = person.nationality");
                        while(rs.next()){                            
                           nationalityCBox.getSelectionModel().select(rs.getString("description"));
                        }
                        
                        rs = DBInterface.getResultSet("SELECT address.id FROM address, person where person.id = " + personCBox.getValue() + " && person.address = address.id");
                        while(rs.next()){                            
                           addressCBox.getSelectionModel().select(rs.getString("address.id"));
                        }
                                  
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {                   
                    addButt.setText("Add");
                    nameField.setText("");
                    surnameField.setText("");                   
                    genderCBox.getSelectionModel().select("m");
                    addressCBox.getSelectionModel().clearSelection();
                    nationalityCBox.getSelectionModel().clearSelection();
                    birthdayDatePicker.setSelectedDate(null);
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
    void handleAddApplyPersonAction(ActionEvent event) { 
        try {
            int nationalityId = 0;
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM nationality where description = '" + nationalityCBox.getValue() + "'");
            while (rs.next()){nationalityId = rs.getInt("id");}
            Calendar c = Calendar.getInstance();
            c.setTime(birthdayDatePicker.getSelectedDate());
            int nextId;
            if (personCBox.getValue().equals("New Person")){   
                 // Die Formularsdaten sind nicht korrekt. Es wird ein Reportdialog aufgerufen.
                  nextId = DBInterface.getNextId("person");
                  DBInterface.executeStatement("INSERT INTO `person`(`id`, `name`, `surname`, `birthdate`, `nationality`, `gender`, `address`) "
                  + "VALUES (" 
                  + nextId + ", '" 
                  + nameField.getText() + "', '" 
                  + surnameField.getText() + "', DATE('"
                  + new java.sql.Date(c.getTimeInMillis()) + "'), "
                  + nationalityId + ", '"
                  + genderCBox.getValue() + "', "        
                  + addressCBox.getValue() + ");");        
                  personCBox.getItems().add(String.valueOf(nextId));              
                 
            } else {
                // Die Formularsdaten sind korrekt. Sie werden in die Datenbank eingetragen.
                  nextId = Integer.parseInt(personCBox.getValue());
                  DBInterface.executeStatement("UPDATE `person` SET "                         
                          + "`name`='" + nameField.getText()
                          + "',`surname`='" + surnameField.getText()
                          + "',`birthdate`=DATE('" + new java.sql.Date(c.getTimeInMillis())
                          + "'),`nationality`=" + nationalityId
                          + ",`gender`='" + genderCBox.getValue()
                          + "',`address`=" + addressCBox.getValue()
                          + " WHERE id = " + personCBox.getValue());     
            }
            refresh();
            personCBox.setValue(String.valueOf(nextId));
        } catch (SQLException ex) {
            Logger.getLogger(AddressTabController.class.getName()).log(Level.SEVERE, null, ex);
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
    void handleRemovePersonAction(ActionEvent event) throws IOException {
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
                 ResultSet rs = DBInterface.getResultSet("SELECT `guest` FROM `person_is_guest` WHERE person = " + personCBox.getValue());
                 while (rs.next()){
                        String str = rs.getString("guest");
                        DBInterface.executeStatement("DELETE FROM `guest` WHERE id = " + str);
                        DBInterface.executeStatement("DELETE FROM `person_is_guest` WHERE guest = " + str + "&& person = " + personCBox.getValue());
                 }  
            } catch (SQLException ex) {
                Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
            }           
            DBInterface.executeStatement("DELETE FROM `person` WHERE id = " + personCBox.getValue());
            personCBox.getItems().remove(personCBox.getValue());
            personCBox.getSelectionModel().selectFirst();
            nationalityCBox.getSelectionModel().clearSelection();
            genderCBox.getSelectionModel().selectFirst();
            addressCBox.getSelectionModel().clearSelection();
        }
    }
    
     /**
    * initialisiert und stellt die GUI-Komponenten ein.
    */
    @FXML
    void initialize() {
        assert addButt != null : "fx:id=\"addButt\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert addressCBox != null : "fx:id=\"addressCBox\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert genderCBox != null : "fx:id=\"genderCBox\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert gridpane != null : "fx:id=\"gridpane\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert nameField != null : "fx:id=\"nameField\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert nationalityCBox != null : "fx:id=\"nationalityCBox\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert personCBox != null : "fx:id=\"personCBox\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert removeButt != null : "fx:id=\"removeButt\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert surnameField != null : "fx:id=\"surnameField\" was not injected: check your FXML file 'PersonTab.fxml'.";
        assert titleCBox != null : "fx:id=\"titleCBox\" was not injected: check your FXML file 'PersonTab.fxml'.";

        birthdayDatePicker = new DatePicker(Locale.ENGLISH);
        birthdayDatePicker.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        birthdayDatePicker.getCalendarView().todayButtonTextProperty().set("Today");
        birthdayDatePicker.getCalendarView().setShowWeeks(false);
        birthdayDatePicker.alignmentProperty().set(Pos.CENTER);        
        birthdayDatePicker.getStylesheets().add("hotelverwaltungfx/DatePicker.css");        
        gridpane.add(birthdayDatePicker, 1, 4);
        GridPane.setMargin(birthdayDatePicker, new Insets(0,0,0,15));
        birthdayDatePicker.setMinWidth(105);
        
        personCBox.setCellFactory(new Callback_Person());
        addressCBox.setCellFactory(new Callback_Address());
        personCBox.setButtonCell(personCBox.getCellFactory().call(null));
        addressCBox.setButtonCell(addressCBox.getCellFactory().call(null));
        personCBox.setItems(personData); 
        refresh();
        personCBox.getSelectionModel().selectFirst();      
        ObservableList<String> genderData = FXCollections.observableArrayList(); 
        genderCBox.setItems(genderData);
        genderCBox.getSelectionModel().selectFirst();        
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT gender FROM gender");
            while (rs.next()){genderData.add(rs.getString("gender"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ObservableList<String> nationalityData = FXCollections.observableArrayList(); 
        nationalityCBox.setItems(nationalityData); 
        nationalityCBox.getSelectionModel().selectFirst();        
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT description FROM nationality");
            while (rs.next()){nationalityData.add(rs.getString("description"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ObservableList<String> addressData = FXCollections.observableArrayList();  
        addressCBox.setItems(addressData); 
        addressCBox.getSelectionModel().selectFirst();        
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM address ORDER BY id");
            while (rs.next()){addressData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * füllt personComboBox mit Daten.
    */
    private void refresh(){
        personData.removeAll(personData);
        personData.add("New Person");
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM person ORDER BY id");
            while (rs.next()){personData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

