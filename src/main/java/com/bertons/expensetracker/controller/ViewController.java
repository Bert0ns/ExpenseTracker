package com.bertons.expensetracker.controller;

import com.bertons.expensetracker.persistence.dao.Repository;

import java.io.Closeable;

public interface ViewController extends DataSource, Closeable {
     <T, ID> void setRepository(Repository<T, ID> repository);
}
