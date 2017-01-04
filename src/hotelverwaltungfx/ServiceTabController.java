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
    * Diese Klasse is für die Arbeit mit der Service-Tabelle verantwortlich ist.
    */

public class ServiceTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButt;

    @FXML
    private Button removeButt;

    @FXML
    private TextField descrField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<String> pricetypeCBox;

    @FXML
    private ComboBox<String> serviceCBox;

    /**
    * füllt das Formular mit den Daten aus der Datenbank.
    *
    * @param  event  Reaktion auf die Adressewahl
    * @see    ActionEvent
    */
    @FXML
    void handleServiceCBoxChangeAction(ActionEvent event) { 
        if (!serviceCBox.getItems().isEmpty()){  
        if (!serviceCBox.getValue().equals("New Service")){
                    addButt.setText("Apply");
                    removeButt.setDisable(false);
                    try {
                        ResultSet rs = DBInterface.getResultSet("SELECT description FROM service where description = '" + serviceCBox.getValue() + "'");
                        while(rs.next()){                            
                            descrField.setText(rs.getString("description"));
                        } 
                        
                        ObservableList<String> pricetypeData = FXCollections.observableArrayList();                        
                        rs = DBInterface.getResultSet("SELECT pricetype.description\n" +
                                                      "FROM service, pricetype\n" +
                                                      "WHERE service.pricetype = pricetype.id && "
                                                      + "service.description =  '" + serviceCBox.getValue() + "'");
                        while (rs.next()){pricetypeData.add(rs.getString("description"));}
                        pricetypeCBox.setItems(pricetypeData);
                        pricetypeCBox.getSelectionModel().selectFirst();                  
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {                   
                    addButt.setText("Add");
                    descrField.setText("");
                    priceField.setText("");
                    removeButt.setDisable(true);                                      
                    try {
                        ObservableList<String> pricetypeData = FXCollections.observableArrayList();  
                        ResultSet rs = DBInterface.getResultSet("SELECT description FROM pricetype");
                        while (rs.next()){pricetypeData.add(rs.getString("description"));}  
                        pricetypeCBox.setItems(pricetypeData);
                        pricetypeCBox.getSelectionModel().selectFirst();
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }
    }
    
    @FXML
    void handlePriceTypeCBoxChangeAction(ActionEvent event) {
         if (!serviceCBox.getValue().equals("New Service")){
             try {
                        ResultSet rs = DBInterface.getResultSet("SELECT price FROM service, pricetype "
                                + "WHERE service.pricetype = pricetype.id && "
                                + "pricetype.description = '" + pricetypeCBox.getValue()
                                + "' && service.description = '" + serviceCBox.getValue() + "'");
                        while(rs.next()){                            
                            priceField.setText(rs.getString("price"));
                        } 
             } catch (SQLException ex) {
                        Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
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
    void handleAddApplyServiceAction(ActionEvent event) throws IOException{ 
        if (descrField.getText().isEmpty() || priceField.getText().isEmpty() || !HelpFunctions.isDouble(priceField.getText())){
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
                  int pricetype = 0;
                  ResultSet rs = DBInterface.getResultSet("SELECT id FROM pricetype where description = '" + pricetypeCBox.getValue() + "'");
                  while (rs.next()){pricetype = rs.getInt("id");}

                  if (serviceCBox.getValue().equals("New Service")){  
                       int nextId = DBInterface.getNextId("service");
                       System.out.println("NEXTID = " + nextId);
                       DBInterface.executeStatement("INSERT INTO service(id, description, price, pricetype) "
                        + "VALUES (" 
                        + nextId + ", '" 
                        + descrField.getText() + "', " 
                        + priceField.getText() + ", " 
                        + pricetype + ");");
                        if (!serviceCBox.getItems().contains(descrField.getText()))serviceCBox.getItems().add(descrField.getText());
                        serviceCBox.getSelectionModel().select(descrField.getText());
                  } else {
                       int index = 0;
                       rs = DBInterface.getResultSet("SELECT id\n" +
                                                     "FROM service\n" +
                                                     "WHERE description = '" + serviceCBox.getValue() + "' && pricetype = " + pricetype);
                       while (rs.next()){index = rs.getInt("id");}                 
                       DBInterface.executeStatement("UPDATE `service` SET "
                               + "`description`= '" + descrField.getText()
                               + "',`price`=" + priceField.getText()
                               + ",`pricetype`=" + pricetype  
                               + " WHERE id = "  + index); 

                       String str = descrField.getText();
                       if (!serviceCBox.getItems().contains(str)){
                           serviceCBox.getItems().remove(serviceCBox.getValue());
                           serviceCBox.getItems().add(str);
                           serviceCBox.getSelectionModel().select(str);
                       }
                  }
              } catch (SQLException ex) {
                  Logger.getLogger(ServiceTabController.class.getName()).log(Level.SEVERE, null, ex);
              }
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
    void handleRemoveServiceAction(ActionEvent event) throws IOException {
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
                int pricetype = 0;
                ResultSet rs = DBInterface.getResultSet("SELECT id FROM pricetype where description = '" + pricetypeCBox.getValue() + "'");
                while (rs.next()){pricetype = rs.getInt("id");}
                DBInterface.executeStatement("DELETE FROM `service` WHERE description = '" + serviceCBox.getValue() + "' && pricetype = " + pricetype);

                serviceCBox.getItems().remove(serviceCBox.getValue());
                serviceCBox.getSelectionModel().selectFirst();
                rs = DBInterface.getResultSet("SELECT description FROM service group by description");
                while (rs.next()){
                    String str = rs.getString("description");
                    if (!serviceCBox.getItems().contains(str)) serviceCBox.getItems().add(str);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ServiceTabController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

     /**
    * initialisiert und stellt die GUI-Komponenten ein.
    */
    @FXML
    void initialize() {
        assert addButt != null : "fx:id=\"AddButt\" was not injected: check your FXML file 'ServiceTab.fxml'.";
        assert removeButt != null : "fx:id=\"RemoveButt\" was not injected: check your FXML file 'ServiceTab.fxml'.";
        assert descrField != null : "fx:id=\"descrField\" was not injected: check your FXML file 'ServiceTab.fxml'.";
        assert priceField != null : "fx:id=\"priceField\" was not injected: check your FXML file 'ServiceTab.fxml'.";
        assert pricetypeCBox != null : "fx:id=\"pricetypeCBox\" was not injected: check your FXML file 'ServiceTab.fxml'.";
        assert serviceCBox != null : "fx:id=\"serviceCBox\" was not injected: check your FXML file 'ServiceTab.fxml'.";

        ObservableList<String> serviceData = FXCollections.observableArrayList();          
        serviceData.add("New Service");
        serviceCBox.setItems(serviceData); 
        serviceCBox.getSelectionModel().selectFirst();        
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT description FROM service group by description");
            while (rs.next()){serviceData.add(rs.getString("description"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
