package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.time.Month;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpenseBarChartController {
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ComboBox<Integer> yearsComboBox;
    @FXML
    private AreaChart<String, Number> areaChart;

    private MainPageViewController mainPageViewController;
    private XYChart.Series<String, Number> series = new XYChart.Series<>();

    public void initBarCharts(ObservableList<Expense> expenses, MainPageViewController mainPageViewController) {
        this.mainPageViewController = mainPageViewController;
        initializeYearsComboBox(expenses);

        final ObservableList<String> months = FXCollections.observableArrayList(Month.JANUARY.toString(), Month.FEBRUARY.toString(), Month.MARCH.toString(), Month.APRIL.toString(), Month.MAY.toString(), Month.JUNE.toString(), Month.JULY.toString(), Month.AUGUST.toString(), Month.SEPTEMBER.toString(), Month.OCTOBER.toString(), Month.NOVEMBER.toString(), Month.DECEMBER.toString());
        xAxis.setCategories(months);
        yAxis.setLabel("Monthly Amount");

        initializeDataSeries(series);
        updateAreaChart(expenses);
    }

    public void updateAreaChart(ObservableList<Expense> expenses) {
        updateYearsComboBox(expenses);
        resetSeries(series);

        if(yearsComboBox.getItems().isEmpty())
        {
            System.out.println("Empty list combobox");
            return;
        }

        if(yearsComboBox.getSelectionModel().getSelectedItem() == null) {
            yearsComboBox.getSelectionModel().selectFirst();
        }

        expenses.stream().filter(expense -> expense.getDate().getYear() == yearsComboBox.getValue()).forEach(expense -> {
            series.getData().forEach(data -> {
                if(Objects.equals(data.getXValue(), expense.getDate().getMonth().toString()))
                {
                    data.setYValue(data.getYValue().doubleValue() + expense.getAmount());
                }
            });
        });
        areaChart.getData().clear();
        areaChart.getData().add(series);
    }

    private void resetSeries(XYChart.Series<String, Number> series) {
        series.getData().forEach(data -> data.setYValue(0));
    }
    private void initializeDataSeries(XYChart.Series<String, Number> series) {
        series.getData().add(new XYChart.Data<>(Month.JANUARY.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.FEBRUARY.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.MARCH.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.APRIL.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.MAY.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.JUNE.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.JULY.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.AUGUST.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.SEPTEMBER.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.OCTOBER.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.NOVEMBER.toString(), 0));
        series.getData().add(new XYChart.Data<>(Month.DECEMBER.toString(), 0));
    }
    private void initializeYearsComboBox(ObservableList<Expense> expenses) {
        yearsComboBox.getItems().removeAll();
        ObservableList<Integer> comboBoxChoices = expenses.stream().map(e -> e.getDate().getYear()).distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));;
        yearsComboBox.setItems(comboBoxChoices);
        yearsComboBox.getSelectionModel().selectFirst();
    }
    private void updateYearsComboBox(ObservableList<Expense> expenses) {
        /*
        yearsComboBox.setItems(expenses.stream().map(e -> e.getDate().getYear()).distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList)));

        if(yearsComboBox.getItems().isEmpty())
        {
            yearsComboBox.setItems(expenses.stream().map(e -> e.getDate().getYear()).distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList)));
            yearsComboBox.getSelectionModel().selectFirst();
            return;
        }
        try {
            int years = yearsComboBox.getSelectionModel().getSelectedItem();
            yearsComboBox.setItems(expenses.stream().map(e -> e.getDate().getYear()).distinct().collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList)));
            yearsComboBox.getSelectionModel().select(years);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
         */
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
       // updateAreaChart();
        mainPageViewController.updateBarChart();
    }
}
