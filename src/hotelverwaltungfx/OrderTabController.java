package hotelverwaltungfx;

import db.HelpFunctions;
import db.DBInterface;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
    * Diese Klasse is für die Arbeit mit der Order-Tabelle verantwortlich ist.
    */

public class OrderTabController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private ImageView billCreate;

    @FXML
    private Text dateError;
    
    @FXML
    private URL location;

    @FXML
    private Button addButt;

    @FXML
    private ImageView addServiceButt;

    @FXML
    private ComboBox<String> guestCBox;

    @FXML
    private ComboBox<String> orderCBox;

    @FXML
    private GridPane gridpane;
    
    @FXML
    private Button removeButt;

    @FXML
    private TextField countField;
    
    @FXML
    private ImageView removeServiceButt;

    @FXML
    private ComboBox<String> roomCBox;

    @FXML
    private ComboBox<String> serviceCBox;

    @FXML
    private ListView<String> serviceList;
    
    private final ObservableList<String> listData = FXCollections.observableArrayList();
    
    private final ObservableMap<String,Integer> mapData = FXCollections.observableHashMap();
    
    private final ObservableList<String> orderData = FXCollections.observableArrayList();
    
    private DatePicker toDatePicker;
    
    private DatePicker fromDatePicker;    

    /**
    * füllt das Buchungsformular mit den Daten aus der Datenbank.
    *
    * @param  event  Reaktion auf die Adressewahl
    * @see    ActionEvent
    */
    
    @FXML
    void handleOrderCBoxChangeAction(ActionEvent event) { 
        for (String key : serviceList.getItems()){
                    mapData.remove(key); 
            }
        serviceList.getItems().removeAll(listData);
        serviceCBox.getSelectionModel().clearSelection();
        if (!orderCBox.getItems().isEmpty()){  
            if (!orderCBox.getValue().equals("New Order")){
                try {
                    addButt.setText("Apply");
                    removeButt.setDisable(false);

                    ResultSet rs = DBInterface.getResultSet("SELECT guest FROM order_room where id = " + orderCBox.getValue());
                    while(rs.next()){
                        guestCBox.getSelectionModel().select(rs.getString("guest"));
                    }

                    rs = DBInterface.getResultSet("SELECT room FROM order_room where id = " + orderCBox.getValue());
                    while(rs.next()){
                        roomCBox.getSelectionModel().select(rs.getString("room"));
                    }

                    rs = DBInterface.getResultSet("SELECT fromDate FROM order_room where id = " + orderCBox.getValue());
                    while(rs.next()){                            
                        fromDatePicker.setSelectedDate(rs.getDate("fromDate"));
                    }

                    rs = DBInterface.getResultSet("SELECT toDate FROM order_room where id = " + orderCBox.getValue());
                    while(rs.next()){                            
                        toDatePicker.setSelectedDate(rs.getDate("toDate"));
                    }      
                    rs = DBInterface.getResultSet("SELECT service, count FROM order_service where order_room = " + orderCBox.getValue());
                    while(rs.next()){                            
                        mapData.put(rs.getString("service"), Integer.valueOf(rs.getInt("count")));
                        listData.add(rs.getString("service"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(OrderTabController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {                   
                addButt.setText("Add");                     
                guestCBox.getSelectionModel().clearSelection();
                roomCBox.getSelectionModel().clearSelection();           
                toDatePicker.setSelectedDate(null);
                fromDatePicker.setSelectedDate(null);
                countField.setText("");
                removeButt.setDisable(true); 
            }
        }
    }    
    
    /**
    * fügt eine Service in die Liste hinzu.
    *
    * @param  event  Reaktion auf einen Klick   
    * @see    MouseEvent
    */
    @FXML
    void addPerson(MouseEvent event){
        try{
            if (Integer.parseInt(countField.getText()) > 0){
                if (!mapData.containsKey(serviceCBox.getValue())){ 
                    mapData.put(serviceCBox.getValue(), Integer.valueOf(countField.getText()));
                    listData.add(serviceCBox.getValue());
                } else {
                    mapData.replace(serviceCBox.getValue(), Integer.valueOf(countField.getText()));
                    listData.remove(serviceCBox.getValue());
                    listData.add(serviceCBox.getValue());
                }
            }   
        }catch(NumberFormatException ex){
            
        }
    }

    /**
    * löscht eine Service aus der Liste.
    *
    * @param  event  Reaktion auf einen Klick   
    * @see    MouseEvent
    */
    @FXML
    void removePerson(MouseEvent event) {
        mapData.remove(serviceList.getSelectionModel().getSelectedItem());
        listData.remove(serviceList.getSelectionModel().getSelectedItem());
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
    void handleAddApplyOrderAction(ActionEvent event) throws IOException {
        if (guestCBox.getSelectionModel().isEmpty() || roomCBox.getSelectionModel().isEmpty() || !dateError.getText().equals("")){
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
            Calendar c1 = Calendar.getInstance();
            c1.setTime(fromDatePicker.getSelectedDate());
            Calendar c2 = Calendar.getInstance();
            c2.setTime(toDatePicker.getSelectedDate());
            int nextId;
            if (orderCBox.getValue().equals("New Order")){
                  nextId = DBInterface.getNextId("order_room");
                  DBInterface.executeStatement("INSERT INTO `order_room`(`id`, `fromDate`, `toDate`, `guest`, `room`)"
                          + " VALUES ("
                          + nextId
                          + ", DATE('" + new java.sql.Date(c1.getTimeInMillis())
                          + "'), DATE('" + new java.sql.Date(c2.getTimeInMillis())
                          + "'), " + guestCBox.getValue()
                          + ", " + roomCBox.getValue()
                          + ")");        
                  orderCBox.getItems().add(String.valueOf(nextId));    
                  
                  for (String str : mapData.keySet()){
                      DBInterface.executeStatement("INSERT INTO `order_service`(`count`, `order_room`, `service`)"
                              + " VALUES (" + mapData.get(str)
                              + "," + nextId
                              + "," + str
                              + ")");
                  }
            } else {
                  nextId = Integer.parseInt(orderCBox.getValue());
                  try {
                      ResultSet rs = DBInterface.getResultSet("SELECT service FROM order_service WHERE order_room = " + orderCBox.getValue());
                      while (rs.next()){                          
                          DBInterface.executeStatement("DELETE FROM `order_service` WHERE service = " + rs.getString("service") + "&& order_room = " + orderCBox.getValue());
                      }  
                  } catch (SQLException ex) {
                      Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                  }
                 
                  for (String str : mapData.keySet()){
                      DBInterface.executeStatement("INSERT INTO `order_service`(`count`, `order_room`, `service`)"
                              + " VALUES (" + mapData.get(str)
                              + "," + orderCBox.getValue()
                              + "," + str
                              + ")");      
                  }                
                  DBInterface.executeStatement("UPDATE `order_room` SET "
                          + "`fromDate`=DATE('" + new java.sql.Date(c1.getTimeInMillis())
                          + "'),`toDate`=DATE('" + new java.sql.Date(c2.getTimeInMillis())
                          + "'),`guest`=" + guestCBox.getValue()
                          + ",`room`=" + roomCBox.getValue()
                          + " WHERE id = " + orderCBox.getValue());    
            }
            refresh();
            orderCBox.setValue(String.valueOf(nextId));
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
    void handleRemoveOrderAction(ActionEvent event) throws IOException {
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
                  ResultSet rs = DBInterface.getResultSet("SELECT service FROM order_service WHERE order_room = " + orderCBox.getValue());
                  while (rs.next()){                          
                     DBInterface.executeStatement("DELETE FROM `order_service` WHERE service = " + rs.getString("service") + "&& order_room = " + orderCBox.getValue());
                  }  
            } catch (SQLException ex) {
                  Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
            }

            DBInterface.executeStatement("DELETE FROM `order_room` WHERE id = " + orderCBox.getValue());
            toDatePicker.setDateFormat(new SimpleDateFormat("yyyy-mm-dd"));
            fromDatePicker.setDateFormat(new SimpleDateFormat("yyyy-mm-dd"));
            guestCBox.getSelectionModel().clearSelection();
            roomCBox.getSelectionModel().clearSelection();
        }
    }
    
    /**
    * ActionEvent auf Auswahl eines Elementes in der Liste.
     * @param arg0
    */
    @FXML 
    public void handleMouseClickOnServiceList(MouseEvent arg0) {
        if (serviceList.getSelectionModel().getSelectedItem()!=null){
            serviceCBox.getSelectionModel().select(serviceList.getSelectionModel().getSelectedItem());
            countField.setText(String.valueOf(mapData.get(serviceList.getSelectionModel().getSelectedItem())));
        }
    }
    
    /**
    * ActionEvent auf Selektion des Datums.
    */
    @FXML
    void handleChangeRoomSelection(ActionEvent event) {
        checkDates();
    }
    
    /**
    * prüft die Daten auf die Richtigkeit.
    */
    public void checkDates() {
        if (roomCBox.getValue() == null){
            dateError.setText("Select the room");
        } else if (fromDatePicker.getSelectedDate() == null || toDatePicker.getSelectedDate() == null || toDatePicker.invalidProperty().get() ||  fromDatePicker.invalidProperty().get()){
            dateError.setText("Select the dates");
        } else {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(fromDatePicker.getSelectedDate());
            Calendar c2 = Calendar.getInstance();
            c2.setTime(toDatePicker.getSelectedDate());
            if (!HelpFunctions.isDateCorrect(new java.sql.Date(c1.getTimeInMillis()), new java.sql.Date(c2.getTimeInMillis()))){
                dateError.setText("Dates are incorrect");
            } else {
                try {
                    ResultSet rs;
                    if (!orderCBox.getValue().equals("New Order")){
                        rs = DBInterface.getResultSet("SELECT EXISTS(SELECT * FROM order_room "
                                                   + "WHERE room = " + roomCBox.getValue() +
                                                   " and id <> " + orderCBox.getValue() +
                                                   " and fromDate <= DATE('" + new java.sql.Date(c2.getTimeInMillis()) + 
                                                   "') and toDate >= DATE('" + new java.sql.Date(c1.getTimeInMillis()) +"') LIMIT 1) as result");
                    } else {
                        rs = DBInterface.getResultSet("SELECT EXISTS(SELECT * FROM order_room "
                                                   + "WHERE room = " + roomCBox.getValue() +                                                  
                                                   " and fromDate <= DATE('" + new java.sql.Date(c2.getTimeInMillis()) + 
                                                   "') and toDate >= DATE('" + new java.sql.Date(c1.getTimeInMillis()) +"') LIMIT 1) as result"); 
                    }
                    while (rs.next()){                            
                         if (rs.getInt("result") == 1) {
                             dateError.setText("The room is occupied");
                             addButt.setDisable(true);
                         } else {
                             dateError.setText("");
                             addButt.setDisable(false);
                         }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(OrderTabController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    
    /**
    *  legt eine Rechnung an.
     * @param arg0
     * @throws java.io.IOException
    */
    @FXML 
    public void createBill(MouseEvent arg0) throws IOException {
       if (!orderCBox.getValue().equals("New Order")){
           double room_price = 0;
           double service_price = 0;
           int days = 0;
           int persons = 0;
           try {
               ResultSet rs = DBInterface.getResultSet("select DATEDIFF(toDate,fromDate) as days from order_room where id = " + orderCBox.getValue());
               while(rs.next()){                            
                     days  = rs.getInt("days");
               }
               
               rs = DBInterface.getResultSet("SELECT count(*) as persons FROM person_is_guest where guest = " + guestCBox.getValue());
               while(rs.next()){    
                     persons = rs.getInt("persons");
               }
               
               rs = DBInterface.getResultSet("SELECT price FROM room where id = " + roomCBox.getValue());
               while(rs.next()){    
                     room_price = rs.getDouble("price") * days;
               }
                              
                     String html = "";
                     html += "<html><head><title>Bill</title></head><body>";
                     html += "<h1>" + "Hotel Bill" + "</h1>";
                     html += "<h3>" + "An : "; 
                     rs = DBInterface.getResultSet("SELECT name, surname, directtext FROM person_is_guest, person, gender where person_is_guest.person = person.id and person.gender = gender.gender and guest = " + guestCBox.getValue());
                        while(rs.next()){    
                              html += rs.getString("directtext") + " " + rs.getString("name") + " " + rs.getString("surname") + "<br>";
                              html += "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
                        }
                     html += "</h3><h3>";
                     rs = DBInterface.getResultSet("SELECT abbreviation, zip, city, street FROM state, address, guest where state.id = address.state and guest.address = address.id and guest.id = " + guestCBox.getValue());
                        while(rs.next()){    
                              html += rs.getString("abbreviation") + " " + rs.getString("city") + " " + rs.getString("zip") + " " + rs.getString("street");
                        }
                     html += "</h3>";
                     html += "<table border=1>";
                     html += "<tr align=center valign=middle><th>OrderID</th><th>CheckIn</th><th>CheckOut</th><th>Days</th><th>Service  [Euro]</th><th>Room  [Euro]</th><th>Overall [Euro]</th></tr>";
                     html += "<tr align=center valign=middle>";
                     html += "<td>" + orderCBox.getValue() + "</td>";
                     html += "<td>" + fromDatePicker.getSelectedDate() + "</td>";
                     html += "<td>" + toDatePicker.getSelectedDate() + "</td>";
                     html += "<td>" + days + "</td>";
                     html += "<td>";
                     rs = DBInterface.getResultSet("SELECT service, description, count, price, pricetype FROM order_service, service where order_service.service =  service.id and order_service.order_room = " + orderCBox.getValue());
                     while(rs.next()){  
                        double price = 0; 
                        switch(rs.getInt("pricetype")){
                            case(1):{
                                price = rs.getDouble("price") * rs.getInt("count");                                
                            break;}
                            case(2):{
                                price = rs.getDouble("price") * days;
                            break;}
                            case(3):{
                                price = rs.getDouble("price") * persons;
                            break;}
                            case(4):{
                                price = rs.getDouble("price") * persons * days;
                            break;}
                        }
                        service_price = service_price + price;
                        html += rs.getInt("count") + "x " + rs.getString("description") + " " + price + "<br>";
                     }
                     html += "<td>" + room_price;
                     html += "</td>";
                     html += "<td>" + (room_price + service_price) + "</td>";
                     html += "</tr>";
                     html += "</table>";
                     DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                     Calendar cal = Calendar.getInstance();
                     html += "<h3>" + dateFormat.format(cal.getTime()) + "</h3>";
                     html += "</body></html>";
                     
                     File dir = new File(System.getProperty("user.dir") + "/Bills");
                     if (!dir.exists()) dir.mkdir();
                     File file = new File(System.getProperty("user.dir") + "/Bills/Order" + orderCBox.getValue() + ".html");
                     file.createNewFile();
               try (FileWriter writer = new FileWriter(file, false)) {
                   writer.write(html);
                   writer.flush();
               }
           } catch (SQLException ex) {
               Logger.getLogger(OrderTabController.class.getName()).log(Level.SEVERE, null, ex);
           }
       } 
    }
    
    
    /**
    * füllt die Liste mit Daten.
    *
    * @param  event  Reaktion auf die Adressewahl
    * @see    ActionEvent
    */
    @FXML
    void handleServiceCBoxChangeAction(ActionEvent event) { 
        if (!serviceCBox.getItems().isEmpty() && !serviceCBox.getSelectionModel().isEmpty()){ 
            try {
                ResultSet rs = DBInterface.getResultSet("SELECT pricetype FROM service where id = " + serviceCBox.getValue());
                        while(rs.next()){    
                            if (rs.getInt("pricetype") == 1){
                                countField.setDisable(false);
                            } else {
                                countField.setText("1");
                                countField.setDisable(true);
                            }
                        }
            } catch (SQLException ex) {
                Logger.getLogger(OrderTabController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
           countField.setText("");
           countField.setDisable(true); 
        } 
    }

     /**
    * initialisiert und stellt die GUI-Komponenten ein.
    */
    @FXML
    void initialize() {
        assert addButt != null : "fx:id=\"addButt\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert addServiceButt != null : "fx:id=\"addServiceButt\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert billCreate != null : "fx:id=\"billCreate\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert countField != null : "fx:id=\"countField\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert dateError != null : "fx:id=\"dateError\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert gridpane != null : "fx:id=\"gridpane\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert guestCBox != null : "fx:id=\"guestCBox\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert orderCBox != null : "fx:id=\"orderCBox\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert removeButt != null : "fx:id=\"removeButt\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert removeServiceButt != null : "fx:id=\"removeServiceButt\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert roomCBox != null : "fx:id=\"roomCBox\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert serviceCBox != null : "fx:id=\"serviceCBox\" was not injected: check your FXML file 'OrderTab.fxml'.";
        assert serviceList != null : "fx:id=\"serviceList\" was not injected: check your FXML file 'OrderTab.fxml'.";

        dateError.setText("");
        
        serviceList.setItems(listData);
        orderCBox.setCellFactory(new Callback_Order());
        guestCBox.setCellFactory(new Callback_Guest());
        roomCBox.setCellFactory(new Callback_Room());
        serviceCBox.setCellFactory(new Callback_Service());
        serviceList.setCellFactory(new Callback<ListView<String>,ListCell<String>>(){
                @Override
                public ListCell<String> call(ListView<String> p) {
                    ListCell<String> cell = new ListCell<String>(){
                                @Override
                                protected void updateItem(String t, boolean bln) {
                                    super.updateItem(t, bln);
                                    if (t != null) {
                                            try { 
                                               ResultSet rs = DBInterface.getResultSet("SELECT description, pricetype FROM service WHERE id = " + t);
                                               String str = "";
                                               while (rs.next()){
                                                     ResultSet rs1 = DBInterface.getResultSet("SELECT description FROM pricetype WHERE id = " + rs.getString("pricetype"));
                                                     while (rs1.next()){
                                                         str = str + rs.getString("description") + " - " + rs1.getString("description") + " : ";
                                                     }
                                                }
                                                setText(str + mapData.get(t));
                                            } catch (SQLException ex) {
                                                Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                }
                            };
                            return cell;
                }
            });
        
        toDatePicker = new DatePicker(Locale.ENGLISH);
        toDatePicker.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        toDatePicker.getCalendarView().todayButtonTextProperty().set("Today");
        toDatePicker.getCalendarView().setShowWeeks(false);
        toDatePicker.alignmentProperty().set(Pos.CENTER);        
        toDatePicker.getStylesheets().add("hotelverwaltungfx/DatePicker.css");        
        gridpane.add(toDatePicker, 1, 4);
        GridPane.setMargin(toDatePicker, new Insets(0,0,0,15));
        toDatePicker.setMinWidth(105);
        toDatePicker.selectedDateProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
               checkDates();
            }
        });
        
        fromDatePicker = new DatePicker(Locale.ENGLISH);
        fromDatePicker.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fromDatePicker.getCalendarView().todayButtonTextProperty().set("Today");
        fromDatePicker.getCalendarView().setShowWeeks(false);
        fromDatePicker.alignmentProperty().set(Pos.CENTER);        
        fromDatePicker.getStylesheets().add("hotelverwaltungfx/DatePicker.css");        
        gridpane.add(fromDatePicker, 1, 3);
        GridPane.setMargin(fromDatePicker, new Insets(0,0,0,15));
        fromDatePicker.setMinWidth(105);
        fromDatePicker.selectedDateProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
               checkDates();               
            }
        });
        
        orderCBox.setCellFactory(new Callback_Order());   
        orderCBox.setButtonCell(orderCBox.getCellFactory().call(null));
        roomCBox.setCellFactory(new Callback_Room());   
        roomCBox.setButtonCell(roomCBox.getCellFactory().call(null));
        serviceCBox.setCellFactory(new Callback_Service());   
        serviceCBox.setButtonCell(serviceCBox.getCellFactory().call(null));
        guestCBox.setCellFactory(new Callback_Guest());   
        guestCBox.setButtonCell(guestCBox.getCellFactory().call(null));
        
        orderCBox.setItems(orderData);
        refresh();
        orderCBox.getSelectionModel().selectFirst();      
        ObservableList<String> guestData = FXCollections.observableArrayList();   
        guestCBox.setItems(guestData); 
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM guest ORDER BY id");
            while (rs.next()){guestData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ObservableList<String> roomData = FXCollections.observableArrayList(); 
        roomCBox.setItems(roomData); 
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM room ORDER BY id");
            while (rs.next()){roomData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ObservableList<String> serviceData = FXCollections.observableArrayList(); 
        serviceCBox.setItems(serviceData); 
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM service ORDER BY id");
            while (rs.next()){serviceData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * füllt orderComboBox mit Daten.
    */
    private void refresh(){     
        orderData.removeAll(orderData);
        orderData.add("New Order");
        try {
            ResultSet rs = DBInterface.getResultSet("SELECT id FROM order_room ORDER BY id");
            while (rs.next()){orderData.add(rs.getString("id"));}  
        } catch (SQLException ex) {
            Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
