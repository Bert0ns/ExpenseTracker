package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;

public class ExpensePieChartController {
    @FXML
    private PieChart expensePieChart;

    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    private void initPieChartData() {
        pieChartData.clear();
        pieChartData.add(new PieChart.Data(Expense.ExpenseType.Miscellaneous.toString(), 0));
        pieChartData.add(new PieChart.Data(Expense.ExpenseType.Car.toString(), 0));
        pieChartData.add(new PieChart.Data(Expense.ExpenseType.Debt.toString(), 0));
        pieChartData.add(new PieChart.Data(Expense.ExpenseType.EatingOut.toString(), 0));
        pieChartData.add(new PieChart.Data(Expense.ExpenseType.Groceries.toString(), 0));
        pieChartData.add(new PieChart.Data(Expense.ExpenseType.Subscription.toString(), 0));
    }

    public void initPieChart(ObservableList<Expense> expensesData)
    {
        initPieChartData();
        for (Expense expense : expensesData) {
            switch (expense.getExpenseType()) {
                case Miscellaneous:
                    pieChartData.get(0).setPieValue(pieChartData.get(0).getPieValue() + expense.getAmount());
                    break;
                case Car:
                    pieChartData.get(1).setPieValue(pieChartData.get(1).getPieValue() + expense.getAmount());
                    break;
                case Debt:
                    pieChartData.get(2).setPieValue(pieChartData.get(2).getPieValue() + expense.getAmount());
                    break;
                case EatingOut:
                    pieChartData.get(3).setPieValue(pieChartData.get(3).getPieValue() + expense.getAmount());
                    break;
                case Groceries:
                    pieChartData.get(4).setPieValue(pieChartData.get(4).getPieValue() + expense.getAmount());
                    break;
                case Subscription:
                    pieChartData.get(5).setPieValue(pieChartData.get(5).getPieValue() + expense.getAmount());
                    break;
            }
        }
        expensePieChart.getData().setAll(pieChartData);
    }

    public void OnMenuFileCloseButton_Click(ActionEvent actionEvent) {

    }

    public void OnMenuHelpAboutButton_Click(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Expense Tracker");
        alert.setContentText("""
                Author:
                Davide Bertoni
                
                This is the pie chart visualizing the expenses
                
                version 0.1
                """);
        alert.showAndWait();
    }
}
