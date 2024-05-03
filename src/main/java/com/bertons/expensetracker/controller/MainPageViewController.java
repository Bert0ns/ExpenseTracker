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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LongStringConverter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    private ExpensePieChartController expensePieChartController;

    public void initDataSource(HikariDataSource hikariDataSource) {
        this.expenseRepository = new ExpenseRepository(hikariDataSource);
        updateExpenses();
    }

    public void OnMenuFileCloseButton_Click(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void initialize() {
        initializeTableViewExpense();
    }

    private void updateExpenses() {
        try {
            Iterable<Expense> expensesFound = this.expenseRepository.findAll();
            expenses.clear();
            expenses.addAll(StreamSupport.stream(expensesFound.spliterator(), false).toList());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
        }
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
        tableColumnExpenseId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        tableColumnExpenseId.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
    }
    private void initializeTableColumnAmount() {
        tableColumnAmount.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAmount()));
        tableColumnAmount.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
            @Override
            public Double fromString(String string) {
                try{
                    return Double.parseDouble(string);
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Input amount: " + string + "\nIs NOT a number, try changing \",\" with \".\"", ButtonType.OK).showAndWait();
                    return null;
                }
            }
        }));
        tableColumnAmount.setOnEditCommit(event -> {
            Expense selectedExpense = event.getRowValue();
            if(event.getNewValue() != null)
            {
                selectedExpense.setAmount(event.getNewValue());
                expenseRepository.save(selectedExpense);
                updatePieChart();
            }
            else
            {
                updateExpenses();
            }
        });
    }
    private void initializeTableColumnDate() {
        tableColumnDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));
        tableColumnDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String string) {
                try{
                    return LocalDate.parse(string);
                }
                catch (Exception e)
                {
                    new Alert(Alert.AlertType.ERROR, "Input Date: " + string + "\nIs NOT a date", ButtonType.OK).showAndWait();
                    return null;
                }
            }
        }));
        tableColumnDate.setOnEditCommit(e ->{
            Expense selectedExpense = e.getRowValue();
            if(e.getNewValue() != null)
            {
                selectedExpense.setDate(e.getNewValue());
                expenseRepository.save(selectedExpense);
            }
            else
            {
                updateExpenses();
            }
        });
    }
    private void initializeTableColumnDescription() {
        tableColumnDescription.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDescription()));
        tableColumnDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnDescription.setOnEditCommit(e ->{
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setDescription(e.getNewValue());
            expenseRepository.save(selectedExpense);
        });
    }
    private void initializeTableColumnExpenseType(){
        tableColumnExpenseType.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getExpenseType()));
        tableColumnExpenseType.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Expense.ExpenseType expenseType) {
                return expenseType.toString();
            }

            @Override
            public Expense.ExpenseType fromString(String s) {
                System.out.println(s);
                s = StringUtils.capitalize(s);
                System.out.println(s);
                Expense.ExpenseType expenseType = Expense.getExpenseTypeFromString(s);
                if (expenseType == null) {
                    new Alert(Alert.AlertType.ERROR, "Input string: " + s + "\nIs NOT an Expense Type.\nTry with: " + Expense.getAllExpenseTypes(), ButtonType.OK).showAndWait();
                    return Expense.ExpenseType.Miscellaneous;
                }
                return expenseType;
            }
        }));
        tableColumnExpenseType.setOnEditCommit(e -> {
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setExpenseType(e.getNewValue());
            expenseRepository.save(selectedExpense);
            updatePieChart();
        });
    }
    private void initializeTableColumnPayingMethod(){
        tableColumnPayingMethod.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPayingMethod()));
        tableColumnPayingMethod.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Expense.PayingMethod expensePayingMethod) {
                return expensePayingMethod.toString();
            }

            @Override
            public Expense.PayingMethod fromString(String s) {
                StringUtils.capitalize(s);
                Expense.PayingMethod payingMethod = Expense.getPayingMethodFromString(s);
                if (payingMethod == null) {
                    new Alert(Alert.AlertType.ERROR, "Input string: " + s + "\nIs NOT a Paying Method.\nTry with: " + Expense.getAllPayingMethods(), ButtonType.OK).showAndWait();
                    return Expense.PayingMethod.Cash;
                }
                return Expense.getPayingMethodFromString(s);
            }
        }));
        tableColumnPayingMethod.setOnEditCommit(e ->{
            Expense selectedExpense = e.getRowValue();
            selectedExpense.setPayingMethod(e.getNewValue());
            expenseRepository.save(selectedExpense);
            updatePieChart();
        });
    }

    private void updatePieChart() {
        if(Objects.isNull(expensePieChartController))
        {
            return;
        }

        updateExpenses();
        expensePieChartController.updateExpenseTypesPieChartData(expenses);
        expensePieChartController.updatePayingMethodPieChartData(expenses);
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
                for (Expense expense : tmp) {
                    expenseRepository.save(expense);
                }
                updateExpenses();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
        updatePieChart();
    }

    public void OnMenuFileExportButton_Click(ActionEvent actionEvent) {
        System.out.println("Exporting expenses from table");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if (Objects.nonNull(file)) {
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

                updatePieChart();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    public void OnDeleteExpenseButton_Click(ActionEvent actionEvent) {
        Expense selectedItem = tableViewExpenses.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                expenseRepository.deleteById(selectedItem.getId());
                expenses.remove(selectedItem);
                updatePieChart();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }

    public void OnMenuViewPieChartButton_Click(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("expense-pie-chart-view.fxml"));
        Parent root = loader.load();
        expensePieChartController = loader.getController();

        updateExpenses();
        expensePieChartController.initPieCharts(expenses);

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Expense Pie Chart");
        stage.setResizable(true);
        stage.show();
    }

    public void OnMenuViewBarChartButton_Click(ActionEvent actionEvent) {

    }
}
