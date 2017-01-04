/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;

/** 
 * Hilfsfunktionen
 * @author Nosenko
 */
public class HelpFunctions {
    
    /** 
     * prüft, ob ein String einen Integer darstellt
     * @param str
     * @return boolean
    */    
    public static boolean isDouble(String str){
        boolean check = true;
        try{
            Double.parseDouble(str);
        }catch(NumberFormatException ex){
            check = false;
        }
        return check;
    }
    
     /** 
     * prüft, ob ein Datum vor einem anderem liegt
     * @param start1    
     * @param end1    
     * @return boolean
    */ 
    public static boolean isDateCorrect(Date start1, Date end1) {
        return start1.before(end1);
    }
    
     /** 
     * prüft, ob ein Date zwischen einem Zeitraum liegt
     * @param start1     
     * @param end1     
     * @param start2     
     * @param end2     
     * @return boolean
    */ 
    public static boolean checkDateRangeIntersection(Date start1, Date end1, Date start2, Date end2) {
        //(StartA <= EndB) and (EndA >= StartB)        
        return start1.getTime()<=end2.getTime() && end1.getTime()>=start2.getTime();
    }
    
     /** 
     * liefert Set von Monaten in einem Zeitraum zurück
     * @param start
     * @param end
     * @return boolean
    */ 
    public static LinkedList<Calendar> getMonthFromRange(Date start, Date end) {                
        LinkedList<Calendar> l = new LinkedList<>();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        while(c1.get(Calendar.MONTH) <= c2.get(Calendar.MONTH)){           
            l.add((Calendar) c1.clone());
            c1.add(Calendar.MONTH, 1);
        }
        return l;
    }
    
     /** 
     * liefert Auslastung in einem Monaten zurück
     * @param c    
     * @return boolean
     * @throws java.sql.SQLException
    */
    public static double getUsageInMonth(Calendar c) throws SQLException {
       int days = 0;
       int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
       for(int i = 0; i < max; i++){                    
           ResultSet rs = DBInterface.getResultSet("SELECT fromDate, toDate\n" +
                                                   "FROM order_room\n" +
                                                   "WHERE fromDate <= DATE('" + new java.sql.Date(c.getTimeInMillis()) + 
                                                   "') and toDate >= DATE('" + new java.sql.Date(c.getTimeInMillis()) +"')");
           while(rs.next()){               
               days++;
           }
           c.add(Calendar.DAY_OF_MONTH, 1);
       }
       
       int rooms = 0;
       ResultSet rs = DBInterface.getResultSet("SELECT count(*) as rooms\n" +
                                               "FROM room\n");
        if(rs.next()){ 
            rooms = rs.getInt("rooms");
       }
       BigDecimal bd = new BigDecimal((double) days*100 / (double) rooms * max).setScale(2, RoundingMode.HALF_EVEN);
       return bd.doubleValue();
    }
    
     /** 
     * liefert Auslastung in einem Monaten pro ein Zimmer zurück
     * @param c    
     * @param room    
     * @return boolean
     * @throws java.sql.SQLException
    */
    public static int getUsageOfRoomInMonth(Calendar c, String room) throws SQLException {
       int days = 0;
       c.set(Calendar.DAY_OF_MONTH, 1);
       int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
       for(int i = 0; i < max; i++){   
           ResultSet rs = DBInterface.getResultSet("SELECT EXISTS(SELECT * FROM order_room "
                                                   + "WHERE room = " + room +
                                                   " and fromDate <= DATE('" + new java.sql.Date(c.getTimeInMillis()) + 
                                                   "') and toDate >= DATE('" + new java.sql.Date(c.getTimeInMillis()) +"') LIMIT 1) as result");
           while(rs.next()){
               if (rs.getInt("result") == 1) days++;
           }
           c.add(Calendar.DAY_OF_MONTH, 1);
       }
       return days;
    }
    
     /** 
     * liefert Anzahl von Tagen in einem Jahr zurück   
     * @param year
     * @return boolean
    */
    public static int getDaysInYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();                                
        cal.set(Calendar.YEAR, year);  
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
    }
    
     /** 
     * liefert Auslastung in einem Jahr
     * @param year
     * @return boolean
     * @throws java.sql.SQLException
    */
    public static double getUsageInYear(int year) throws SQLException {
        int days = 0;
        ResultSet rs = DBInterface.getResultSet("SELECT fromDate, toDate\n" +
                                                "FROM order_room\n" +
                                                "WHERE YEAR(fromDate) =" + year + " or YEAR(toDate) = " + year);
        while(rs.next()){             
           Calendar c1 = Calendar.getInstance();
           c1.setTime(rs.getDate("fromDate"));
           Calendar c2 = Calendar.getInstance();
           c2.setTime(rs.getDate("toDate"));         
           if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)){
               Calendar clone = (Calendar) c1.clone();
               while (clone.before(c2)) {                   
                  clone.add(Calendar.DAY_OF_MONTH, 1);  
                  days++;  
               }         
           }else if (c1.get(Calendar.YEAR) == year) {
               Calendar clone = (Calendar) c1.clone();
               Calendar nextYear = Calendar.getInstance();
               nextYear.set((year + 1), 0, 1);
               while (clone.before(nextYear)) {  
                  clone.add(Calendar.DAY_OF_MONTH, 1);  
                  days++;  
               } 
           }else{
               Calendar thisYear = Calendar.getInstance();
               thisYear.set(year, 0, 1);
               while (thisYear.before(c2)) {  
                  thisYear.add(Calendar.DAY_OF_MONTH, 1);  
                  days++;  
               }
           }
        }
        
        int rooms = 0;
        rs = DBInterface.getResultSet("SELECT count(*) as rooms\n" +
                                      "FROM room\n");
        if(rs.next()){ 
            rooms = rs.getInt("rooms");
        }
        
        return (days * 100 / (rooms * getDaysInYear(year)));
    } 
}
