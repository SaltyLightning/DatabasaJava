package com.company;
import java.sql.*;
import java.util.Properties;

public class DatabaseHandle {
	Connection sqlConnect;
	public DatabaseHandle() {
		super();
	}
	
	public boolean connect() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	    Properties connectionProps = new Properties();
		connectionProps.put("user", "northwind");
	    connectionProps.put("password", " ");
	    try {
			sqlConnect = DriverManager.getConnection(
			        "jdbc:mysql://localhost:3306/northwind", connectionProps);
			return true;
		} catch (SQLException e) {
			System.out.println("Failed to connect to database");
			System.out.println(e.getMessage());
			return false;
		}

	}
}
