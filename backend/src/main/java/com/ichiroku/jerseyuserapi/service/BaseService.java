package com.ichiroku.jerseyuserapi.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseService {
	
	private final String URL = "jdbc:mysql://localhost/jerseyuserapi";
	private final String USERNAME = "root";
	private final String PASSWORD = "";
	protected Connection con;
	
	public BaseService() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
