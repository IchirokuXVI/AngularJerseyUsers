package com.ichiroku.jerseyuserapi.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ichiroku.jerseyuserapi.model.CustomDesignOptions;

public class CustomDesignOptionsService extends BaseService {
	public CustomDesignOptionsService() {
		super();
	}
	
	public CustomDesignOptions get(int id) {
		CustomDesignOptions option = new CustomDesignOptions();
		try {
			PreparedStatement pst = con.prepareStatement("SELECT id, name FROM customDesignOptions WHERE id = ?");
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				option.setId(rs.getInt("id"));
				option.setName(rs.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return option;
	}
	
	public List<CustomDesignOptions> getAll() {
		ArrayList<CustomDesignOptions> options = new ArrayList<CustomDesignOptions>();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT id, name, description FROM customDesignOptions");
			while (rs.next()) {
				CustomDesignOptions option = new CustomDesignOptions();
				option.setId(rs.getInt("id"));
				option.setName(rs.getString("name"));
				option.setDescription(rs.getString("description"));
				options.add(option);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return options;
	}
}
