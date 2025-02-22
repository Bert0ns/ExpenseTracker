package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.ObservableList;

public interface ObserverExpense {
    void update(ObservableList<Expense> newExpenses);
}
