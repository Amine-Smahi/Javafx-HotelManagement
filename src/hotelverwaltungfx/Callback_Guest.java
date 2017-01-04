/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelverwaltungfx;

import db.DBInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * dient zur Darstellung von Daten in Containers, wie ComboBoxen oder Listen
 * @author Nosenko
 */
public class Callback_Guest implements Callback<ListView<String>,ListCell<String>>{

    @Override
    public ListCell<String> call(ListView<String> p) {
        ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            if (t.equals("New Guest")){
                               setText(t);
                            } else {
                                try { 
                                   ResultSet rs = DBInterface.getResultSet("SELECT `person` FROM `person_is_guest` WHERE guest = " + t);
                                   String str = t + ":";
                                   while (rs.next()){
                                         ResultSet rs1 = DBInterface.getResultSet("SELECT name, surname FROM person WHERE id = " + rs.getString("person"));
                                         while (rs1.next()){
                                             str = str + " '" + rs1.getString("surname") + " " + rs1.getString("name") + "'";
                                         }
                                    }
                                    setText(str);
                                } catch (SQLException ex) {
                                    Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                };
                return cell;
    }
    
}