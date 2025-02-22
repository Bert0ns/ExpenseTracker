package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.collections.ObservableList;

public interface ObservableExpense {
    void addObserver(ObserverExpense observerExpense);
    void removeObserver(ObserverExpense observerExpense);
    void notifyAllObservers(ObservableList<Expense> expenses);
}
