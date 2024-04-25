package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.dao.ExpenseRepository;
import com.bertons.expensetracker.persistence.model.Expense;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LongStringConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public class MainPageViewController {
    @FXML
    private TableView<Expense> tableViewExpenses;
    @FXML
    private TableColumn<Expense, Long> tableColumnExpenseId;
    @FXML
    private TableColumn<Expense, Double> tableColumnAmount;
    @FXML
    private TableColumn<Expense, LocalDate> tableColumnDate;
    @FXML
    private TableColumn<Expense, String> tableColumnDescription;
    @FXML
    private TableColumn<Expense, Expense.ExpenseType> tableColumnExpenseType;
    @FXML
    private TableColumn<Expense, Expense.PayingMethod> tableColumnPayingMethod;

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ExpenseRepository expenseRepository;

    public void initDataSource(HikariDataSource hikariDataSource) {
        this.expenseRepository = new ExpenseRepository(hikariDataSource);

        Iterable<Expense> expensesFound = this.expenseRepository.findAll();
        expenses.addAll(StreamSupport.stream(expensesFound.spliterator(), false).toList());
        //expenses.addAll(StreamSupport.stream(this.expenseRepository.findAll().spliterator(), false).toList());
    }

    public void OnMenuFileCloseButton_Click(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void initialize() {
        initializeTableViewExpense();
    }

    private void initializeTableViewExpense() {
        System.out.println("Initializing table view expense");
        tableViewExpenses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        initializeTableColumnId();
        initializeTableColumnAmount();
        initializeTableColumnDate();
        initializeTableColumnDescription();
        initializeTableColumnExpenseType();
        initializeTableColumnPayingMethod();


        FilteredList<Expense> filteredList = new FilteredList<>(expenses, expense -> true);
        SortedList<Expense> sortedList = new SortedList<>(filteredList.sorted(Comparator.comparing(Expense::getDate)));
        sortedList.comparatorProperty().bind(tableViewExpenses.comparatorProperty());
        tableViewExpenses.setItems(sortedList);
    }

    private void initializeTableColumnId() {
        //tableColumnExpenseId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnExpenseId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        tableColumnExpenseId.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
    }
    private void initializeTableColumnAmount() {
        //tableColumnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableColumnAmount.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));
        tableColumnAmount.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tableColumnAmount.setOnEditCommit(event -> {
            Expense selectedExpense = event.getRowValue();
            selectedExpense.setAmount(event.getNewValue());
            expenseRepository.save(selectedExpense);
        });
    }
    private void initializeTableColumnDate() {
        //tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));
        tableColumnDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        tableColumnDate.setOnEditCommit(e ->{
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setDate(e.getNewValue());
            expenseRepository.save(selectedExpense);
        });
    }
    private void initializeTableColumnDescription() {
        //tableColumnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableColumnDescription.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDescription()));
        tableColumnDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnDescription.setOnEditCommit(e ->{
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setDescription(e.getNewValue());
            expenseRepository.save(selectedExpense);
        });
    }
    private void initializeTableColumnExpenseType(){
        //tableColumnExpenseType.setCellValueFactory(new PropertyValueFactory<>("expenseType"));
        tableColumnExpenseType.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getExpenseType()));
        tableColumnExpenseType.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Expense.ExpenseType expenseType) {
                return Expense.getStringFromExpenseType(expenseType);
            }

            @Override
            public Expense.ExpenseType fromString(String s) {
                return Expense.getExpenseTypeFromString(s);
            }
        }));
        tableColumnExpenseType.setOnEditCommit(e -> {
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setExpenseType(e.getNewValue());
            expenseRepository.save(selectedExpense);
        });
    }
    private void initializeTableColumnPayingMethod(){
        //tableColumnPayingMethod.setCellValueFactory(new PropertyValueFactory<>("payingMethod"));
        tableColumnPayingMethod.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPayingMethod()));
        tableColumnPayingMethod.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Expense.PayingMethod expensePayingMethod) {
                return Expense.getStringFromPayingMethod(expensePayingMethod);
            }

            @Override
            public Expense.PayingMethod fromString(String s) {
                return Expense.getPayingMethodFromString(s);
            }
        }));
        tableColumnPayingMethod.setOnEditCommit(e ->{
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setPayingMethod(e.getNewValue());
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

    public void OnInsertExpenseButton_Click(ActionEvent actionEvent) throws IOException {
        new AddExpenseDialog().showAndWait().ifPresent(expense -> {
            try {
                Expense saved = expenseRepository.save(expense);
                expenses.add(saved);
                System.out.println(saved);
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }
}
