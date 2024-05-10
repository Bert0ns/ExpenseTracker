package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;

public class ExpenseBarChartController {

    @FXML
    private AreaChart areaChart;

    public void initBarCharts(ObservableList<Expense> expenses) {

    }

    public void OnMenuFileCloseButton_Click(ActionEvent actionEvent) {
        areaChart.getScene().getWindow().hide();
    }

    public void OnMenuHelpAboutButton_Click(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Expense Tracker");
        alert.setContentText("""
                Author:
                Davide Bertoni
                
                This is the area chart visualizing the expenses through the year
                """);
        alert.showAndWait();
    }
}
