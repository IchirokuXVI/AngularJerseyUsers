package com.ichiroku.jerseyuserapi.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ichiroku.jerseyuserapi.model.CustomDesignOptions;
import com.ichiroku.jerseyuserapi.model.RefreshToken;
import com.ichiroku.jerseyuserapi.model.User;
import com.ichiroku.jerseyuserapi.util.Util;

public class UserService extends BaseService {
	
	public UserService() {
		super();
	}
	
	public boolean create(User user) {
		try {
			if (getUser(user.getUsername()) != null) {
				return false;
			}
			PreparedStatement pst = con.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)");
			pst.setString(1, user.getUsername());
			pst.setString(2, user.getPassword());
			pst.execute();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public User getUser(String username) {
		User user = null;
		try {
			PreparedStatement pst = con.prepareStatement("SELECT * FROM user WHERE username=?");
			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt(("id")));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
	public User getUser(int id) {
		User user = null;
		try {
			PreparedStatement pst = con.prepareStatement("SELECT * FROM user WHERE id=?");
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
	public User getUser(String username, String password) throws Exception {
		PreparedStatement pst = con.prepareStatement("SELECT * FROM user WHERE username=? AND password=?");
		pst.setString(1, username);
		pst.setString(2, Util.digest(password));
		ResultSet rs = pst.executeQuery();
		if (!rs.next()) {
			throw new Exception("Username/password are invalid");
		}
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		return user;
	}
	
	public List<CustomDesignOptions> getOptions(User user) {
		ArrayList<CustomDesignOptions> options = new ArrayList<CustomDesignOptions>();
		try {
			// Description isn't important so it is not being queried
			PreparedStatement pst = con.prepareStatement("SELECT opt.id id, name FROM user_customdesignoptions useropt JOIN customdesignoptions opt ON useropt.option_id=opt.id  WHERE user_id=?");
			pst.setInt(1, user.getId());
			ResultSet rs = pst.executeQuery();
			options = new ArrayList<CustomDesignOptions>();
			while(rs.next()) {
				CustomDesignOptions option = new CustomDesignOptions();
				option.setId(rs.getInt("id"));
				option.setName(rs.getString("name"));
				
				options.add(option);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return options;
	}
	
	public List<RefreshToken> getRefreshTokens(User user) {
		List<RefreshToken> tokens = null;
		try {
			PreparedStatement pst = con.prepareStatement("SELECT signature, revoked FROM refresh_token WHERE user_id=?");
			pst.setInt(1, user.getId());
			ResultSet rs = pst.executeQuery();
			tokens = new ArrayList<RefreshToken>();
			while (rs.next()) {
				RefreshToken token = new RefreshToken();
				token.setSignature(rs.getString("signature"));
				token.setRevoked(rs.getBoolean("revoked"));
				tokens.add(token);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;
	}
	
	public void updateOptions(User user, List<CustomDesignOptions> options) throws Exception {
		if (options.size() > 0) {
			String questionMarks = "?";
			//System.out.println(options.size());
			for (int i = 1; i < options.size(); i++) { //Starts index at 1 because the first question mark is already in the string
				questionMarks += ",?";
			}
			//System.out.println("DELETE FROM user_customdesignoptions WHERE user_id=? AND option_id NOT IN (" + questionMarks + ")");
			PreparedStatement pst = con.prepareStatement("DELETE FROM user_customdesignoptions WHERE user_id=? AND option_id NOT IN (" + questionMarks + ")");
			pst.setInt(1, user.getId());
			ArrayList<Integer> optionsId = new ArrayList<Integer>();
			for (int i = 0; i < options.size(); i++) {
				pst.setInt(i+2, options.get(i).getId()); // Statement parameters starts at 1 and "i" starts at 0, first parameter was already set with user id, thats why I sum 2 to "i"
			}
			
			for (CustomDesignOptions option : options) {
				optionsId.add(option.getId());
			}
			// Array array = con.createArrayOf("INTEGER", optionsId.toArray()); //MySQL doesn't support arrays
			pst.execute();
			pst.close();
			pst = con.prepareStatement("INSERT IGNORE INTO user_customdesignoptions (user_id, option_id) VALUES (?, ?)");
			pst.setInt(1, user.getId());
			for (CustomDesignOptions option : options) {
				pst.setInt(2, option.getId());
				pst.execute();
			}
		} else {
			PreparedStatement pst = con.prepareStatement("DELETE FROM user_customdesignoptions WHERE user_id=?");
			pst.setInt(1, user.getId());
			pst.execute();
		}
	}
	
	public boolean updateUser(User user) {
		try {
			PreparedStatement pst = con.prepareStatement("UPDATE user SET username=?, password=? WHERE id=?");
			pst.setString(1, user.getUsername());
			pst.setString(2, user.getPassword());
			pst.setInt(3, user.getId());
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteUser(User user) {
		try {
			PreparedStatement pst = con.prepareStatement("DELETE FROM user WHERE id=?");
			pst.setLong(1, user.getId());
			return pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
}
