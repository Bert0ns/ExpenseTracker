package com.bertons.expensetracker.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.io.Closeable;

public abstract class ChartController implements DataToCharts, Closeable {

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
