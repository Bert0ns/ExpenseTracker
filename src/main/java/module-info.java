module com.bertons.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires org.slf4j;


    opens com.bertons.expensetracker to javafx.fxml;
    exports com.bertons.expensetracker;
    exports com.bertons.expensetracker.controller;
    opens com.bertons.expensetracker.controller to javafx.fxml;
}