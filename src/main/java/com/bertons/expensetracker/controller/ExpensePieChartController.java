package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;

public class ExpensePieChartController {
    @FXML
    private PieChart payingMethodsPieChart;
    @FXML
    private PieChart expenseTypesPieChart;

    ObservableList<PieChart.Data> expenseTypesPieChartData = FXCollections.observableArrayList();
    ObservableList<PieChart.Data> payingMethodsPieChartData = FXCollections.observableArrayList();

    public void initPieCharts(ObservableList<Expense> expensesData) {
        initExpenseTypesPieChartData();
        insertDataIntoExpenseTypesPieChartData(expensesData);
        expenseTypesPieChart.getData().setAll(expenseTypesPieChartData);

        initPayingMethodsPieChartData();
        insertDataIntoPayingMethodsPieChartData(expensesData);
        payingMethodsPieChart.getData().setAll(payingMethodsPieChartData);
    }

    public void updateExpenseTypesPieChartData(ObservableList<Expense> expensesData) {
        expenseTypesPieChartData.get(0).setPieValue(0);
        expenseTypesPieChartData.get(1).setPieValue(0);
        expenseTypesPieChartData.get(2).setPieValue(0);
        expenseTypesPieChartData.get(3).setPieValue(0);
        expenseTypesPieChartData.get(4).setPieValue(0);
        expenseTypesPieChartData.get(5).setPieValue(0);
        insertDataIntoExpenseTypesPieChartData(expensesData);
    }
    public void updatePayingMethodPieChartData(ObservableList<Expense> expensesData) {
        payingMethodsPieChartData.get(0).setPieValue(0);
        payingMethodsPieChartData.get(1).setPieValue(0);
        insertDataIntoPayingMethodsPieChartData(expensesData);
    }

    private void initExpenseTypesPieChartData() {
        expenseTypesPieChartData.clear();
        expenseTypesPieChartData.add(new PieChart.Data(Expense.ExpenseType.Miscellaneous.toString(), 0));
        expenseTypesPieChartData.add(new PieChart.Data(Expense.ExpenseType.Car.toString(), 0));
        expenseTypesPieChartData.add(new PieChart.Data(Expense.ExpenseType.Debt.toString(), 0));
        expenseTypesPieChartData.add(new PieChart.Data(Expense.ExpenseType.EatingOut.toString(), 0));
        expenseTypesPieChartData.add(new PieChart.Data(Expense.ExpenseType.Groceries.toString(), 0));
        expenseTypesPieChartData.add(new PieChart.Data(Expense.ExpenseType.Subscription.toString(), 0));
    }
    private void initPayingMethodsPieChartData() {
        payingMethodsPieChartData.clear();
        payingMethodsPieChartData.add(new PieChart.Data(Expense.PayingMethod.Cash.toString(), 0));
        payingMethodsPieChartData.add(new PieChart.Data(Expense.PayingMethod.Card.toString(), 0));
    }

    private void insertDataIntoExpenseTypesPieChartData(ObservableList<Expense> expensesData) {
        for (Expense expense : expensesData) {
            switch (expense.getExpenseType()) {
                case Miscellaneous:
                    expenseTypesPieChartData.get(0).setPieValue(expenseTypesPieChartData.get(0).getPieValue() + expense.getAmount());
                    break;
                case Car:
                    expenseTypesPieChartData.get(1).setPieValue(expenseTypesPieChartData.get(1).getPieValue() + expense.getAmount());
                    break;
                case Debt:
                    expenseTypesPieChartData.get(2).setPieValue(expenseTypesPieChartData.get(2).getPieValue() + expense.getAmount());
                    break;
                case EatingOut:
                    expenseTypesPieChartData.get(3).setPieValue(expenseTypesPieChartData.get(3).getPieValue() + expense.getAmount());
                    break;
                case Groceries:
                    expenseTypesPieChartData.get(4).setPieValue(expenseTypesPieChartData.get(4).getPieValue() + expense.getAmount());
                    break;
                case Subscription:
                    expenseTypesPieChartData.get(5).setPieValue(expenseTypesPieChartData.get(5).getPieValue() + expense.getAmount());
                    break;
            }
        }
    }
    private void insertDataIntoPayingMethodsPieChartData(ObservableList<Expense> expensesData) {
        for (Expense expense : expensesData) {
            switch (expense.getPayingMethod()) {
                case Cash -> payingMethodsPieChartData.get(0).setPieValue(payingMethodsPieChartData.get(0).getPieValue() + expense.getAmount());
                case Card -> payingMethodsPieChartData.get(1).setPieValue(payingMethodsPieChartData.get(1).getPieValue() + expense.getAmount());
            }
        }
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
