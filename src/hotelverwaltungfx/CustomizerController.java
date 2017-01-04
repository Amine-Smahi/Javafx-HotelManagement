package hotelverwaltungfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;


public class CustomizerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Tab addressTab;

    @FXML
    private Tab guestTab;

    @FXML
    private Tab orderTab;

    @FXML
    private Tab personTab;

    @FXML
    private Tab roomTab;

    @FXML
    private Tab serviceTab;

    @FXML
    void addressTabShowing(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddressTab.fxml"));         
        addressTab.setContent((AnchorPane)loader.load());
    }
    
    @FXML
    void addressTabClosing(Event event) {
        addressTab.setContent(null);
    }
    
    @FXML
    void guestTabShowing(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GuestTab.fxml"));         
        guestTab.setContent((AnchorPane)loader.load());
    }
    
    @FXML
    void guestTabClosing(Event event) {
        guestTab.setContent(null);
    }

    @FXML
    void orderTabShowing(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderTab.fxml"));         
        orderTab.setContent((AnchorPane)loader.load());
    }
    
    @FXML
    void orderTabClosing(Event event) {
        orderTab.setContent(null);
    }

    @FXML
    void personTabShowing(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonTab.fxml"));         
        personTab.setContent((AnchorPane)loader.load());
    }
    
    @FXML
    void personTabClosing(Event event) {
        personTab.setContent(null);
    }

    @FXML
    void roomTabShowing(Event event) throws IOException {        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RoomTab.fxml"));         
        roomTab.setContent((AnchorPane)loader.load());
    } 
    
    @FXML
    void roomTabClosing(Event event) {
        roomTab.setContent(null);
    }

    @FXML
    void serviceTabShowing(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiceTab.fxml"));         
        serviceTab.setContent((AnchorPane)loader.load());
    }
    
    @FXML
    void serviceTabClosing(Event event){
        serviceTab.setContent(null);
    }

    @FXML
    void initialize() {
        assert addressTab != null : "fx:id=\"addressTab\" was not injected: check your FXML file 'Customizer.fxml'.";
        assert guestTab != null : "fx:id=\"guestTab\" was not injected: check your FXML file 'Customizer.fxml'.";
        assert orderTab != null : "fx:id=\"orderTab\" was not injected: check your FXML file 'Customizer.fxml'.";
        assert personTab != null : "fx:id=\"personTab\" was not injected: check your FXML file 'Customizer.fxml'.";
        assert roomTab != null : "fx:id=\"roomTab\" was not injected: check your FXML file 'Customizer.fxml'.";
        assert serviceTab != null : "fx:id=\"serviceTab\" was not injected: check your FXML file 'Customizer.fxml'.";
    }
}
