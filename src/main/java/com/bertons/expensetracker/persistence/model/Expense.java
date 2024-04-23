package com.bertons.expensetracker.persistence.model;

import java.time.LocalDateTime;
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

    private Long id;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private ExpenseType category;
    private PayingMethod payingMethod;

    public Expense(Long id, Double amount, LocalDateTime date, String description, ExpenseType category, PayingMethod payingMethod) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.payingMethod = payingMethod;
    }

    public Expense(Double amount, LocalDateTime date, String description, ExpenseType category, PayingMethod payingMethod) {
        this(null, amount, date, description, category, payingMethod);
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseType getCategory() {
        return category;
    }

    public void setCategory(ExpenseType category) {
        this.category = category;
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
                ", date=" + date +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", payingMethod=" + payingMethod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(getId(), expense.getId()) && Objects.equals(getAmount(), expense.getAmount()) && Objects.equals(getDate(), expense.getDate()) && Objects.equals(getDescription(), expense.getDescription()) && getCategory() == expense.getCategory() && getPayingMethod() == expense.getPayingMethod();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getDate(), getDescription(), getCategory(), getPayingMethod());
    }
}
