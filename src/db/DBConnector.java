/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *  erzeugt Verbindung mit einer Datenbank
 * @author Nosenko
 */
public class DBConnector {
        
    private final static DBConnector instance = new DBConnector(); 
    
     /**
    * Singelton
     * @return DBConnector
    */
    public static DBConnector getInstance() {
        return instance;
    }
    
    private Connection conn = null;

     /**
    *  get Verbindung    
     * @return Connection
    */
    public Connection getConnection() {
        return conn;
    }
    
    /**
    *  erzeugt Verbindung mit einer Datenbank    
     * @param url
     * @param user
     * @param password
     * @param database
     * @return Connection
     * @throws java.sql.SQLException
    */
    public Connection createConnection(String url, String user, String password, String database) throws SQLException{ 
        if (conn != null) conn.close();
        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", user);
            connectionProps.put("password", password); 
            connectionProps.put("database", database); 
            conn = DriverManager.getConnection(url + database, connectionProps);   
        } catch(SQLException ex) {
            return null;
        } 
        return conn;
    }
}
