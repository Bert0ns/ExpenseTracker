package com.bertons.expensetracker.persistence.dao;

import com.bertons.expensetracker.persistence.model.Expense;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExpenseRepository implements Repository<Expense, Long> {
    private final HikariDataSource dataSource;

    public ExpenseRepository(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        checkTable();
    }

    private void checkTable() {
        String sql = "SELECT * FROM expense LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            System.out.println(rs);
        } catch (SQLException e) {
            // Must be disabled in production!
            initTable();
        }
    }

    private void initTable() {
        String sql = "DROP TABLE IF EXISTS expense;" +
                "CREATE TABLE expense " +
                "(id SERIAL, " +
                "amount MONEY DEFAULT 0, " +
                "transactionDate DATE DEFAULT current_date, " +
                "description VARCHAR(200), " +
                "expenseType VARCHAR(15) DEFAULT 'Miscellaneous'," +
                "payingMethod VARCHAR(5) DEFAULT 'cash', " +
                "CONSTRAINT pk_expense PRIMARY KEY(id))";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Expense getExpenseFromResultSet(ResultSet rs) throws SQLException {
        try
        {
            return new Expense(rs.getLong("id"),
                    rs.getDouble("amount"),
                    rs.getDate("transactionDate"),
                    rs.getString("description"),
                    Expense.getExpenseTypeFromString(rs.getString("expenseType")),
                    Expense.getPayingMethodFromString(rs.getString("payingMethod")));
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<Expense> findById(Long id) {
        String sql = "SELECT * FROM expense WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(getExpenseFromResultSet(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Iterable<Expense> findAll() {
        String sql = "SELECT * FROM expense";
        List<Expense> expenses = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                expenses.add(getExpenseFromResultSet(rs));
            }
            return expenses;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Expense save(Expense entity) {
        if (Objects.isNull(entity.getId())) {
            return insert(entity);
        }

        Optional<Expense> expense = findById(entity.getId());
        if (expense.isEmpty()) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private Expense insert(Expense entity) {
        String sql = "INSERT INTO expense (amount, transactionDate, description, expenseType, payingMethod) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setDouble(1, entity.getAmount());
            statement.setDate(2, entity.getDate());
            statement.setString(3, entity.getDescription());
            statement.setString(4, entity.getExpenseType().toString());
            statement.setString(5, entity.getPayingMethod().toString());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                entity.setId(keys.getLong(1));
                return entity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Expense update(Expense entity) {
        String sql = "UPDATE expense SET amount=?, transactionDate=?, description=?, expenseType=?, payingMethod=? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setDouble(1, entity.getAmount());
            statement.setDate(2, entity.getDate());
            statement.setString(3, entity.getDescription());
            statement.setString(4, entity.getExpenseType().toString());
            statement.setString(5, entity.getPayingMethod().toString());
            statement.setLong(6, entity.getId());
            statement.executeUpdate();

            return entity;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM expense WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM expense";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
}
