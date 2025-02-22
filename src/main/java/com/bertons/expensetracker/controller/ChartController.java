package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.io.Closeable;

public abstract class ChartController implements DataToCharts, Closeable, ObserverExpense {

    public void showAboutInformation(String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Expense Tracker");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void close() {}
}
