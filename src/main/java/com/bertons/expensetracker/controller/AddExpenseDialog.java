package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.model.Expense;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.io.IOException;

public class AddExpenseDialog extends Dialog<Expense> {

    @FXML private TextField textFieldAmount;
    @FXML private TextField textFieldDescription;
    @FXML private DatePicker datePickerExpenseDate;
    @FXML private ComboBox<Expense.ExpenseType> comboBoxExpenseType;
    @FXML private ComboBox<Expense.PayingMethod> comboBoxPayingMethod;

    public void initialize() {
        comboBoxExpenseType.getItems().removeAll();
        comboBoxExpenseType.getItems().addAll(Expense.ExpenseType.values());
        comboBoxExpenseType.getSelectionModel().select(Expense.ExpenseType.Miscellaneous);

        comboBoxPayingMethod.getItems().removeAll();
        comboBoxPayingMethod.getItems().addAll(Expense.PayingMethod.values());
        comboBoxPayingMethod.getSelectionModel().select(Expense.PayingMethod.Cash);
    }

    public AddExpenseDialog() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("add-expense-view.fxml"));
        loader.setController(this);
        setDialogPane(loader.load());
        setTitle("Add Expense");
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                return new Expense(Double.parseDouble(textFieldAmount.getText()),
                        datePickerExpenseDate.getValue(),
                        textFieldDescription.getText(),
                        comboBoxExpenseType.getValue(),
                        comboBoxPayingMethod.getValue());
            }
            return null;
        });
    }
}
