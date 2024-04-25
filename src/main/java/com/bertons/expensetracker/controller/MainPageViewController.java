package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.dao.ExpenseRepository;
import com.bertons.expensetracker.persistence.model.Expense;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MainPageViewController {
    private HikariDataSource hikariDataSource;
    private ExpenseRepository expenseRepository;
    @FXML
    private TableView<Expense> tableViewExpense;

    public void initDataSource(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        this.expenseRepository = new ExpenseRepository(hikariDataSource);

        // insert some example users
        // password are saved in "encrypted" form
        expenseRepository.deleteAll();
        //userRepository.save(new User("admin", String.valueOf("admin".hashCode())));
        //userRepository.save(new User("user", String.valueOf("user".hashCode())));
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


         //cols.get(0).setText("NON VAA");
    }
}
