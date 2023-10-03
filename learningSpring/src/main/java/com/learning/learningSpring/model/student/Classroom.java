package com.learning.learningSpring.model.student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class Classroom {
	private List<Student> students;
	StudentDB db;
	boolean load;

	public Classroom() {
		students = new ArrayList<>();
		db = new StudentDB();
	}

	public List<Student> getStudents() {
		if (students.isEmpty()) {
			load = true;
			students = loadDB();
			rank();
		}
		return Collections.unmodifiableList(students);
	}

	private void rank() {
		Collections.sort(students, (s1, s2) -> -Integer.compare(s1.getScore(), s2.getScore()));
		for (int i = 0; i < students.size(); i++)
			students.get(i).setRank(i + 1);
		for (int i = 1; i < students.size(); i++) {
			if (students.get(i).getScore() == students.get(i - 1).getScore())
				students.get(i).setRank(students.get(i - 1).getRank());
		}
	}

	public void add(Student student) {

		student.setId(updateID());
		students.add(student);
		rank();
		try {
			db.saveData(student.getId(), student.getRank(), student.getName(), student.getScore());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateTable();
	}

	public void remove(int id) {
		Optional<Student> studentToRemove = getById(id);
		students.remove(studentToRemove.get());
		rank();
		try {
			db.removeData(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateTable();
	}

	public void replace(int id, Student current) {
		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).getId() == id) {
				students.get(i).setName(current.getName());
				students.get(i).setScore(current.getScore());
			}
		}
		rank();
		updateTable();
	}

	public Optional<Student> getById(int id) {
		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).getId() == id)
				return Optional.of(students.get(i));
		}
		return Optional.empty();
	}

	public int updateID() {
		int maxId = 0;

		for (Student st : students) {
			int studentId = st.getId();
			if (studentId > maxId) {
				maxId = studentId;
			}
		}
		return maxId + 1;
	}

	public void updateTable() {
		for (Student s : students) {
			try {
				db.updateData(Integer.valueOf(s.getId()), Integer.valueOf(s.getRank()), String.valueOf(s.getName()),
						Integer.valueOf(s.getScore()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void clear() {
		db.clearDB();
		students.removeAll(students);
	}

	public List<Student> loadDB() {
		ResultSet rs = db.getData();
		try {
			while (rs.next()) {
				int id = rs.getInt("id");
				int rank = rs.getInt("sRank");
				String name = rs.getString("name");
				int score = rs.getInt("score");

				Student student = new Student();
				student.setId(id);
				student.setRank(rank);
				student.setName(name);
				student.setScore(score);
				students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return students;
	}

}