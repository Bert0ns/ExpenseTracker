package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.dao.ExpenseRepository;
import com.bertons.expensetracker.persistence.dao.Repository;
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
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LongStringConverter;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class MainPageViewController implements ViewController, ObservableExpense {
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
    private Repository<Expense, Long> expenseRepository;

    private ChartController expensePieChartController;
    private ChartController expenseAreaChartController;
    private final List<ObserverExpense> observers = new ArrayList<ObserverExpense>();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String INVALID_AMOUNT_MESSAGE = "Input amount: %s\nIs NOT a number, try changing \",\" with \".\"";
    private static final String INVALID_DATE_MESSAGE = "Input Date: %s\nIs NOT a date";
    private static final String INVALID_EXPENSE_TYPE_MESSAGE = "Input string: %s\nIs NOT an Expense Type.\nTry with: %s";
    private static final String INVALID_PAYING_METHOD_MESSAGE = "Input string: %s\nIs NOT a Paying Method.\nTry with: %s";

    private void initializeTableViewExpense() {
        System.out.println("Initializing table view expense");
        tableViewExpenses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        setupColumn(tableColumnExpenseId, Expense::getId, new LongStringConverter(), null);
        setupColumn(tableColumnAmount, Expense::getAmount, createDoubleConverter(), this::handleAmountEdit);
        setupColumn(tableColumnDate, Expense::getDate, createDateConverter(), this::handleDateEdit);
        setupColumn(tableColumnDescription, Expense::getDescription, new DefaultStringConverter(), this::handleDescriptionEdit);
        setupColumn(tableColumnExpenseType, Expense::getExpenseType, createExpenseTypeConverter(), this::handleExpenseTypeEdit);
        setupColumn(tableColumnPayingMethod, Expense::getPayingMethod, createPayingMethodConverter(), this::handlePayingMethodEdit);

        setupTableSorting();
    }
    private <T> void setupColumn(TableColumn<Expense, T> column, Function<Expense, T> propertyGetter, StringConverter<T> converter, Consumer<TableColumn.CellEditEvent<Expense, T>> editHandler) {
        column.setCellValueFactory(data -> new SimpleObjectProperty<>(propertyGetter.apply(data.getValue())));
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        if (editHandler != null) {
            column.setOnEditCommit(editHandler::accept);
        }
    }
    private void setupTableSorting() {
        FilteredList<Expense> filteredList = new FilteredList<>(expenses, expense -> true);
        SortedList<Expense> sortedList = new SortedList<>(filteredList.sorted(Comparator.comparing(Expense::getDate).reversed()));
        sortedList.comparatorProperty().bind(tableViewExpenses.comparatorProperty());
        tableViewExpenses.setItems(sortedList);
    }
    private StringConverter<LocalDate> createDateConverter() {
        return new LocalDateStringConverter(DATE_FORMATTER, DATE_FORMATTER) {
            @Override
            public LocalDate fromString(String string) {
                try {
                    return LocalDate.parse(string, DATE_FORMATTER);
                } catch (Exception e) {
                    showAlert(String.format(INVALID_DATE_MESSAGE, string));
                    return null;
                }
            }
        };
    }
    private StringConverter<Double> createDoubleConverter() {
        return new DoubleStringConverter() {
            @Override
            public Double fromString(String string) {
                try{
                    return Double.parseDouble(string);
                } catch (Exception e) {
                    showAlert(String.format(INVALID_AMOUNT_MESSAGE, string));
                    return null;
                }
            }
        };
    }
    private StringConverter<Expense.ExpenseType> createExpenseTypeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Expense.ExpenseType type) {
                return type.toString();
            }

            @Override
            public Expense.ExpenseType fromString(String str) {
                str = StringUtils.capitalize(str);
                Expense.ExpenseType type = Expense.getExpenseTypeFromString(str);
                if (type == null) {
                    showAlert(String.format(INVALID_EXPENSE_TYPE_MESSAGE, str, Expense.getAllExpenseTypes()));
                    return Expense.ExpenseType.Miscellaneous;
                }
                return type;
            }
        };
    }
    private StringConverter<Expense.PayingMethod> createPayingMethodConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Expense.PayingMethod method) {
                return method.toString();
            }

            @Override
            public Expense.PayingMethod fromString(String str) {
                str = StringUtils.capitalize(str);
                Expense.PayingMethod method = Expense.getPayingMethodFromString(str);
                if (method == null) {
                    showAlert(String.format(INVALID_PAYING_METHOD_MESSAGE, str, Expense.getAllPayingMethods()));
                    return Expense.PayingMethod.Cash;
                }
                return method;
            }
        };
    }
    private void handleAmountEdit(TableColumn.CellEditEvent<Expense, Double> event) {
        if (event.getNewValue() != null) {
            updateExpense(event.getRowValue(), expense -> expense.setAmount(event.getNewValue()));
        } else {
            updateExpenses();
        }
    }
    private void handleDateEdit(TableColumn.CellEditEvent<Expense, LocalDate> event) {
        if (event.getNewValue() != null) {
            updateExpense(event.getRowValue(), expense -> expense.setDate(event.getNewValue()));
        } else {
            updateExpenses();
        }
    }
    private void handleDescriptionEdit(TableColumn.CellEditEvent<Expense, String> event) {
        updateExpense(event.getRowValue(), expense -> expense.setDescription(event.getNewValue()));
    }
    private void handleExpenseTypeEdit(TableColumn.CellEditEvent<Expense, Expense.ExpenseType> event) {
        updateExpense(event.getRowValue(), expense -> expense.setExpenseType(event.getNewValue()));
    }
    private void handlePayingMethodEdit(TableColumn.CellEditEvent<Expense, Expense.PayingMethod> event) {
        updateExpense(event.getRowValue(), expense -> expense.setPayingMethod(event.getNewValue()));
    }
    private void updateExpense(Expense expense, Consumer<Expense> updateAction) {
        updateAction.accept(expense);
        expenseRepository.save(expense);
        updateExpenses();
    }
    private void showAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
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

    private void updateCharts()
    {
        notifyAllObservers(expenses);
    }

    private void closeChart(ChartController expensePieChartController) {
        if (Objects.nonNull(expensePieChartController)) {
            expensePieChartController.close();
            removeObserver(expensePieChartController);
        }
    }

    @FXML
    public void initialize() {
        initializeTableViewExpense();
    }

    @Override
    public void initDataSource(HikariDataSource hikariDataSource) {
        setRepository(new ExpenseRepository(hikariDataSource));
        updateExpenses();
    }

    @Override
    public void close() {
        closeChart(expensePieChartController);
        closeChart(expenseAreaChartController);
    }

    public void OnMenuFileCloseButton_Click(ActionEvent actionEvent) {
        Platform.exit();
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
        updateCharts();
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
                expenseRepository.save(expense);
                updateExpenses();
                updateCharts();
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
                updateExpenses();
                updateCharts();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
        tableViewExpenses.getSelectionModel().selectFirst();
    }

    public void OnMenuViewPieChartButton_Click(ActionEvent actionEvent) throws IOException {
        closeChart(expensePieChartController);

        Pair<ChartController, Stage> values = openChart("expense-pie-chart-view.fxml");
        expensePieChartController = values.getKey();
        addObserver(expensePieChartController);

        Stage stage = values.getValue();
        stage.setTitle("Expense Pie Chart");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bertons/expensetracker/images/icons/App-icon.png"))));
        stage.setResizable(true);
        stage.setHeight(500);
        stage.setMaxHeight(500);
        stage.setMinHeight(400);
        stage.setMaxWidth(1000);
        stage.setMinWidth(500);
        stage.show();
    }

    public void OnMenuViewAreaChartButton_Click(ActionEvent actionEvent) throws IOException {
        closeChart(expenseAreaChartController);

        Pair<ChartController, Stage> values = openChart("expense-area-chart-view.fxml");
        expenseAreaChartController = values.getKey();
        addObserver(expenseAreaChartController);

        Stage stage = values.getValue();
        stage.setTitle("Expense Area Chart");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bertons/expensetracker/images/icons/App-icon.png"))));
        stage.setResizable(true);
        stage.setHeight(500);
        stage.setMinHeight(300);
        stage.setWidth(1000);
        stage.setMinWidth(330);
        stage.show();
    }

    private Pair<ChartController, Stage> openChart(String viewPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
        Parent root = loader.load();
        ChartController chartController = loader.getController();

        updateExpenses();
        chartController.initCharts(expenses);

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        return new Pair<>(chartController, stage);
    }

    public ObservableList<Expense> getExpenses() {
        return expenses;
    }

    public void OnButtonRefreshData_Click(ActionEvent actionEvent) {
        updateExpenses();
        updateCharts();
    }

    @Override
    public <T, ID> void setRepository(Repository<T, ID> repository) {
        if (repository != null) {
            try {
                this.expenseRepository = (Repository<Expense, Long>) repository;
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid repository type passed.");
            }
        } else {
            throw new IllegalArgumentException("Invalid repository object.");
        }
    }

    @Override
    public void addObserver(ObserverExpense observerExpense) {
        observers.add(observerExpense);
    }

    @Override
    public void removeObserver(ObserverExpense observerExpense) {
        observers.remove(observerExpense);
    }

    @Override
    public void notifyAllObservers(ObservableList<Expense> expenses) {
        for (ObserverExpense observerExpense : observers) {
            observerExpense.update(expenses);
        }
    }
}
