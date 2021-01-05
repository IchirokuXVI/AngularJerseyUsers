package com.ichiroku.jerseyuserapi.service;

import com.ichiroku.jerseyuserapi.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ichiroku.jerseyuserapi.model.RefreshToken;

public class RefreshTokenService extends BaseService {

	public RefreshTokenService() {
		super();
	}
	
	public boolean create(RefreshToken token, User user) {
		try {
			PreparedStatement pst = con.prepareStatement("INSERT INTO refresh_token (token_signature, user_id) VALUES (?, ?)"); //Revoked is false by default
			pst.setString(1, token.getSignature());
			pst.setInt(2, user.getId());
			return pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isValid(RefreshToken token) {
		try {
			PreparedStatement pst = con.prepareStatement("SELECT * FROM refresh_token WHERE token_signature=? AND revoked=0");
			pst.setString(1, token.getSignature());
			ResultSet rs = pst.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void revoke(RefreshToken token) {
		try {
			PreparedStatement pst = con.prepareStatement("UPDATE refresh_token SET revoked=1 WHERE token_signature=?");
			pst.setString(1, token.getSignature());
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public User getUser(RefreshToken token) {
		User user = null;
		try {
			PreparedStatement pst = con.prepareStatement("SELECT u.id, u.username FROM refresh_token r JOIN user u ON r.user_id=u.id WHERE token_signature=?");
			pst.setString(1, token.getSignature());
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("username"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
}
