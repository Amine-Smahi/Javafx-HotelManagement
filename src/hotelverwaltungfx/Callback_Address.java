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
public class Callback_Address implements Callback<ListView<String>,ListCell<String>>{

    @Override
    public ListCell<String> call(ListView<String> p) {
        ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            if (t.equals("New Address")){
                               setText(t);
                            } else {
                                try { 
                                    ResultSet rs = DBInterface.getResultSet("SELECT state, zip, city, street FROM address where id = " + t);
                                    while (rs.next()){
                                        ResultSet rs1 = DBInterface.getResultSet("SELECT abbreviation FROM state where id = " + rs.getString("state"));
                                        while (rs1.next()){
                                            setText(t + ": " + rs1.getString("abbreviation") + " " + rs.getString("city") + " " + rs.getString("street") + " " + rs.getString("zip"));
                                        }
                                    }  
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
