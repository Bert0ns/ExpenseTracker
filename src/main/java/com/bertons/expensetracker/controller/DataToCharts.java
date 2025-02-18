package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.ObservableList;

public interface DataToCharts {
    void initCharts(ObservableList<Expense> expensesData, MainPageViewController mainPageViewController);
    void updateCharts(ObservableList<Expense> expensesData);
}
