package com.bertons.expensetracker.controller;

import com.zaxxer.hikari.HikariDataSource;

public class MainPageViewController {
    private HikariDataSource hikariDataSource;

    public void initDataSource(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        //this.userRepository = new UserRepository(hikariDataSource);

        // insert some example users
        // password are saved in "encrypted" form
        //userRepository.deleteAll();
        //userRepository.save(new User("admin", String.valueOf("admin".hashCode())));
        //userRepository.save(new User("user", String.valueOf("user".hashCode())));
    }
}
