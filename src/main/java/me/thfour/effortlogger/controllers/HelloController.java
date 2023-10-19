package me.thfour.effortlogger.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private TableView<String> table = new TableView<String>();

    public void initDatabase() throws SQLException {
        String jdbcUrl = "jdbc:h2:./effortloggerdatabase";
        Connection connection = DriverManager.getConnection(jdbcUrl);
        System.out.println("Connected!");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}