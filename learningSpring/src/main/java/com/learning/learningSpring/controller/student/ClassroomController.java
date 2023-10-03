package com.learning.learningSpring.controller.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.learningSpring.model.student.Classroom;
import com.learning.learningSpring.model.student.Student;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {

	@Autowired
	private Classroom classroom;

	@GetMapping
	public String listAllStudents(Model model) {
		if (!model.containsAttribute("student"))
			model.addAttribute("student", new Student());
		model.addAttribute("students", classroom.getStudents());
		return "classroom";
	}

	@PostMapping("/add")
	public String addStudent(@Valid Student student, BindingResult bindingResult, RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
			attr.addFlashAttribute("student", student);
			return "redirect:/classroom";
		}
		classroom.add(student);
		return "redirect:/classroom";
	}

	@PostMapping("/delete")
	public String deleteStudent(@RequestParam int id) {
		classroom.remove(id);
		return "redirect:/classroom";
	}
	/*
	 * public String deleteStudent(HttpServletRequest req) {
	 * classroom.remove(Integer.valueOf(req.getParameter("idx"))); return
	 * "redirect:/classroom"; }
	 */

	@GetMapping("/edit")
	public String editStudentForm(@RequestParam int id, Model model) {
		Student student;
		if (!model.containsAttribute("student")) {
			student = classroom.getById(id).get();
			model.addAttribute("student", student);
		}
		return "studentEditForm";
	}

	@PostMapping("/edit")
	public String editStudent(@Valid Student student, BindingResult bindingResult, RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
			attr.addFlashAttribute("student", student);
			attr.addAttribute("id", student.getId());
			return "redirect:/classroom/edit";
		} else {
			classroom.replace(student.getId(), student);
			return "redirect:/classroom";
		}
	}

	@PostMapping("/clearDB")
	public String clearDB() {
		classroom.clear();
		return "redirect:/classroom";
	}

}