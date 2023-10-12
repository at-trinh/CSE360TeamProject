package me.th4.effortlogger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        try {
            initDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initDatabase() throws SQLException {
        String jdbcUrl = "jdbc:h2:./effortloggerdatabase";
        Connection connection = DriverManager.getConnection(jdbcUrl);
        System.out.println("Connected!");
    }
}