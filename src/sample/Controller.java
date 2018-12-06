package sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {

  private static int recordNumber = 1;
  final static String DATABASE_URL = "jdbc:derby:lib\\books";

  @FXML
  private Label lblFirst;

  @FXML
  private Label lblLast;

  @FXML
  private TextField txtFirst;

  @FXML
  private TextField txtLast;

  @FXML
  void createRecord(ActionEvent event) {
    String INSERT_QUERY = String.format("INSERT INTO AUTHORS(firstName, lastName)"
        + " VALUES ('%s', '%s')", txtFirst.getText(), txtLast.getText());
    // https://www.tutorialspoint.com/jdbc/preparestatement-object-example.htm
    connectAndExecute(INSERT_QUERY);
  }

  @FXML
  void deleteRecord(ActionEvent event) {
    String DELETE_QUERY = String.format("DELETE FROM AUTHORS WHERE FIRSTNAME = '%s' AND "
        + "LASTNAME = '%s'", lblFirst.getText(), lblLast.getText());
    connectAndExecute(DELETE_QUERY);
  }

  @FXML
  void nextRecord(ActionEvent event) {
    final String SELECT_QUERY =
        "SELECT authorID, firstName, lastName FROM authors";
    recordNumber++;

    // https://stackoverflow.com/questions/4085420/how-do-i-read-a-properties-file-and-connect-a-mysql-database
    Properties props = new Properties();
    http://tutorials.jenkov.com/java-exception-handling/try-with-resources.html
    try (FileInputStream in = new FileInputStream("dir/db.properties")) {
      props.load(in);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String username = props.getProperty("jdbc.username");
    String password = props.getProperty("jdbc.password");

    // use try-with-resources to connect to and query the database
    try (
        Connection connection = DriverManager.getConnection(DATABASE_URL, username, password);
        Statement statement = connection
            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {
      if (resultSet.next()) {
        resultSet.absolute(recordNumber);
      }

      if (resultSet.next()) {
        lblFirst.setText(resultSet.getString(2));
        lblLast.setText(resultSet.getString(3));
      } else {
        resultSet.first();
        recordNumber = 1;
        lblFirst.setText(resultSet.getString(2));
        lblLast.setText(resultSet.getString(3));
      }
    } // AutoCloseable objects' close methods are called now
    catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }
  }

  @FXML
  void updateRecord(ActionEvent event) {
    String UPDATE_QUERY = String.format("UPDATE AUTHORS SET FIRSTNAME = '%s', LASTNAME = '%s' "
        + "WHERE FIRSTNAME = '%s' AND LASTNAME = '%s'", txtFirst.getText(), txtLast.getText(),
        lblFirst.getText(), lblLast.getText());
    connectAndExecute(UPDATE_QUERY);
  }

  void connectAndExecute(String query) {
    Properties props = new Properties();
    try (FileInputStream in = new FileInputStream("dir/db.properties")) {
      props.load(in);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String username = props.getProperty("jdbc.username");
    String password = props.getProperty("jdbc.password");

    // use try-with-resources to connect to and query the database
    // https://stackoverflow.com/questions/8066501/how-should-i-use-try-with-resources-with-jdbc
    // https://stackoverflow.com/questions/24932262/java-prepared-statement-in-try-with-resources-not-working
    try (Connection connection = DriverManager.getConnection(DATABASE_URL, username, password);
        Statement statement = connection.createStatement();) {
      try {
        statement.execute(query);
      }
      // AutoCloseable objects' close methods are called now
      catch (Exception ex) {
        ex.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }
}
