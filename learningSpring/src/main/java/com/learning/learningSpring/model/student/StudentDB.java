package com.learning.learningSpring.model.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDB {
	private Connection cnx = null;

	public StudentDB() {
		try {
			this.cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudentDB", "chiragagarwals",
					"chiragagarwals");
			System.out.println("DB Connected!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveData(int id, int sRank, String name, int score) throws SQLException {

		String insertQuery = "INSERT INTO Student (id,sRank,name,score) VALUES (?,?,?,?)";
		try (PreparedStatement statement = cnx.prepareStatement(insertQuery)) {

			statement.setInt(1, id);
			statement.setInt(2, sRank);
			statement.setString(3, name);
			statement.setInt(4, score);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void removeData(int id) throws SQLException {
		String deleteQuery = "DELETE FROM Student where id=" + id;
		try (PreparedStatement statement = cnx.prepareStatement(deleteQuery)) {

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateData(int id, int newsRank, String newName, int newScore) throws SQLException {
		String updateQuery = "UPDATE Student SET sRank = ?, name = ?, score = ? WHERE id = ? ORDER BY score DESC";
		try (PreparedStatement statement = cnx.prepareStatement(updateQuery)) {

			statement.setInt(1, newsRank);
			statement.setString(2, newName);
			statement.setInt(3, newScore);
			statement.setInt(4, id);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void clearDB() {
		String clearDBquery = "DELETE FROM Student";
		try (PreparedStatement statement = cnx.prepareStatement(clearDBquery)) {
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet getData() {
		PreparedStatement show;
		try {
			show = cnx.prepareStatement("SELECT * FROM Student ORDER BY score DESC");
			ResultSet rs = show.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
