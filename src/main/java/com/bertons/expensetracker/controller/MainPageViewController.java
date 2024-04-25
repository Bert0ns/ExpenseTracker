package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.dao.ExpenseRepository;
import com.bertons.expensetracker.persistence.model.Expense;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class MainPageViewController {
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();

    private ExpenseRepository expenseRepository;
    @FXML
    private TableView<Expense> tableViewExpense;

    public void initDataSource(HikariDataSource hikariDataSource) {
        this.expenseRepository = new ExpenseRepository(hikariDataSource);

        Iterable<Expense> expensesFound = this.expenseRepository.findAll();
        expenses.addAll(StreamSupport.stream(expensesFound.spliterator(), false).toList());
    }

    public void OnMenuFileCloseButton_Click(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void initialize() {
        initializeTableViewExpense();
    }

    private void initializeTableViewExpense() {
        System.out.println("Initializing table view expense");
        List<TableColumn<Expense, ?>> cols = tableViewExpense.getColumns();

        //column "id"
        //do nothing

        //column "amount"
        //cols.get(1).setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        cols.get(1).setOnEditCommit(event -> {
            Expense selectedExpense = event.getRowValue();
            selectedExpense.setAmount((Double) event.getNewValue());
            expenseRepository.save(selectedExpense);
        });

        //column "Date"
        cols.get(2).setOnEditCommit(event -> {
            Expense selectedExpense = event.getRowValue();
            selectedExpense.setDescription((String) event.getNewValue());
            expenseRepository.save(selectedExpense);
        });
    }

    public void OnMenuHelpAboutButton_Click(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Expense Tracker");
        alert.setContentText("""
                Author:
                Davide Bertoni
                    
                I made this project for the exam
                    
                version 0.2
                """);
        alert.showAndWait();
    }

    public void OnMenuFileImportButton_Click(ActionEvent actionEvent) {
        System.out.println("Importing expenses from file");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            try {
                List<Expense> tmp = mapper.readValue(file, new TypeReference<>() {
                });
                for (Expense plane : tmp) {
                    Expense saved = expenseRepository.save(plane);
                    expenses.add(saved);
                }
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }

    public void OnMenuFileExportButton_Click(ActionEvent actionEvent) {
        System.out.println("Exporting expenses from table");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, expenses);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }
}
