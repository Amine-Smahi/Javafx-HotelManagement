package hotelverwaltungfx;

import db.HelpFunctions;
import db.DBInterface;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * dienst zur Darstellung der Statistiken und Grafiken
 * @author Nosenko
 */
public class StatisticsController {

    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane mainGridPane;

    @FXML
    private ComboBox<String> switcher;

    @FXML
    private TableView<ObjectImpl> table;
    
    private LineChart<String,Number> lineChart;

    /**   
    * ActionEvent on switch ComboBox
    */
    @FXML
    void handleSwitcherChangeAction(ActionEvent event){
         if (table != null) table.getColumns().removeAll(table.getColumns());
         try {
            switch(switcher.getValue()){
                case("Statistics"):{
                    if (lineChart != null){
                        mainGridPane.getChildren().remove(lineChart);
                        lineChart = null;
                    } 
                    if (table != null){ 
                        mainGridPane.getChildren().remove(table); 
                        table = null;
                    }
                break;}                
                case("Age of guests per year"):{
                    createTable();
                        TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Year");
                        col1.setCellValueFactory(
                        new PropertyValueFactory<>("param1"));                 
                        TableColumn<ObjectImpl, String> col2 = new TableColumn<>("Min");
                        col2.setCellValueFactory(
                        new PropertyValueFactory<>("param2"));
                        TableColumn<ObjectImpl, String> col3 = new TableColumn<>("Max");
                        col3.setCellValueFactory(
                        new PropertyValueFactory<>("param3"));
                        TableColumn<ObjectImpl, String> col4 = new TableColumn<>("Average");
                        col4.setCellValueFactory(
                        new PropertyValueFactory<>("param4"));
                        LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                        colList.add(col1);
                        colList.add(col2);
                        colList.add(col3);
                        colList.add(col4);
                        table.getColumns().addAll(colList);
                        col1.setPrefWidth(100);                        
                        col2.setPrefWidth(100);
                        col3.setPrefWidth(100);
                        col4.setPrefWidth(100);
                        ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                        table.setItems(row);

                        ResultSet rs = DBInterface.getFirstStatistic();
                        while(rs.next()){ 
                            row.add(new ObjectImpl(rs.getString("year"),rs.getString("max"),rs.getString("min"),rs.getString("avg"), ""));
                        }
                break;}
                case("Number of guests each province and abroad"):{
                    createTable();
                            TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Year");
                            col1.setCellValueFactory(
                            new PropertyValueFactory<>("param1"));                 
                            TableColumn<ObjectImpl, String> col2 = new TableColumn<>("State");
                            col2.setCellValueFactory(
                            new PropertyValueFactory<>("param2"));
                            TableColumn<ObjectImpl, String> col3 = new TableColumn<>("Together");
                            col3.setCellValueFactory(
                            new PropertyValueFactory<>("param3"));

                            LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                            colList.add(col1);
                            colList.add(col2);
                            colList.add(col3);

                            table.getColumns().addAll(colList);  
                            col1.setPrefWidth(100);                        
                            col2.setPrefWidth(100);
                            col3.setPrefWidth(100);

                            ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                            table.setItems(row);

                            ResultSet rs = DBInterface.getSecondStatistic();
                            while(rs.next()){ 
                                row.add(new ObjectImpl(rs.getString("year"),rs.getString("state"),rs.getString("together"), "", ""));
                            }
                break;}
                case("Usage: Total per year"):{   
                    createTable();
                            TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Year");
                            col1.setCellValueFactory(
                            new PropertyValueFactory<>("param1"));                 
                            TableColumn<ObjectImpl, String> col2 = new TableColumn<>("Days");
                            col2.setCellValueFactory(
                            new PropertyValueFactory<>("param2"));
                            TableColumn<ObjectImpl, String> col3 = new TableColumn<>("Usage [%]");
                            col3.setCellValueFactory(
                            new PropertyValueFactory<>("param3"));
                            
                            LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                            colList.add(col1);
                            colList.add(col2);
                            colList.add(col3);

                            table.getColumns().addAll(colList); 
                            col1.setPrefWidth(100);                        
                            col2.setPrefWidth(100);
                            col3.setPrefWidth(100);
                       
                            
                            ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                            table.setItems(row);
                            
                            ResultSet rs = DBInterface.getResultSet("SELECT YEAR(fromDate) as year\n" +
                                                                    "FROM order_room\n" +
                                                                    "GROUP BY YEAR(fromDate)");
                            while(rs.next()){                   
                                row.add(new ObjectImpl(rs.getString("year"), 
                                        String.valueOf(HelpFunctions.getDaysInYear(rs.getInt("year"))),
                                        String.valueOf(HelpFunctions.getUsageInYear(rs.getInt("year"))), "", ""));
                            }
                break;}    
                case("Usage: Per room, year and month"):{
                    createTable();
                            TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Year");
                            col1.setCellValueFactory(
                            new PropertyValueFactory<>("param1"));                 
                            TableColumn<ObjectImpl, String> col2 = new TableColumn<>("Month");
                            col2.setCellValueFactory(
                            new PropertyValueFactory<>("param2"));
                            TableColumn<ObjectImpl, String> col3 = new TableColumn<>("Room");
                            col3.setCellValueFactory(
                            new PropertyValueFactory<>("param3"));
                            TableColumn<ObjectImpl, String> col4 = new TableColumn<>("Days");
                            col4.setCellValueFactory(
                            new PropertyValueFactory<>("param4"));
                            TableColumn<ObjectImpl, String> col5 = new TableColumn<>("Usage [%]");
                            col5.setCellValueFactory(
                            new PropertyValueFactory<>("param5"));
                            
                            LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                            colList.add(col1);
                            colList.add(col2);
                            colList.add(col3);
                            colList.add(col4);
                            colList.add(col5);
                            table.getColumns().addAll(colList);
                            col1.setPrefWidth(100);                        
                            col2.setPrefWidth(100);
                            col3.setPrefWidth(100);
                            col4.setPrefWidth(100);
                            col5.setPrefWidth(100);
                            
                            ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                            table.setItems(row);
                            
                            ResultSet rs = DBInterface.getResultSet("select Year(fromDate), room, room.description, fromDate, toDate\n" +
                                                                    "from order_room, room "
                                                                    + "where order_room.room = room.id");
                            while(rs.next()){ 
                                LinkedList<Calendar> l = HelpFunctions.getMonthFromRange(rs.getDate("fromDate"), rs.getDate("toDate"));
                                for (Calendar c : l) {
                                    int days = HelpFunctions.getUsageOfRoomInMonth((Calendar)c.clone(), rs.getString("room"));
                                    BigDecimal bd = new BigDecimal((double) days*100 / (double) c.getActualMaximum(Calendar.DAY_OF_MONTH)).setScale(2, RoundingMode.HALF_EVEN);
                                    row.add(new ObjectImpl(String.valueOf(c.get(Calendar.YEAR)),  
                                    new SimpleDateFormat("MMMM").format(c.getTime()),
                                    rs.getString("room.description"), String.valueOf(days), String.valueOf(bd.doubleValue())));
                                }
                            }
                break;}
                case("Usage: Per Month and Year (Chart)"):{
                            int x = 0;
                            final CategoryAxis xAxis = new CategoryAxis();
                            final NumberAxis yAxis = new NumberAxis(0, 100, 20);
                            lineChart = new LineChart<>(xAxis,yAxis);  
                            lineChart.getStylesheets().add("hotelverwaltungfx/chart.css"); 
                            lineChart.setLegendVisible(true);
                           
                            ResultSet rs = DBInterface.getResultSet("select YEAR(fromDate) as year\n" +
                                                                    "from order_room\n" +
                                                                    "\n" +
                                                                    "UNION \n" +
                                                                    "\n" +
                                                                    "select YEAR(toDate) as year\n" +
                                                                    "from order_room");
                            LinkedList<String> l = new LinkedList<>();
                            l.add("Jan");
                            l.add("Feb");
                            l.add("Mar");
                            l.add("Apr");
                            l.add("May");
                            l.add("Jun");
                            l.add("Jul");
                            l.add("Aug");
                            l.add("Sep");
                            l.add("Oct");
                            l.add("Nov");
                            l.add("Dec");
                            while(rs.next()){
                                XYChart.Series<String, Number> series = new XYChart.Series<>();
                                series.setName(rs.getString("year"));
                                for (int i = 0; i < 12; i++){
                                    Calendar c = Calendar.getInstance();
                                    c.set(rs.getInt("year"), i, 1);
                                    series.getData().add(new XYChart.Data<>(l.get(i), HelpFunctions.getUsageInMonth(c)));
                                }
                                lineChart.getData().add(series);
                            }                            
                            createChart();
                break;}    
                case("Usage: Per Month and Year"):{ 
                    createTable();
                            TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Year");
                            col1.setCellValueFactory(
                            new PropertyValueFactory<>("param1"));                 
                            TableColumn<ObjectImpl, String> col2 = new TableColumn<>("Month");
                            col2.setCellValueFactory(
                            new PropertyValueFactory<>("param2"));
                            TableColumn<ObjectImpl, String> col3 = new TableColumn<>("Usage [%]");
                            col3.setCellValueFactory(
                            new PropertyValueFactory<>("param3"));
                            
                            LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                            colList.add(col1);
                            colList.add(col2);
                            colList.add(col3);                           
                            table.getColumns().addAll(colList); 
                            col1.setPrefWidth(100);                        
                            col2.setPrefWidth(100);
                            col3.setPrefWidth(100);                            
                            
                            ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                            table.setItems(row);
                            
                            ResultSet rs = DBInterface.getResultSet("select YEAR(fromDate) as year\n" +
                                                                    "from order_room\n" +
                                                                    "\n" +
                                                                    "UNION \n" +
                                                                    "\n" +
                                                                    "select YEAR(toDate) as year\n" +
                                                                    "from order_room");
                            while(rs.next()){ 
                                for (int i = 0; i < 12; i++){
                                    Calendar c = Calendar.getInstance();
                                    c.set(rs.getInt("year"), i, 1);
                                    row.add(new ObjectImpl(rs.getString("year"), new SimpleDateFormat("MMMM").format(c.getTime()), String.valueOf(HelpFunctions.getUsageInMonth(c)), "", ""));
                                }
                            }                    
                break;}
                case("Number of guests per year and days of bookings"):{
                    createTable();
                            TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Year");
                            col1.setCellValueFactory(
                            new PropertyValueFactory<>("param1"));                 
                            TableColumn<ObjectImpl, String> col2 = new TableColumn<>("Min");
                            col2.setCellValueFactory(
                            new PropertyValueFactory<>("param2"));
                            TableColumn<ObjectImpl, String> col3 = new TableColumn<>("Max");
                            col3.setCellValueFactory(
                            new PropertyValueFactory<>("param3"));
                            TableColumn<ObjectImpl, String> col4 = new TableColumn<>("Average");
                            col4.setCellValueFactory(
                            new PropertyValueFactory<>("param4"));
                            TableColumn<ObjectImpl, String> col5 = new TableColumn<>("Number of guests");
                            col5.setCellValueFactory(
                            new PropertyValueFactory<>("param5"));

                            LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                            colList.add(col1);
                            colList.add(col2);
                            colList.add(col3);
                            colList.add(col4);
                            colList.add(col5);
                            table.getColumns().addAll(colList); 
                            col1.setPrefWidth(100);                        
                            col2.setPrefWidth(100);
                            col3.setPrefWidth(100);
                            col4.setPrefWidth(100);
                            col5.setPrefWidth(150);

                            ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                            table.setItems(row);

                            ResultSet rs = DBInterface.getSixthStatistic();
                            while(rs.next()){ 
                                row.add(new ObjectImpl(rs.getString("year"),rs.getString("min"),rs.getString("max"), rs.getString("avg"), rs.getString("guest")));
                            }
                break;}
                case("Overview of all guests"):{
                    createTable();
                            TableColumn<ObjectImpl, String> col1 = new TableColumn<>("Guest");
                            col1.setCellValueFactory(
                            new PropertyValueFactory<>("param1")); 
                            TableColumn<ObjectImpl, String> col2 = new TableColumn<>("From");
                            col2.setCellValueFactory(
                            new PropertyValueFactory<>("param2"));
                            TableColumn<ObjectImpl, String> col3 = new TableColumn<>("To");
                            col3.setCellValueFactory(
                            new PropertyValueFactory<>("param3"));
                            TableColumn<ObjectImpl, String> col4 = new TableColumn<>("Days");
                            col4.setCellValueFactory(
                            new PropertyValueFactory<>("param4"));

                            LinkedList<TableColumn<ObjectImpl, String>> colList = new LinkedList<>();
                            colList.add(col1);
                            colList.add(col2);
                            colList.add(col3);
                            colList.add(col4);                            
                            table.getColumns().addAll(colList); 
                            col1.setPrefWidth(100);                        
                            col2.setPrefWidth(100);
                            col3.setPrefWidth(100);
                            col4.setPrefWidth(100);                            

                            ObservableList<ObjectImpl> row = FXCollections.observableArrayList();
                            table.setItems(row);

                            ResultSet rs = DBInterface.getSeventhStatistic();
                            while(rs.next()){ 
                                row.add(new ObjectImpl(rs.getString("pers"),rs.getString("fromDate"), rs.getString("toDate"), rs.getString("days"), ""));
                            }
                break;}
            }
         } catch (SQLException ex) {
             Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    /**
    * erzeugt eine Tabelle und setzt die in das Formular
    */
    private void createTable(){
        if (table == null){
            if (lineChart != null){
                mainGridPane.getChildren().remove(lineChart);
                lineChart = null;
            } 
            table = new TableView<>();
            table.getStylesheets().add("hotelverwaltungfx/table.css");   
            mainGridPane.add(table, 0, 0);
            GridPane.setMargin(table, new Insets(5,5,5,5));
            GridPane.setColumnSpan(table, 3);
        }
    }
    
    /**
    * erzeugt eine Grafik und setzt die in das Formular
    */
     private void createChart(){
            if (table != null){ 
                mainGridPane.getChildren().remove(table); 
                table = null;
            }
            mainGridPane.add(lineChart, 0, 0);
            lineChart.setMaxWidth(570);            
            GridPane.setMargin(lineChart, new Insets(5,5,5,5));
            GridPane.setColumnSpan(lineChart, 3);
    }

     /**
    * initialisiert und stellt die GUI-Komponenten ein.
    */
    @FXML
    void initialize() {
        assert mainGridPane != null : "fx:id=\"mainGridPane\" was not injected: check your FXML file 'Statistics.fxml'.";
        assert switcher != null : "fx:id=\"switcher\" was not injected: check your FXML file 'Statistics.fxml'.";
                 
        ObservableList<String> list = FXCollections.observableArrayList(); 
        switcher.setItems(list);        
        list.add("Age of guests per year");
        list.add("Number of guests each province and abroad");
        list.add("Usage: Total per year");
        list.add("Usage: Per Month and Year");
        list.add("Usage: Per Month and Year (Chart)");
        list.add("Usage: Per room, year and month");
        list.add("Number of guests per year and days of bookings");
        list.add("Overview of all guests");
        switcher.getSelectionModel().selectFirst();
    }

    /**
    * Klasse für Objekte, die die Einträge in eine Tabelle darstellen.
    */
    public static class ObjectImpl extends Object {

        private final SimpleStringProperty param1;
        private final SimpleStringProperty param2;
        private final SimpleStringProperty param3;
        private final SimpleStringProperty param4;
        private final SimpleStringProperty param5;

        public String getParam1() {
            return param1.get();
        }

        public String getParam2() {
            return param2.get();
        }

        public String getParam3() {
            return param3.get();
        }

        public String getParam4() {
            return param4.get();
        }

        public String getParam5() {
            return param5.get();
        }
        
        public ObjectImpl(String param1, String param2, String param3, String param4, String param5) {
            this.param1 = new SimpleStringProperty(param1);
            this.param2 = new SimpleStringProperty(param2);
            this.param3 = new SimpleStringProperty(param3);
            this.param4 = new SimpleStringProperty(param4);
            this.param5 = new SimpleStringProperty(param5);
        }
    }
}

