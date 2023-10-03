package com.learning.learningSpring.controller.student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.learning.learningSpring.model.student.StudentData;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class StudentController {
	List<StudentData> records = new ArrayList<>();
	String name = "";
	String score = "";

	@GetMapping("/studentrecord")
	public String showStudentForm(Model model) {
		model.addAttribute("record", records);
		return "studentrecord";
	}

	@PostMapping("/studentrecord")
	public String processStudentForm(@RequestParam String Sname, @RequestParam String Sscore, HttpServletResponse resp)
			throws IOException {
		int score = Integer.parseInt(Sscore);
		StudentData newStudent = new StudentData(records.size() + 1, Sname, score);
		records.add(newStudent);
		records.sort(Comparator.comparingInt(StudentData::getScore).reversed());
		for (int i = 0; i < records.size(); i++) {
			StudentData student = records.get(i);
			student.setRank(i + 1);
		}
		for (int i = 1; i < records.size(); i++) {
			StudentData prevStudent = records.get(i - 1);
			StudentData currStudent = records.get(i);
			int curr = prevStudent.getScore();
			int prev = currStudent.getScore();
			if (curr == prev) {
				currStudent.setRank(prevStudent.getRank());
				records.set(i, currStudent);
			}
		}
		return "redirect:/studentrecord";
	}

	@PostMapping("/deleteStudent")
	public String deleteStudent(@RequestParam("index") int index) {
		if (index >= 0 && index < records.size()) {
			records.remove(index);
		}
		records.sort(Comparator.comparingInt(StudentData::getScore).reversed());
		for (int i = 0; i < records.size(); i++) {
			StudentData currentStudent = records.get(i);
			currentStudent.setRank(i + 1);
		}
		for (int i = 1; i < records.size(); i++) {
			StudentData prevStudent = records.get(i - 1);
			StudentData currStudent = records.get(i);
			int curr = prevStudent.getScore();
			int prev = currStudent.getScore();
			if (curr == prev) {
				currStudent.setRank(prevStudent.getRank());
				records.set(i, currStudent);
			}
		}
		return "redirect:/studentrecord";
	}

	@PostMapping("/editStudent")
	public String editStudent(@RequestParam("index") int index, Model model) {
		if (index >= 0 && index < records.size()) {
			StudentData student = records.get(index);
			model.addAttribute("student", student);
			model.addAttribute("index", index);
		}

		return "editStudent";
	}

	@PostMapping("/saveEdit")
	public String saveEdit(@RequestParam("index") int index, @RequestParam("editedName") String editedName,
			@RequestParam("editedScore") int editedScore) {
		if (index >= 0 && index < records.size()) {
			StudentData student = records.get(index);
			student.setName(editedName);
			student.setScore(editedScore);

			records.sort(Comparator.comparingInt(StudentData::getScore).reversed());
			for (int i = 0; i < records.size(); i++) {
				StudentData currentStudent = records.get(i);
				currentStudent.setRank(i + 1);
			}
			for (int i = 1; i < records.size(); i++) {
				StudentData prevStudent = records.get(i - 1);
				StudentData currStudent = records.get(i);
				int curr = prevStudent.getScore();
				int prev = currStudent.getScore();
				if (curr == prev) {
					currStudent.setRank(prevStudent.getRank());
					records.set(i, currStudent);
				}
			}
		}

		return "redirect:/studentrecord";
	}

}
