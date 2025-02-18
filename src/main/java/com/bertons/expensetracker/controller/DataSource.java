package com.bertons.expensetracker.controller;

import com.zaxxer.hikari.HikariDataSource;

public interface DataSource {
    void initDataSource(HikariDataSource hikariDataSource);
}
