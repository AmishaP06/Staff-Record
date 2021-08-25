package com.test.main;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeMain extends Application implements Initializable {

	@FXML
	private TextField txtid, txtfirstname, txtlastname, txtaddress, txtcity, txttelephone, txtmiddlename;

	@FXML
	private Button btnview, btninsert, btnupdate, btndelete, btnclear;

	@FXML
	private Label lblavailableid;

	@FXML
	private ComboBox<String> stateCombo;

	ObservableList<String> states = FXCollections.observableArrayList("ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC",
			"WA");

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/com/test/fxml/Home.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void doView() {
		if (StringUtils.isBlank(txtid.getText()))
			showAlert(AlertType.WARNING, "Please enter your ID");
		else
			new JDBC().update();
	}

	public void doGetRecord() {
		idPlay();
	}

	public void idPlay() {
		lblavailableid.setText("Available Id to Insert : " + (new JDBC()).selectFreeId());
		if (StringUtils.isBlank(txtid.getText())) {
			btninsert.setDisable(false);
			btnupdate.setDisable(false);
			btndelete.setDisable(false);
		} else {
			if ((new JDBC()).select()) {
				btnupdate.setDisable(false);
				btndelete.setDisable(false);
				btninsert.setDisable(true);
				lblavailableid.setText("Record of " + txtfirstname.getText() + " " + txtmiddlename.getText() + " "
						+ txtlastname.getText());
				lblavailableid.setWrapText(true);
			} else {
				btnupdate.setDisable(false);
				btndelete.setDisable(false);
				btninsert.setDisable(true);
				clearContentsLeaveId();
				showAlert(AlertType.INFORMATION, "No record exist for the given ID");
			}
		}
	}

	public void doInsert() {
		if (validateDataAndInsert()) {
			new JDBC().insert();
		}
	}

	private boolean validateDataAndInsert() {
		if (StringUtils.isBlank(txtfirstname.getText())) {
			showAlert(AlertType.WARNING, "Please enter first name");
			return false;
		}
		if (StringUtils.isBlank(txtmiddlename.getText())) {
			showAlert(AlertType.WARNING, "Please enter middle name");
			return false;
		}
		if (StringUtils.isBlank(txtlastname.getText())) {
			showAlert(AlertType.WARNING, "Please enter last name");
			return false;
		}
		if (StringUtils.isBlank(txttelephone.getText())) {
			showAlert(AlertType.WARNING, "Please enter telephone number");
			return false;
		}
		if (StringUtils.isBlank(txtaddress.getText())) {
			showAlert(AlertType.WARNING, "Please enter address");
			return false;
		}
		if (StringUtils.isBlank(txtcity.getText())) {
			showAlert(AlertType.WARNING, "Please enter city");
			return false;
		}
		String state = stateCombo.getSelectionModel().getSelectedItem();
		if (StringUtils.isBlank(state)) {
			showAlert(AlertType.WARNING, "Please select state");
			return false;
		}
		return true;
	}

	private void showAlert(AlertType alertType, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(alertType.name());
		alert.setHeaderText(alertType.name());
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void doUpdate() {
		if (validateDataAndInsert()) {
			if (StringUtils.isBlank(txtid.getText()))
				showAlert(AlertType.WARNING, "Please enter your ID");
			else
				new JDBC().update();
		}
	}

	public void doDelete() {
		if (StringUtils.isBlank(txtid.getText()))
			showAlert(AlertType.WARNING, "Please select state");
		else
			new JDBC().delete();
	}

	public void doClear() {
		clearContentsLeaveId();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		stateCombo.setItems(states);
		idPlay();
	}

	public void clearContentsLeaveId() {
		txtfirstname.setText("");
		txtlastname.setText("");
		txtmiddlename.setText("");
		txtaddress.setText("");
		txtcity.setText("");
//		.setSelectedIndex(0);
		stateCombo.getSelectionModel().select(0);
		txttelephone.setText("");
	}

	class JDBC {
		String query;
		// JDBC driver name and database URL
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc:mysql://localhost:3306/task1";

		// Database credentials
		static final String USER = "root";
		static final String PASS = "root";

		public int insert() {
			Connection conn = null;
			Statement stmt = null;
			try {
				// STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				// STEP 3: Open a connection
				System.out.println("Connecting to a selected database...");
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				System.out.println("Connected database successfully...");

				// STEP 4: Execute a query
				System.out.println("Inserting records into the table...");
				stmt = conn.createStatement();

				String sql = "INSERT INTO staff(ln,fn,mi,address,city,state,phone) " + "VALUES ('"
						+ txtlastname.getText() + "','" + txtfirstname.getText() + "','" + txtmiddlename.getText()
						+ "','" + txtaddress.getText() + "','" + txtcity.getText() + "','"
						+ stateCombo.getSelectionModel().getSelectedItem() + "','" + txttelephone.getText() + "')";
				int numOfRowsAffected = stmt.executeUpdate(sql);
				if (numOfRowsAffected > 0)
					showAlert(AlertType.INFORMATION, "Record inserted into the table...");
				else
					showAlert(AlertType.ERROR, "Something went wrong");

			} catch (SQLException se) {
				// Handle errors for JDBC
				System.out.println(se.getMessage());
			} catch (Exception e) {
				// Handle errors for Class.forName
				System.out.println(e.getMessage());
			} finally {
				// finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				} catch (SQLException se) {
				} // do nothing
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} // end finally try
			} // end try
			return 0;
		}

		public int update() {
			Connection conn = null;
			Statement stmt = null;
			try {
				// STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				// STEP 3: Open a connection
				System.out.println("Connecting to a selected database...");
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				System.out.println("Connected database successfully...");

				// STEP 4: Execute a query
				System.out.println("Creating statement...");
				stmt = conn.createStatement();
				String sql = "UPDATE staff " + "SET  ln='" + txtlastname.getText() + "',fn='" + txtfirstname.getText()
						+ "',mi='" + txtmiddlename.getText() + "',address='" + txtaddress.getText() + "',city='"
						+ txtcity.getText() + "',state='" + stateCombo.getSelectionModel().getSelectedItem()
						+ "',phone='" + txttelephone.getText() + "' where id=" + txtid.getText();
				int numOfRowsAffected = stmt.executeUpdate(sql);
				if (numOfRowsAffected > 0)
					showAlert(AlertType.INFORMATION, "Recrod updated successfully!");
				else
					showAlert(AlertType.ERROR, "Something went wrong");
				/*
				 * // Now you can extract all the records // to see the updated records sql =
				 * "SELECT id, first, last, age FROM Registration"; ResultSet rs =
				 * stmt.executeQuery(sql);
				 * 
				 * while (rs.next()) { // Retrieve by column name int id = rs.getInt("id"); int
				 * age = rs.getInt("age"); String first = rs.getString("first"); String last =
				 * rs.getString("last");
				 * 
				 * // Display values System.out.print("ID: " + id); System.out.print(", Age: " +
				 * age); System.out.print(", First: " + first); System.out.println(", Last: " +
				 * last); } rs.close();
				 */
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				// Handle errors for Class.forName
				e.printStackTrace();
			} finally {
				// finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				} catch (SQLException se) {
				} // do nothing
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} // end finally try
			} // end try
			return 0;
		}

		public int delete() {
			Connection conn = null;
			Statement stmt = null;
			try {
				// STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				// STEP 3: Open a connection
				System.out.println("Connecting to a selected database...");
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				System.out.println("Connected database successfully...");

				// STEP 4: Execute a query
				System.out.println("Creating statement...");
				stmt = conn.createStatement();
				String sql = "DELETE FROM Staff " + "WHERE id = " + txtid.getText();
				stmt.executeUpdate(sql);
				showAlert(AlertType.INFORMATION, "Record Deleted");
				// Now you can extract all the records
				// to see the remaining records
				/*
				 * sql = "SELECT id, first, last, age FROM Registration"; ResultSet rs =
				 * stmt.executeQuery(sql);
				 * 
				 * while (rs.next()) { // Retrieve by column name int id = rs.getInt("id"); int
				 * age = rs.getInt("age"); String first = rs.getString("first"); String last =
				 * rs.getString("last");
				 * 
				 * // Display values System.out.print("ID: " + id); System.out.print(", Age: " +
				 * age); System.out.print(", First: " + first); System.out.println(", Last: " +
				 * last); } rs.close();
				 */
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				// Handle errors for Class.forName
				e.printStackTrace();
			} finally {
				// finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				} catch (SQLException se) {
				} // do nothing
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} // end finally try
			} // end try
			lblavailableid.setText("Available Id to Insert : " + (new JDBC()).selectFreeId());
			clearContentsLeaveId();
			return 0;
		}

		public boolean select() {
			boolean gotData = false;
			Connection conn = null;
			Statement stmt = null;
			try {
				// STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				// STEP 3: Open a connection
				System.out.println("Connecting to database...");
				conn = DriverManager.getConnection(DB_URL, USER, PASS);

				// STEP 4: Execute a query
				System.out.println("Creating statement...");
				stmt = conn.createStatement();
				String sql;
				sql = "SELECT ln,fn,mi,address,city,state,phone FROM staff where id=" + txtid.getText();
				ResultSet rs = stmt.executeQuery(sql);

				// STEP 5: Extract data from result set
				while (rs.next()) {
					gotData = true;
					// Retrieve by column name
					txtlastname.setText(rs.getString(1));
					txtfirstname.setText(rs.getString(2));
					txtmiddlename.setText(rs.getString(3));
					txtaddress.setText(rs.getString(4));
					txtcity.setText(rs.getString(5));
					stateCombo.getSelectionModel().select(rs.getString(6));
					txttelephone.setText(rs.getString(7));
				}
				// STEP 6: Clean-up environment
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				// Handle errors for Class.forName
				e.printStackTrace();
			} finally {
				// finally block used to close resources
				try {
					if (stmt != null)
						stmt.close();
				} catch (SQLException se2) {
				} // nothing we can do
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} // end finally try
			} // end try
				// return new String[0][0];
			return gotData;
		}

		public int selectFreeId() {
			int id = 0;
			Connection conn = null;
			Statement stmt = null;
			try {
				// STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				// STEP 3: Open a connection
				System.out.println("Connecting to database...");
				conn = DriverManager.getConnection(DB_URL, USER, PASS);

				// STEP 4: Execute a query
				System.out.println("Creating statement...");
				stmt = conn.createStatement();
				String sql;
				sql = "SELECT max(id) from staff";
				ResultSet rs = stmt.executeQuery(sql);

				// STEP 5: Extract data from result set
				while (rs.next()) {
					id = Integer.parseInt(rs.getString(1));
				}
				// STEP 6: Clean-up environment
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				// Handle errors for Class.forName
				e.printStackTrace();
			} finally {
				// finally block used to close resources
				try {
					if (stmt != null)
						stmt.close();
				} catch (SQLException se2) {
				} // nothing we can do
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} // end finally try
			} // end try
				// return new String[0][0];
			return id + 1;
		}
	}
}
