package com.teamtreehouse.techdegrees;

import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.model.Todo;
import com.teamtreehouse.techdegrees.testing.ApiClient;
import com.teamtreehouse.techdegrees.testing.ApiResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AppTest {
    public static final String PORT = "4568";
    public static final String TEST_DATASOURCE = "jdbc:h2:mem:test";
    private Sql2oTodoDao dao;
    private Sql2o sql2o;
    private Connection con;
    private ApiClient client;
    private Gson gson;

    @BeforeClass
    public static void StartServer() throws Exception {
        String[] args = {PORT, TEST_DATASOURCE};
        App.main(args);
    }

    @AfterClass
    public static void StopServer() throws Exception {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        dao = new Sql2oTodoDao(sql2o);
        con = sql2o.open();
        client = new ApiClient("http://localhost:" + PORT);
        gson = new Gson();
    }

    @After
    public void tearDown() throws Exception {
        con.close();
    }

    @Test
    public void requestingTodosReturnsAll() throws Exception {
        dao.add(new Todo("Add a todo"));
        dao.add(new Todo("Add another todo"));

        ApiResponse res = client.request("GET", "/api/v1/todos");
        Todo[] todos = gson.fromJson(res.getBody(), Todo[].class);

        assertEquals(2, todos.length);
    }

    @Test
    public void addingATodoReturnsACreatedStatus() throws Exception {
        Todo todo = new Todo("test");

        ApiResponse res = client.request("POST", "/api/v1/todos", gson.toJson(todo));

        assertEquals(201, res.getStatus());
    }

    @Test
    public void postingTodoSavesToDao() throws Exception {
        Todo todo = new Todo("test");

        client.request("POST", "/api/v1/todos", gson.toJson(todo));

        assertEquals(1, dao.findAll().size());
    }

    @Test
    public void PUTtingTodoChangesNameAndEdited() throws Exception {
        Todo todo = new Todo("test");
        dao.add(todo);
        Map<String, Object> newTodo = new HashMap<>();
        newTodo.put("name", "new name");
        newTodo.put("completed", true);

        client.request("PUT", String.format("/api/v1/todos/%d",todo.getId()), gson.toJson(newTodo));

        assertArrayEquals(new Object[]{"new name", true}, new Object[]{dao.findById(todo.getId()).getName(), dao.findById(todo.getId()).isCompleted()});
    }

    @Test
    public void deletePathDeletesProperTodo() throws Exception {
        Todo todo = new Todo("test");
        dao.add(todo);

        client.request("DELETE", String.format("/api/v1/todos/%d", todo.getId()));

        assertEquals(dao.findAll().size(), 0);
    }

    @Test
    public void deletingReturns204StatusCode() throws Exception {
        Todo todo = new Todo("test");
        dao.add(todo);

        ApiResponse res = client.request("DELETE", String.format("/api/v1/todos/%d", todo.getId()));

        assertEquals(204, res.getStatus());
    }

    @Test
    public void deletingReturnsEmptyBody() throws Exception {
        Todo todo = new Todo("test");
        dao.add(todo);

        ApiResponse res = client.request("DELETE", String.format("/api/v1/todos/%d", todo.getId()));

        assertEquals("", res.getBody());
    }
}