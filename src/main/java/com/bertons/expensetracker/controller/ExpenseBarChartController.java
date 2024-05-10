package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseBarChartController {
    @FXML
    private ComboBox<Integer> yearsComboBox;
    @FXML
    private AreaChart<String, Number> areaChart;

    public void initBarCharts(ObservableList<Expense> expenses) {
        initializeComboBox(expenses);
        expenses = expenses.stream().filter(expense -> expense.getDate().getYear() == yearsComboBox.getValue()).collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));

        final ObservableList<String> months = FXCollections.observableArrayList("Jan", "Feb");
        CategoryAxis xAxis = new CategoryAxis(months);
        xAxis.setLabel("Months");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Monthly amounts");
        areaChart = new AreaChart<>(xAxis, yAxis);

    }

    private void initializeComboBox(ObservableList<Expense> expenses) {
        yearsComboBox.getItems().removeAll();
        ObservableList<Integer> comboBoxChoices = expenses.stream().map(e -> e.getDate().getYear()).distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));;
        yearsComboBox.setItems(comboBoxChoices);
        yearsComboBox.getSelectionModel().selectFirst();
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

    public void OnYearsComboBoxSelectYear(ActionEvent actionEvent) {
    }
}
