package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.model.Todo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oTodoDaoTest {
    private Sql2oTodoDao dao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:test;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString,"","");
        dao = new Sql2oTodoDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingTodoSetsId() throws Exception {
        Todo todo = newTodo();
        int originalId = todo.getId();

        dao.add(todo);

        assertNotEquals(todo.getId(), originalId);
    }

    @Test
    public void canReturnTodoFromId() throws Exception {
        Todo todo = newTodo();
        dao.add(todo);
        
        Todo foundTodo = dao.findById(todo.getId());
        
        assertEquals(foundTodo, todo);
    }

    @Test
    public void findAllReturnsAll() throws Exception {
        dao.add(new Todo("test2"));
        dao.add(newTodo());

        assertEquals(2, dao.findAll().size());
    }

    @Test
    public void noTodosReturnsNothingWhenFindAllIsCalled() throws Exception {
        assertEquals(0, dao.findAll().size());
    }

    @Test
    public void todosCanBeUpdated() throws Exception {
        Todo todo = newTodo();
        dao.add(todo);

        todo.setName("new name");
        dao.save(todo);

        assertEquals("new name", dao.findById(todo.getId()).getName());
    }

    @Test
    public void deletingDeletesFromDao() throws Exception {
        Todo todo = newTodo();
        dao.add(todo);

        dao.delete(todo.getId());

        assertEquals(0, dao.findAll().size());
    }

    private Todo newTodo() {
        return new Todo("Test");
    }
}