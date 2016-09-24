package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.exceptions.DaoException;
import com.teamtreehouse.techdegrees.model.Todo;

import java.util.List;

public interface TodoDao {
    Todo findById(int id);
    List<Todo> findAll();
    void add(Todo todo) throws DaoException;
    void save(Todo todo) throws DaoException;
    void delete(int id) throws DaoException;
}
