package com.bertons.expensetracker.persistence.model;

import java.sql.Date;
import java.util.Objects;

public class Expense {
    public enum ExpenseType {
        Miscellaneous,
        Subscription,
        EatingOut,
        Groceries,
        Debt,
        Car
    }
    public enum PayingMethod {
        Card,
        Cash
    }

    public static ExpenseType getExpenseTypeFromString(String expenseType) {
        return switch (expenseType) {
            case "Miscellaneous" -> ExpenseType.Miscellaneous;
            case "Subscription" -> ExpenseType.Subscription;
            case "EatingOut" -> ExpenseType.EatingOut;
            case "Groceries" -> ExpenseType.Groceries;
            case "Debt" -> ExpenseType.Debt;
            case "Car" -> ExpenseType.Car;
            default -> null;
        };
    }

    public static PayingMethod getPayingMethodFromString(String payingMethod) {
        return switch (payingMethod) {
            case "Card" -> PayingMethod.Card;
            case "Cash" -> PayingMethod.Cash;
            default -> null;
        };
    }

    private Long id;
    /**Expense value in €, EUR*/
    private Double amount;
    private Date date;
    /**Max Lenght 200 characters (sql constraint)*/
    private String description;
    private ExpenseType expenseType;
    private PayingMethod payingMethod;

    public Expense(Long id, Double amount, Date date, String description, ExpenseType expenseType, PayingMethod payingMethod) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.expenseType = expenseType;
        this.payingMethod = payingMethod;
    }

    public Expense(Double amount, Date date, String description, ExpenseType expenseType, PayingMethod payingMethod) {
        this(null, amount, date, description, expenseType, payingMethod);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    public PayingMethod getPayingMethod() {
        return payingMethod;
    }

    public void setPayingMethod(PayingMethod payingMethod) {
        this.payingMethod = payingMethod;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", date=" + date.toLocalDate() +
                ", description='" + description + '\'' +
                ", category=" + expenseType +
                ", payingMethod=" + payingMethod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(getId(), expense.getId()) && Objects.equals(getAmount(), expense.getAmount()) && Objects.equals(getDate(), expense.getDate()) && Objects.equals(getDescription(), expense.getDescription()) && getExpenseType() == expense.getExpenseType() && getPayingMethod() == expense.getPayingMethod();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getDate(), getDescription(), getExpenseType(), getPayingMethod());
    }
}
